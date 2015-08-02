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

import nc.noumea.mairie.organigramme.core.event.UpdateOngletAbstractEntityEvent;
import nc.noumea.mairie.organigramme.core.services.AuthentificationService;
import nc.noumea.mairie.organigramme.core.viewmodel.AbstractEditViewModel;
import nc.noumea.mairie.organigramme.core.ws.IAdsWSConsumer;
import nc.noumea.mairie.organigramme.core.ws.ISirhWSConsumer;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.dto.EntiteHistoDto;
import nc.noumea.mairie.organigramme.dto.FichePosteDto;
import nc.noumea.mairie.organigramme.dto.ProfilAgentDto;
import nc.noumea.mairie.organigramme.dto.ReturnMessageDto;
import nc.noumea.mairie.organigramme.dto.TypeEntiteDto;
import nc.noumea.mairie.organigramme.enums.EntiteOnglet;
import nc.noumea.mairie.organigramme.enums.Statut;
import nc.noumea.mairie.organigramme.services.OrganigrammeService;
import nc.noumea.mairie.organigramme.services.ReturnMessageService;
import nc.noumea.mairie.organigramme.services.TypeEntiteService;
import nc.noumea.mairie.organigramme.utils.ComparatorUtil;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Li;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;

@Init(superclass = true)
@VariableResolver(DelegatingVariableResolver.class)
public class EditEntiteDtoViewModel extends AbstractEditViewModel<EntiteDto> implements Serializable {

	private static final long serialVersionUID = 1L;

	// @formatter:off
	@WireVariable
	IAdsWSConsumer adsWSConsumer;
	@WireVariable
	ISirhWSConsumer sirhWSConsumer;
	@WireVariable
	TypeEntiteService typeEntiteService;
	@WireVariable
	OrganigrammeService organigrammeService;
	@WireVariable
	AuthentificationService authentificationService;
	@WireVariable
	ReturnMessageService returnMessageService;
	// @formatter:on

	/** Le currentUser connecté **/
	private ProfilAgentDto profilAgentDto;

	/**
	 * L'onglet en cours de sélection (par défaut, quand on ouvre une entité
	 * c'est caractéristique)
	 **/
	private EntiteOnglet ongletSelectionne = EntiteOnglet.CARACTERISTIQUE;

	private boolean dirty = false;

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		boolean majTitreOnglet = this.dirty != dirty;
		this.dirty = dirty;
		if (majTitreOnglet) {
			updateTitreOnglet();
		}
	}

	private void updateTitreOnglet() {
		String suffixe = this.dirty ? " (*)" : null;
		EventQueues.lookup("organigrammeQueue", EventQueues.DESKTOP, true).publish(
				new UpdateOngletAbstractEntityEvent(this.entity, suffixe));
	}

	@Override
	public void initSetup(@ExecutionArgParam("entity") EntiteDto entiteDto) {
		profilAgentDto = authentificationService.getCurrentUser();
		Li li = entiteDto.getLi();
		// On recharge l'entité depuis la base pour être sur d'être bien à jour
		this.entity = adsWSConsumer.getEntiteWithChildren(entiteDto.getIdEntite());
		this.entity.setLi(li);
		this.entity.setStatut(Statut.getStatutById(this.entity.getIdStatut()));
	}

	public ProfilAgentDto getProfilAgentDto() {
		return profilAgentDto;
	}

	public void setProfilAgentDto(ProfilAgentDto profilAgentDto) {
		this.profilAgentDto = profilAgentDto;
	}

	/**
	 * L'entité est-elle éditable ?
	 * 
	 * @return true si l'entité est éditable, false sinon
	 */
	public boolean isEditable() {
		// On ne peux modifier que si on a le rôle édition et si ce n'est pas
		// l'entité VDN
		// #17117 : En dehors du statut "prévision", une entité n'est pas
		// modifiable
		return profilAgentDto.isEdition()
				&& (this.entity != null && this.entity.isPrevision() && !this.entity.getSigle().equals("VDN"));
	}

	public EntiteOnglet getOngletSelectionne() {
		return ongletSelectionne;
	}

	public void setOngletSelectionne(EntiteOnglet ongletSelectionne) {
		this.ongletSelectionne = ongletSelectionne;
	}

	/**
	 * Renvoie la liste des types d'entités actifs et inactifs triés par nom
	 * (d'abord les actifs, puis les inactifs postfixés par "(inactif)"
	 * 
	 * @return la liste des types d'entités actifs et inactifs triés par nom
	 */
	public List<TypeEntiteDto> getListeTypeEntiteActifInactif() {
		return typeEntiteService.getListeTypeEntiteActifInactif();
	}

	/**
	 * Affiche la liste des entités remplaçables, ie. la liste de toutes les
	 * entités qui ne sont pas dans un statut "prévision". Elle ne contient pas
	 * l'entité selectionnée (une entité ne peux pas être remplacée par
	 * elle-même).
	 * 
	 * @return la liste des entités remplaçables
	 */
	public List<EntiteDto> getListeEntiteRemplace() {
		List<EntiteDto> listeEntiteRemplace = organigrammeService.findAllNotPrevision();
		listeEntiteRemplace.remove(this.entity);

		return listeEntiteRemplace;
	}

	/**
	 * Renvoie la liste des fiches de postes groupées par Sigle
	 * 
	 * @return la liste des fiches de postes groupées par Sigle
	 */
	public FichePosteGroupingModel getFichePosteGroupingModel() {
		if (this.entity == null || !this.ongletSelectionne.equals(EntiteOnglet.FDP)) {
			return null;
		}

		List<FichePosteDto> listeFichePosteDto = sirhWSConsumer
				.getFichePosteByIdEntite(this.entity.getIdEntite(), true);

		return new FichePosteGroupingModel(listeFichePosteDto, new ComparatorUtil.FichePosteComparator());
	}

	/**
	 * Renvoie la liste de l'historique de l'entité
	 * 
	 * @return la liste de l'historique de l'entité
	 */
	public List<EntiteHistoDto> getListeHistorique() {
		if (this.entity == null || !this.ongletSelectionne.equals(EntiteOnglet.HISTORIQUE)) {
			return null;
		}

		return adsWSConsumer.getListeEntiteHisto(this.entity.getIdEntite(), new HashMap<Integer, String>());
	}

	@Command
	@NotifyChange("dirty")
	public void onChangeValueEntity() {
		setDirty(true);
	}

	@Command
	@NotifyChange({ "listeHistorique", "fichePosteGroupingModel" })
	public void selectOnglet(@BindingParam("onglet") int onglet) {
		setOngletSelectionne(EntiteOnglet.getEntiteOngletByPosition(onglet));
	}

	/**
	 * Met à jour le {@link EntiteDto} avec les informations renseignées côté
	 * client
	 * 
	 * @param entiteDto
	 *            : l'{@link EntiteDto} à mettre à jour
	 * @return true si tout s'est bien passé, false sinon
	 */
	@Command
	@NotifyChange({ "entity", "dirty" })
	public boolean updateEntite(@BindingParam("entity") EntiteDto entiteDto) {

		if (!profilAgentDto.isEdition() || showErrorPopup(entiteDto)) {
			return false;
		}

		entiteDto.setIdAgentModification(profilAgentDto.getIdAgent());

		// On fait appel au WS ADS de mise à jour d'une entité
		ReturnMessageDto returnMessageDto = adsWSConsumer.saveOrUpdateEntite(entiteDto);
		if (!returnMessageService.gererReturnMessage(returnMessageDto)) {
			return false;
		}

		// On recharge l'arbre complet d'ADS et on rafraichi le client. Ainsi on
		// est sur d'avoir une version bien à jour
		final Map<String, Object> mapEntite = new HashMap<String, Object>();
		mapEntite.put("entiteDto", entiteDto);
		BindUtils.postGlobalCommand(null, null, "refreshOrganigrammeSuiteAjout", mapEntite);

		setDirty(false);
		return true;
	}

	/**
	 * Supprime l'{@link EntiteDto} de l'arbre DTO représentée par l'
	 * {@link EntiteDto} entiteDtoRoot et rafraichie côté client
	 * 
	 * @param entiteDto
	 *            : l'{@link EntiteDto} à supprimer
	 */
	@Command
	public void deleteEntite(@BindingParam("entity") EntiteDto entiteDto) {

		if (!profilAgentDto.isEdition() && entiteDto.isPrevision()) {
			return;
		}

		entiteDto.setIdAgentSuppression(profilAgentDto.getIdAgent());
		final EntiteDto entiteDtoASupprimer = entiteDto;

		Messagebox.show("Voulez-vous vraiment supprimer l'entité '" + entiteDto.getLabel() + "' ?", "Suppression",
				new Messagebox.Button[] { Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION,
				new EventListener<Messagebox.ClickEvent>() {

					@Override
					public void onEvent(ClickEvent evt) {
						if (evt.getName().equals("onYes")) {
							if (organigrammeService.deleteEntite(entiteDtoASupprimer)) {
								fermeOnglet(entity);
								setEntity(null);
								// On recharge l'arbre complet d'ADS et on
								// rafraichi le client. Ainsi on est sur d'avoir
								// une version bien à jour
								BindUtils.postGlobalCommand(null, null, "refreshOrganigrammeWithoutSelectedEntite",
										null);
							}
						}
					}
				});

	}
}