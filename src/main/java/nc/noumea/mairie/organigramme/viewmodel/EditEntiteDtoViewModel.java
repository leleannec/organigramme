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
import nc.noumea.mairie.organigramme.enums.StatutFichePoste;
import nc.noumea.mairie.organigramme.services.OrganigrammeService;
import nc.noumea.mairie.organigramme.services.ReturnMessageService;
import nc.noumea.mairie.organigramme.services.TypeEntiteService;
import nc.noumea.mairie.organigramme.utils.ComparatorUtil;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Tab;

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

	private FichePosteGroupingModel fichePosteGroupingModel;

	private List<FichePosteDto> listeFichePoste;

	private List<EntiteHistoDto> listeHistorique;

	/**
	 * L'onglet en cours de sélection (par défaut, quand on ouvre une entité
	 * c'est caractéristique)
	 **/
	private EntiteOnglet ongletSelectionne = EntiteOnglet.CARACTERISTIQUE;

	private boolean afficheFdpInactive = false;

	private boolean afficheFdpTableau = false;

	public boolean isAfficheFdpInactive() {
		return afficheFdpInactive;
	}

	@DependsOn({ "fichePosteGroupingModel", "listeFichePoste" })
	public boolean isAfficheFdpTableau() {
		return afficheFdpTableau;
	}

	public boolean isModifiable() {
		// On ne peux modifier que si on a le rôle édition
		return profilAgentDto.isEdition() && this.entity != null;
	}

	public String getTitreOngletFdp() {
		String result = "Fiches de postes";
		if (this.fichePosteGroupingModel != null) {
			int resultat = 0;
			for (int i = 0; i < this.fichePosteGroupingModel.getGroupCount(); i++) {
				resultat += this.fichePosteGroupingModel.getChildCount(i);
			}
			result += " (" + resultat + ")";
		} else if (this.listeFichePoste != null) {
			result += " (" + this.listeFichePoste.size() + ")";
		}

		return result;
	}

	@NotifyChange({ "fichePosteGroupingModel", "listeFichePoste" })
	public void setAfficheFdpTableau(boolean afficheFdpTableau) {
		this.afficheFdpTableau = afficheFdpTableau;
		// On remet la liste à null pour qu'elle soit rechargée
		this.fichePosteGroupingModel = null;
	}

	@NotifyChange({ "fichePosteGroupingModel", "listeFichePoste" })
	public void setAfficheFdpInactive(boolean afficheFdpInactive) {
		this.afficheFdpInactive = afficheFdpInactive;
		// On remet la liste à null pour qu'elle soit rechargée
		this.fichePosteGroupingModel = null;
	}

	public void setEntiteDirty(boolean dirty) {
		boolean majTitreOnglet = this.entity.isDirty() != dirty;
		this.entity.setDirty(dirty);
		if (majTitreOnglet) {
			updateTitreOnglet();
		}
	}

	private void updateTitreOnglet() {
		String suffixe = this.entity.isDirty() ? " (*)" : null;
		EventQueues.lookup("organigrammeQueue", EventQueues.DESKTOP, true).publish(
				new UpdateOngletAbstractEntityEvent(this.entity, suffixe));
	}

	@Override
	public void initSetup(@ExecutionArgParam("entity") EntiteDto entiteDto) {
		profilAgentDto = authentificationService.getCurrentUser();
		// On recharge l'entité depuis la base pour être sur d'être bien à jour
		this.entity = adsWSConsumer.getEntiteWithChildren(entiteDto.getIdEntite());
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
	@NotifyChange("titreOngletFdp")
	public FichePosteGroupingModel getFichePosteGroupingModel() {
		if (this.entity == null || !this.ongletSelectionne.equals(EntiteOnglet.FDP)) {
			return null;
		}

		if (this.fichePosteGroupingModel != null || this.afficheFdpTableau) {
			return this.fichePosteGroupingModel;
		}

		String listIdStatutFDP = StatutFichePoste.getListIdStatutActif();

		if (this.afficheFdpInactive) {
			listIdStatutFDP += "," + StatutFichePoste.INACTIVE.getId();
		}

		List<FichePosteDto> listeFichePosteDto = sirhWSConsumer.getFichePosteByIdEntite(this.entity.getIdEntite(),
				listIdStatutFDP, true);

		this.fichePosteGroupingModel = new FichePosteGroupingModel(listeFichePosteDto,
				new ComparatorUtil.FichePosteComparatorAvecSigleEnTete(this.entity.getSigle()), this.entity.getSigle());

		return this.fichePosteGroupingModel;
	}

	/**
	 * Renvoie la liste des fiches de postes
	 * 
	 * @return la liste des fiches de postes
	 */
	@NotifyChange("titreOngletFdp")
	public List<FichePosteDto> getListeFichePoste() {
		if (this.entity == null || !this.ongletSelectionne.equals(EntiteOnglet.FDP)) {
			return null;
		}

		if (this.listeFichePoste != null || !this.afficheFdpTableau) {
			return this.listeFichePoste;
		}

		String listIdStatutFDP = StatutFichePoste.getListIdStatutActif();

		if (this.afficheFdpInactive) {
			listIdStatutFDP += "," + StatutFichePoste.INACTIVE.getId();
		}

		return sirhWSConsumer.getFichePosteByIdEntite(this.entity.getIdEntite(), listIdStatutFDP, true);
	}

	@Command
	@NotifyChange("fichePosteGroupingModel")
	public void replierTouteFdp() {
		if (this.fichePosteGroupingModel != null) {
			// On replie tous les groupes sauf celui de l'entité
			for (int i = 0; i < this.fichePosteGroupingModel.getGroupCount(); i++) {
				this.fichePosteGroupingModel.removeOpenGroup(i);
			}
		}
	}

	@Command
	@NotifyChange("fichePosteGroupingModel")
	public void deplierTouteFdp() {
		if (this.fichePosteGroupingModel != null) {
			// On replie tous les groupes sauf celui de l'entité
			for (int i = 0; i < this.fichePosteGroupingModel.getGroupCount(); i++) {
				this.fichePosteGroupingModel.addOpenGroup(i);
			}
		}
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

		if (this.listeHistorique != null) {
			return this.listeHistorique;
		}

		this.listeHistorique = adsWSConsumer.getListeEntiteHisto(this.entity.getIdEntite(),
				new HashMap<Integer, String>());

		return this.listeHistorique;
	}

	@Command
	@NotifyChange("entity")
	public void onChangeValueEntity() {
		setEntiteDirty(true);
	}

	@Command
	@NotifyChange({ "listeHistorique", "fichePosteGroupingModel", "titreOngletFdp" })
	public void selectOnglet(@BindingParam("onglet") int onglet) {
		setOngletSelectionne(EntiteOnglet.getEntiteOngletByPosition(onglet));
	}

	/**
	 * Rafraichi l'entité depuis la base de donnée
	 * 
	 * @param entiteDto
	 *            : l'entité à rafraîchir
	 */
	@Command
	@NotifyChange({ "*", "titreOngletFdp" })
	public void refreshEntite(@BindingParam("entity") EntiteDto entiteDto) {
		this.entity = adsWSConsumer.getEntiteWithChildren(entiteDto.getIdEntite());

		// On force à null pour que ce soit rafraîchi
		this.fichePosteGroupingModel = null;
		this.listeFichePoste = null;
		this.listeHistorique = null;

		setEntiteDirty(false);
		updateTitreOnglet();
		Clients.showNotification("Entité " + this.entity.getSigle() + " rafraîchie.", "info", null, "top_center", 0);
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
	@NotifyChange({ "entity", "listeHistorique" })
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

		setEntiteDirty(false);
		this.listeHistorique = null;

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

	@GlobalCommand
	public void onCloseEntiteOnglet(@BindingParam("entity") EntiteDto entiteDto, @BindingParam("tab") Tab tab) {
		if (entiteDto.getId().equals(this.entity.getId())) {
			if (this.entity.isDirty()) {
				final EntiteDto entiteASauver = this.entity;
				final Tab tabAFermer = tab;
				Messagebox.show(
						"Voulez-vous enregistrer les modifications apportées à l'entité '" + this.entity.getSigle()
								+ "' ?", "Fermeture de l'onglet", new Messagebox.Button[] { Messagebox.Button.YES,
								Messagebox.Button.NO, Messagebox.Button.CANCEL }, Messagebox.QUESTION,
						new EventListener<Messagebox.ClickEvent>() {

							@Override
							public void onEvent(ClickEvent evt) {
								if (evt.getName().equals("onYes")) {
									updateEntite(entiteASauver);
									tabAFermer.onClose();
								}
								if (evt.getName().equals("onNo")) {
									tabAFermer.onClose();
								}
							}
						});
			} else {
				tab.onClose();
			}
		}
	}

	/**
	 * @return statut de l'entité
	 */
	@DependsOn("entity")
	public Statut getStatut() {
		if (this.entity == null) {
			return null;
		}
		return entity.getStatut();
	}
}
