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

import nc.noumea.mairie.organigramme.core.viewmodel.AbstractPopupViewModel;
import nc.noumea.mairie.organigramme.core.ws.IAdsWSConsumer;
import nc.noumea.mairie.organigramme.dto.DuplicationDto;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.dto.ReturnMessageDto;
import nc.noumea.mairie.organigramme.services.OrganigrammeService;
import nc.noumea.mairie.organigramme.services.ReturnMessageService;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Window;

@Init(superclass = true)
@VariableResolver(DelegatingVariableResolver.class)
public class CreateDuplicationViewModel extends AbstractPopupViewModel<DuplicationDto> implements Serializable {

	private static final long serialVersionUID = 1L;

	@WireVariable
	OrganigrammeService organigrammeService;

	@WireVariable
	IAdsWSConsumer adsWSConsumer;

	@WireVariable
	ReturnMessageService returnMessageService;

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		setPopup((Window) Selectors.iterable(view, "#createDuplication").iterator().next());
	}

	/**
	 * Affiche la liste des entités cibles pour une duplcation, ie. la liste de
	 * toutes les entités qui sont dans un statut "actif" ou "prévision. Elle ne
	 * contient pas l'entité selectionnée (une entité ne peux pas être dupliqué
	 * vers elle-même).
	 * 
	 * @return la liste des entités cibles possibles
	 */
	public List<EntiteDto> getListeEntiteCible() {
		List<EntiteDto> result = organigrammeService.findAllActifOuPrevision();
		if (this.entity != null) {
			result.remove(this.entity.getEntiteDto());
		}

		return result;
	}

	@GlobalCommand
	@NotifyChange({ "entity", "listeEntiteCible" })
	public void ouvrePopupCreationDuplication(@BindingParam("duplicationDto") DuplicationDto duplicationDto) {
		setEntity(duplicationDto);
		getPopup().doModal();
	}

	/**
	 * Lance la duplication
	 */
	@Command
	public void duplique() {
		ReturnMessageDto returnMessageDto = organigrammeService.dupliqueEntite(this.entity);

		if (!returnMessageService.gererReturnMessage(returnMessageDto)) {
			return;
		}

		getPopup().detach();

		// On recharge le dto directement depuis ADS pour être sur d'avoir la
		// version bien à jour
		EntiteDto newEntiteDto = adsWSConsumer.getEntite(returnMessageDto.getId());

		final Map<String, Object> mapEntite = new HashMap<String, Object>();
		mapEntite.put("entiteDtoParent", newEntiteDto.getEntiteParent());
		mapEntite.put("newEntiteDto", newEntiteDto);
		mapEntite.put("ouvreOnglet", false);
		BindUtils.postGlobalCommand(null, null, "refreshArbreSuiteAjout", mapEntite);
	}
}
