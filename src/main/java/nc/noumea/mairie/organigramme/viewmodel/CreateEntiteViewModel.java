package nc.noumea.mairie.organigramme.viewmodel;

/*
 * #%L
 * Logiciel de Gestion des Organigrammes de la Ville de Nouméa
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2015 Mairie de Nouméa
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.organigramme.core.services.AuthentificationService;
import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;
import nc.noumea.mairie.organigramme.core.viewmodel.AbstractPopupViewModel;
import nc.noumea.mairie.organigramme.core.ws.AdsWSConsumer;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.dto.ProfilAgentDto;
import nc.noumea.mairie.organigramme.dto.ReturnMessageDto;
import nc.noumea.mairie.organigramme.dto.TypeEntiteDto;
import nc.noumea.mairie.organigramme.enums.Statut;
import nc.noumea.mairie.organigramme.services.CouleurTypeEntiteService;
import nc.noumea.mairie.organigramme.services.ReturnMessageService;
import nc.noumea.mairie.organigramme.services.TypeEntiteService;

import org.apache.commons.lang.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Li;
import org.zkoss.zhtml.Ul;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Window;

@Init(superclass = true)
@VariableResolver(DelegatingVariableResolver.class)
public class CreateEntiteViewModel extends AbstractPopupViewModel<EntiteDto> implements Serializable {

	private static final long serialVersionUID = 1L;

	// @formatter:off
	@WireVariable
	AdsWSConsumer adsWSConsumer;
	@WireVariable
	CouleurTypeEntiteService couleurTypeEntiteService;
	@WireVariable
	ReturnMessageService returnMessageService;
	@WireVariable
	AuthentificationService authentificationService;
	@WireVariable
	TypeEntiteService typeEntiteService;

	// @formatter:on

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		setPopup((Window) Selectors.iterable(view, "#createEntite").iterator().next());
	}

	@GlobalCommand
	@NotifyChange("entity")
	public void ouvrePopupEditionEntiteDto(@BindingParam("entity") EntiteDto entiteDto) {
		setEntity(entiteDto);
		getPopup().doModal();
	}

	/**
	 * Crée une entité en fonction de ce qui a été indiqué dans la popup de
	 * création. L'ajoute à l'arbre DTO représenté par l'{@link EntiteDto}
	 * "entiteDtoRoot" et l'ajoute aussi à l'arborescence {@link Ul}/{@link Li}
	 * qui est utilisée pour rafraîchir l'arbre en retour de cette méthode
	 */
	@Command
	@NotifyChange("entity")
	public void save() {

		ProfilAgentDto profilAgentDto = authentificationService.getCurrentUser();

		// #16902 : Ajouter le champ labelCourt limité à 60 caractères dans les
		// propriétés d'une entité
		if (StringUtils.isNotBlank(this.entity.getLabel())) {
			this.entity.setLabelCourt(StringUtils.substring(this.entity.getLabel(), 0, 60));
		}

		if (!profilAgentDto.isEdition() || showErrorPopup(this.entity)) {
			return;
		}

		EntiteDto entiteDtoParent = this.entity.getEntiteParent();
		EntiteDto newEntiteDto = createAndInitNewEntiteDto(entiteDtoParent, profilAgentDto.getIdAgent());

		// On fait appel au WS ADS de création d'une entité
		ReturnMessageDto returnMessageDto = adsWSConsumer.saveOrUpdateEntite(newEntiteDto);
		if (!returnMessageService.gererReturnMessage(returnMessageDto)) {
			return;
		}

		// On recharge le dto directement depuis ADS pour être sur d'avoir la
		// version bien à jour
		newEntiteDto = adsWSConsumer.getEntite(returnMessageDto.getId());

		// #16861 : On force le sigle en majuscule
		newEntiteDto.setSigle(OrganigrammeUtil.majusculeSansAccentTrim(newEntiteDto.getSigle()));

		// On ajoute l'entité à l'arbre déjà existant pour que le côté client
		// puisse reconstruire l'arbre complet
		final Map<String, Object> mapEntite = new HashMap<String, Object>();
		mapEntite.put("entiteDtoParent", entiteDtoParent);
		mapEntite.put("newEntiteDto", newEntiteDto);
		mapEntite.put("ouvreOnglet", true);
		BindUtils.postGlobalCommand(null, null, "refreshArbreSuiteAjout", mapEntite);

		getPopup().detach();
	}

	/**
	 * Renvoi une nouvelle instance d'{@link EntiteDto} initialisée avec les
	 * valeurs de la popup de création
	 * 
	 * @param entiteDtoParent
	 *            : l'{@link EntiteDto} parente
	 * @param idAgent
	 *            : l'idAgent qui fait la création
	 * @return une nouvelle instance d'{@link EntiteDto}
	 */
	private EntiteDto createAndInitNewEntiteDto(EntiteDto entiteDtoParent, Integer idAgent) {
		EntiteDto newEntiteDto = new EntiteDto();
		newEntiteDto.setStatut(Statut.PREVISION);
		newEntiteDto.setIdStatut(0);
		newEntiteDto.setSigle(this.entity.getSigle());
		newEntiteDto.setLabel(this.entity.getLabel());
		newEntiteDto.setLabelCourt(this.entity.getLabelCourt());
		newEntiteDto.setTypeEntite(this.entity.getTypeEntite());
		newEntiteDto.setEntiteParent(entiteDtoParent);
		newEntiteDto.setIdAgentCreation(idAgent);
		return newEntiteDto;
	}

	/**
	 * Renvoie la liste des types d'entités actifs triés par nom
	 * 
	 * @return la liste des types d'entités actifs triés par nom
	 */
	public List<TypeEntiteDto> getListeTypeEntiteActifInactif() {
		return typeEntiteService.getListeTypeEntiteActif();
	}
}
