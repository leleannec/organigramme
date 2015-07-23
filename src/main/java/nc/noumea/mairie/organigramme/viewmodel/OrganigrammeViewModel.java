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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.organigramme.core.services.AuthentificationService;
import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;
import nc.noumea.mairie.organigramme.core.viewmodel.AbstractViewModel;
import nc.noumea.mairie.organigramme.core.ws.IAdsWSConsumer;
import nc.noumea.mairie.organigramme.core.ws.ISirhWSConsumer;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.dto.ExportDto;
import nc.noumea.mairie.organigramme.dto.FichePosteDto;
import nc.noumea.mairie.organigramme.dto.ProfilAgentDto;
import nc.noumea.mairie.organigramme.dto.ReturnMessageDto;
import nc.noumea.mairie.organigramme.dto.TypeEntiteDto;
import nc.noumea.mairie.organigramme.enums.FiltreStatut;
import nc.noumea.mairie.organigramme.enums.Statut;
import nc.noumea.mairie.organigramme.enums.Transition;
import nc.noumea.mairie.organigramme.query.EntiteDtoQueryListModel;
import nc.noumea.mairie.organigramme.services.CouleurTypeEntiteService;
import nc.noumea.mairie.organigramme.services.ExportGraphMLService;
import nc.noumea.mairie.organigramme.services.OrganigrammeService;
import nc.noumea.mairie.organigramme.services.ReturnMessageService;
import nc.noumea.mairie.organigramme.services.TypeEntiteService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

@Init(superclass = true)
@VariableResolver(DelegatingVariableResolver.class)
public class OrganigrammeViewModel extends AbstractViewModel<EntiteDto> implements Serializable {

	private static final long				serialVersionUID				= 1L;

	public static Logger					log								= LoggerFactory.getLogger(OrganigrammeViewModel.class);

	//@formatter:off
	@WireVariable IAdsWSConsumer			adsWSConsumer;
	@WireVariable ISirhWSConsumer			sirhWsConsumer;
	@WireVariable ExportGraphMLService		exportGraphMLService;
	@WireVariable OrganigrammeService		organigrammeService;
	@WireVariable AuthentificationService	authentificationService;
	@WireVariable CouleurTypeEntiteService	couleurTypeEntiteService;
	@WireVariable TypeEntiteService			typeEntiteService;
	@WireVariable ReturnMessageService		returnMessageService;
	//@formatter:on

	private static final String				CREATE_ENTITE_VIEW				= "/layout/createEntite.zul";

	private static final String[]			LISTE_PROP_A_NOTIFIER_ENTITE	= new String[] { "statut", "entity", "listeTransitionAutorise", "listeEntite",
			"listeEntiteRemplace", "editable", "listeTypeEntiteActifInactif", "hauteurPanelEdition", "mapIdLiEntiteDto", "stylePanelEdition",
			"selectedEntiteDtoRecherche", "selectedEntiteDtoZoom", "entiteDtoQueryListModel", "listeFicheDePoste" };

	private OrganigrammeWorkflowViewModel	organigrammeWorkflowViewModel	= new OrganigrammeWorkflowViewModel(this);
	public TreeViewModel					treeViewModel					= new TreeViewModel(this);

	/** Le vlayout général dans lequel sera ajouté l'arbre **/
	Vlayout									vlayout;

	/** L'{@link EntiteDto} représentant l'arbre au complet **/
	EntiteDto								entiteDtoRoot;

	/** L'{@link EntiteDto} représentant l'entité recherchée **/
	EntiteDto								selectedEntiteDtoRecherche;

	/** L'{@link EntiteDto} représentant l'entité zommé **/
	EntiteDto								selectedEntiteDtoZoom;

	/** Le {@link FiltreStatut} représentant le filtre actif, par défaut, on affiche tous les statuts **/
	FiltreStatut							selectedFiltreStatut			= FiltreStatut.TOUS;

	/** Map permettant rapidement d'accèder à une {@link EntiteDto} à partir de son id html client **/
	Map<String, EntiteDto>					mapIdLiEntiteDto;

	/** Map permettant de savoir si le Li est ouvert ou non à partir de son id html client **/
	Map<String, Boolean>					mapIdLiOuvert;

	/** Le currentUser connecté **/
	ProfilAgentDto							profilAgentDto;

	/** Liste de toutes les entités zoomables et/ou recherchables **/
	private List<EntiteDto>					listeEntite						= new ArrayList<EntiteDto>();

	/** ListModel de toutes les entités zoomables et/ou recherchables **/
	private EntiteDtoQueryListModel			entiteDtoQueryListModel;

	/** Liste des entités remplaçables **/
	private List<EntiteDto>					listeEntiteRemplace				= new ArrayList<EntiteDto>();

	public List<EntiteDto> getListeEntite() {
		return listeEntite;
	}

	// setté par le treeViewModel
	public void setListeEntite(List<EntiteDto> listeEntite) {
		this.listeEntite = listeEntite;
	}

	public EntiteDto getSelectedEntiteDtoRecherche() {
		return selectedEntiteDtoRecherche;
	}

	public void setSelectedEntiteDtoRecherche(EntiteDto selectedEntiteDtoRecherche) {
		this.selectedEntiteDtoRecherche = selectedEntiteDtoRecherche;
	}

	public EntiteDto getSelectedEntiteDtoZoom() {
		return selectedEntiteDtoZoom;
	}

	public void setSelectedEntiteDtoZoom(EntiteDto selectedEntiteDtoZoom) {
		this.selectedEntiteDtoZoom = selectedEntiteDtoZoom;
	}

	public FiltreStatut getSelectedFiltreStatut() {
		return selectedFiltreStatut;
	}

	public void setSelectedFiltreStatut(FiltreStatut selectedFiltreStatut) {
		this.selectedFiltreStatut = selectedFiltreStatut;
	}

	public EntiteDtoQueryListModel getEntiteDtoQueryListModel() {
		return entiteDtoQueryListModel;
	}

	public void setEntiteDtoQueryListModel(EntiteDtoQueryListModel entiteDtoQueryListModel) {
		this.entiteDtoQueryListModel = entiteDtoQueryListModel;
	}

	/**
	 * Point d'entrée du viewModel. Fait un appel à ADS afin de récupérer l'arbre et le crée
	 * @param view : la vue permettant de récuperer le {@link Vlayout} dans lequel sera ajouté l'arbre
	 */
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		// permet de gérer le onClickEntite
		Selectors.wireEventListeners(view, this);

		mapIdLiOuvert = new HashMap<String, Boolean>();
		vlayout = (Vlayout) Selectors.iterable(view, "#organigramme").iterator().next();
		profilAgentDto = authentificationService.getCurrentUser();

		// On recharge l'arbre complet d'ADS et on rafraichi le client. Ainsi on est sur d'avoir une version bien à jour
		creeArbreComplet(adsWSConsumer.getCurrentTreeWithVDNRoot());
	}

	/**
	 * Affiche la liste des entités remplaçables. Cette liste est chargée et rafraichie dans une variable globale au viewModel pour gagner en perfs et ne pas la
	 * charger (via un appel ADS) à chaque click sur une entité. Elle affiche la liste de toutes les entités qui ne sont pas dans un statut "prévision". Elle ne
	 * contient pas l'entité selectionnée (une entité ne peux pas être remplacée par elle-même).
	 * @return la liste des entités remplaçables
	 */
	public List<EntiteDto> getListeEntiteRemplace() {

		if (this.entity == null) {
			return null;
		}

		// On créée une copie pour ne pas toucher à la liste originale
		List<EntiteDto> listeEntiteRemplaceCopie = new ArrayList<EntiteDto>();
		listeEntiteRemplaceCopie.addAll(listeEntiteRemplace);
		listeEntiteRemplaceCopie.remove(this.entity);

		return listeEntiteRemplaceCopie;
	}

	public void setListeEntiteRemplace(List<EntiteDto> listeEntiteRemplace) {
		this.listeEntiteRemplace = listeEntiteRemplace;
	}

	/**
	 * Crée l'arbre et l'ajoute au {@link Vlayout} client et rafraichi le client
	 */
	@GlobalCommand
	public void creeArbreEtRafraichiClient() {
		treeViewModel.creeArbre(entiteDtoRoot);
		notifyChange(LISTE_PROP_A_NOTIFIER_ENTITE);
		refreshListeEntiteRemplace();
		Clients.evalJavaScript("refreshOrganigramme();");
	}

	private void creeArbreComplet(EntiteDto entiteDtoRootACharger) {
		entiteDtoRoot = entiteDtoRootACharger;
		treeViewModel.creeArbre(entiteDtoRoot);
		notifyChange(LISTE_PROP_A_NOTIFIER_ENTITE);
		refreshListeEntiteRemplace();
	}

	/**
	 * Evenement qui se déclenche lors d'un click sur une entité côté client
	 * @param event : l'évenement click qui contient dans getData() l'id du li selectionné
	 */
	@Listen("onClickEntite = #organigramme")
	public void onClickEntite(Event event) {
		EntiteDto entiteDto = mapIdLiEntiteDto.get(event.getData());
		setEntity(entiteDto);
		notifyChange(LISTE_PROP_A_NOTIFIER_ENTITE);
	}

	/**
	 * Evenement qui se déclenche lors d'un double click sur une entité côté client
	 * @param event : l'évenement click qui contient dans getData() l'id du li selectionné
	 */
	@Listen("onDblClickEntite = #organigramme")
	public void onDblClickEntite(Event event) {
		EntiteDto entiteDto = mapIdLiEntiteDto.get(event.getData());

		boolean ouvert = mapIdLiOuvert.get(entiteDto.getLi().getId()) != null ? !mapIdLiOuvert.get(entiteDto.getLi().getId()) : false;
		mapIdLiOuvert.put(entiteDto.getLi().getId(), ouvert);
		setLiOuvertOuFermeArbre(entiteDto.getEnfants(), ouvert);

		// Lors d'un double clic on set l'entity à null
		setEntity(null);
		notifyChange(LISTE_PROP_A_NOTIFIER_ENTITE);
	}

	@Listen("onClickToutDeplier = #organigramme")
	public void onClickToutDeplier(Event event) {
		mapIdLiOuvert.put(entiteDtoRoot.getLi().getId(), true);
		setLiOuvertOuFermeArbre(entiteDtoRoot.getEnfants(), true);
	}

	@Listen("onClickToutReplier = #organigramme")
	public void onClickToutReplier(Event event) {
		mapIdLiOuvert.put(entiteDtoRoot.getLi().getId(), false);
		setLiOuvertOuFermeArbre(entiteDtoRoot.getEnfants(), false);
	}

	/**
	 * Méthode qui parcours tout l'arbre et met à jour la mapIdLiOuvert
	 * @param listeEntiteDto : la liste à parcourir
	 * @param ouvert : ouvert ou fermé
	 */
	private void setLiOuvertOuFermeArbre(List<EntiteDto> listeEntiteDto, boolean ouvert) {
		for (EntiteDto entiteDto : listeEntiteDto) {
			mapIdLiOuvert.put(entiteDto.getLi().getId(), ouvert);
			setLiOuvertOuFermeArbre(entiteDto.getEnfants(), ouvert);
		}
	}

	/**
	 * Initialise un {@link EntiteDto} avec son parent et rend visible la popup de création de l'entité
	 * @param entiteDto : le {@link EntiteDto} parent
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Command
	public void createEntite(@BindingParam("entity") EntiteDto entiteDto) throws InstantiationException, IllegalAccessException {

		if (!profilAgentDto.isEdition()) {
			return;
		}

		// Le parent doit être en statut P ou A
		if (entiteDto.getStatut() != Statut.PREVISION && entiteDto.getStatut() != Statut.ACTIF) {
			AbstractViewModel.showErrorPopup("Vous ne pouvez créer une entité que si son parent est dans l'état Actif ou Prévision");
			return;
		}

		EntiteDto newEntiteDto = new EntiteDto();
		newEntiteDto.setEntiteParent(entiteDto);
		initPopupEdition(newEntiteDto, CREATE_ENTITE_VIEW);
	}

	/**
	 * Recharge l'arbre complet, rafraichi le client et selectionne l'entité crée
	 * @param entiteDtoParent : l'entité parente
	 * @param newEntiteDto : la nouvelle entitée
	 */
	@GlobalCommand
	public void refreshArbreSuiteAjout(@BindingParam("entiteDtoParent") EntiteDto entiteDtoParent, @BindingParam("newEntiteDto") EntiteDto newEntiteDto) {

		creeArbreComplet(adsWSConsumer.getCurrentTreeWithVDNRoot());

		// Vu qu'on vient de reconstruire l'arbre complet on recharge le nouveau DTO
		newEntiteDto = OrganigrammeUtil.findEntiteDtoDansArbreById(entiteDtoRoot, newEntiteDto.getIdEntite(), null);

		setEntity(newEntiteDto);
		mapIdLiOuvert.put(newEntiteDto.getLi().getId(), false);
		// Appel de la fonction javascript correspondante
		Clients.evalJavaScript("refreshOrganigrammeSuiteAjout('" + newEntiteDto.getLi().getId() + "', '" + entiteDtoParent.getLi().getId() + "');");
	}

	/**
	 * Met à jour le {@link EntiteDto} avec les informations renseignées côté client
	 * @param entiteDto : l'{@link EntiteDto} à mettre à jour
	 * @return true si tout s'est bien passé, false sinon
	 */
	@Command
	@NotifyChange("entity")
	public boolean updateEntite(@BindingParam("entity") EntiteDto entiteDto) {

		if (!profilAgentDto.isEdition()) {
			return false;
		}

		entiteDto.setIdAgentModification(profilAgentDto.getIdAgent());

		// On fait appel au WS ADS de mise à jour d'une entité
		ReturnMessageDto returnMessageDto = adsWSConsumer.saveOrUpdateEntite(entiteDto);
		if (!returnMessageService.gererReturnMessage(returnMessageDto)) {
			return false;
		}

		// On recharge l'arbre complet d'ADS et on rafraichi le client. Ainsi on est sur d'avoir une version bien à jour
		creeArbreComplet(adsWSConsumer.getCurrentTreeWithVDNRoot());
		Clients.evalJavaScript("refreshOrganigrammeSuiteAjout('" + entiteDto.getLi().getId() + "');");

		return true;
	}

	/**
	 * Déplie {@link EntiteDto} côté client.
	 * @param entiteDto : l'{@link EntiteDto} à déplier
	 */
	@Command
	public void deplierEntite(@BindingParam("entity") EntiteDto entiteDto) {
		Clients.evalJavaScript("expandEntiteFromIdDiv('" + entiteDto.getLi().getId() + "');");
	}

	/**
	 * Supprime l'{@link EntiteDto} de l'arbre DTO représentée par l'{@link EntiteDto} entiteDtoRoot et rafraichie côté client
	 * @param entiteDto : l'{@link EntiteDto} à supprimer
	 */
	@Command
	public void deleteEntite(@BindingParam("entity") EntiteDto entiteDto) {

		if (!profilAgentDto.isEdition() && entiteDto.isPrevision()) {
			return;
		}

		entiteDto.setIdAgentSuppression(profilAgentDto.getIdAgent());

		final EntiteDto entiteDtoASupprimer = entiteDto;
		Messagebox.show("Voulez-vous vraiment supprimer l'entité '" + entiteDto.getLabel() + "' ?", "Suppression", new Messagebox.Button[] {
				Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, new EventListener<Messagebox.ClickEvent>() {

			@Override
			public void onEvent(ClickEvent evt) {
				if (evt.getName().equals("onYes")) {
					if (organigrammeService.deleteEntite(entiteDtoASupprimer)) {
						setEntity(null);
						mapIdLiOuvert.remove(entiteDtoASupprimer.getLi().getId());
						// On recharge l'arbre complet d'ADS et on rafraichi le client. Ainsi on est sur d'avoir une version bien à jour
						creeArbreComplet(adsWSConsumer.getCurrentTreeWithVDNRoot());

						// Appel de la fonction javascript correscpondante
						Clients.evalJavaScript("refreshOrganigrammeWithoutSelectedEntite();");
					}
				}
			}
		});

	}

	/**
	 * Exporte au format GraphML l'arbre ayant pour racine l'{@link EntiteDto} entiteDto
	 * @param exportDto : l'exportDto contenant l'entité à partir de laquelle on souhaite exporter et si on souhaite ou non exporter les FDP
	 */
	@GlobalCommand
	public void exportGraphMLFromEntite(@BindingParam("exportDto") ExportDto exportDto) {
		exportGraphMLService.exportGraphMLFromEntite(exportDto, mapIdLiOuvert);
	}

	/**
	 * @return la liste des transitions autorisées pour cette entité
	 */
	public List<Transition> getListeTransitionAutorise() {
		if (this.entity == null) {
			return null;
		}
		return this.entity.getListeTransitionAutorise();
	}

	/**
	 * Met à jour la liste d'entité remplaçable
	 */
	public void refreshListeEntiteRemplace() {
		setListeEntiteRemplace(organigrammeService.findAllNotPrevision());
	}

	/**
	 * Effectue un changement d'état sur l'entité
	 * 
	 * @param transition Transition concernée
	 */
	@Command
	@NotifyChange({ "*" })
	public void passerTransition(@BindingParam("transition") Transition transition) {

		if (!profilAgentDto.isEdition()) {
			return;
		}

		organigrammeWorkflowViewModel.passerTransition(transition);
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

	@GlobalCommand
	public void refreshApresTransition(@BindingParam("entity") EntiteDto entiteDto) {
		refreshGeneric(entiteDto, LISTE_PROP_A_NOTIFIER_ENTITE);
	}

	public void refreshGeneric(EntiteDto entiteDto, String[] listePropANotifier) {

		// ne devrait pas arriver
		if (entity == null) {
			return;
		}

		// l'entité à rafraîchir n'est pas celle associée à ce ViewModel
		if (!OrganigrammeUtil.sameIdAndNotNull(entity.getId(), this.entity.getId())) {
			return;
		}

		refreshListeEntiteRemplace();

		notifyChange(listePropANotifier);

		// Appel de la fonction javascript correspondante
		Clients.evalJavaScript("refreshOrganigramme();");
	}

	/**
	 * L'entité est-elle éditable ?
	 * @return true si l'entité est éditable, false sinon
	 */
	public boolean isEditable() {
		// On ne peux modifier que si on a le rôle édition et si ce n'est pas l'entité VDN
		// #17117 : En dehors du statut "prévision", une entité n'est pas modifiable
		return profilAgentDto.isEdition() && (this.entity != null && this.entity.isPrevision() && !this.entity.getSigle().equals("VDN"));
	}

	/**
	 * Renvoie la liste des types d'entités actifs et inactifs triés par nom (d'abord les actifs, puis les inactifs postfixés par "(inactif)"
	 * @return la liste des types d'entités actifs et inactifs triés par nom
	 */
	public List<TypeEntiteDto> getListeTypeEntiteActifInactif() {
		return typeEntiteService.getListeTypeEntiteActifInactif();
	}

	/**
	 * Met à jour l'entité avec les dates et les références de délibérations saisies et passe la transition
	 * @param entiteDto : l'{@link EntiteDto} sur laquelle on souhaite passer la transition
	 * @param transition : la transition a passer
	 * @param popup : la popup de changement de statut
	 */
	@GlobalCommand
	public void saveStatutWithRefAndDateGenerique(@BindingParam("entity") EntiteDto entiteDto, @BindingParam("transition") final Transition transition,
			@BindingParam("popup") final Window popup) {

		if (entiteDto == null || !OrganigrammeUtil.sameIdAndNotNull(entiteDto.getId(), this.entity.getId())) {
			return;
		}

		String messageConfirmation = "Êtes-vous sur de vouloir passer cette entité en statut 'ACTIF' ? Plus aucune information de cette entité ne pourra être modifiée.";
		Messagebox.show(messageConfirmation, "Confirmation", new Messagebox.Button[] { Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.EXCLAMATION,
				new EventListener<Messagebox.ClickEvent>() {
					@Override
					public void onEvent(ClickEvent evt) {
						if (evt.getName().equals("onYes")) {
							if (organigrammeWorkflowViewModel.executerTransitionGeneric(transition)) {
								popup.detach();
							}
						}
					}
				});
	}

	public Map<String, EntiteDto> getMapIdLiEntiteDto() {
		return mapIdLiEntiteDto;
	}

	public String getStylePanelEdition() {
		String cssCommun = "display:block;";
		return this.entity == null ? (cssCommun += "visibility: hidden;") : (cssCommun += "visibility: visible;");
	}

	@Command
	public void selectionneEntiteRecherche() {
		if (this.selectedEntiteDtoRecherche != null) {
			setEntity(this.selectedEntiteDtoRecherche);
			notifyChange(LISTE_PROP_A_NOTIFIER_ENTITE);
			Clients.evalJavaScript("goToByScroll('" + this.selectedEntiteDtoRecherche.getLi().getId() + "');");
			this.selectedEntiteDtoRecherche = null;
		}
	}

	@Command
	public void deplierTout() {
		Clients.evalJavaScript("deplierTout();");
	}

	@Command
	public void replierTout() {
		Clients.evalJavaScript("replierTout();");
	}

	@Command
	public void dezoomer() {
		this.selectedEntiteDtoZoom = null;
		setEntity(null);
		notifyChange(LISTE_PROP_A_NOTIFIER_ENTITE);
		creeArbreComplet(adsWSConsumer.getCurrentTreeWithVDNRoot());
		Clients.evalJavaScript("refreshOrganigrammeSuiteDezoom();");
	}

	@Command
	public void selectionneEntiteZoom() {
		if (this.selectedEntiteDtoZoom != null) {
			setEntity(null);
			notifyChange(LISTE_PROP_A_NOTIFIER_ENTITE);
			EntiteDto entiteDto = adsWSConsumer.getEntiteWithChildren(this.selectedEntiteDtoZoom.getIdEntite());
			creeArbreComplet(entiteDto);
			Clients.evalJavaScript("refreshOrganigrammeSuiteZoom();");
		}
	}

	/**
	 * Permet de zoomer sur une entité
	 * @param entiteDto : l'entité sur laquelle zoomer
	 */
	@Command
	public void zoomSurEntite(@BindingParam("entity") EntiteDto entiteDto) {
		setSelectedEntiteDtoZoom(entiteDto);
		selectionneEntiteZoom();
	}

	@Command
	public void selectionneFiltreStatut() {
		if (this.selectedFiltreStatut != null) {

			// Si une entité était en édition, on vide pour ne pas se retrouver dans des cas bizarre d'entité ouverte en édition alors qu'elle n'est
			// pas présente avec le fitre actif
			setEntity(null);

			// On recharge toujours à partir d'ADS avant de filtrer pour être sur de tout récupérer à jour
			if (this.selectedFiltreStatut.equals(FiltreStatut.TOUS)) {
				// Si on est en zoom, on filtre uniquement sur les entités zoomées
				if (this.selectedEntiteDtoZoom != null) {
					entiteDtoRoot = adsWSConsumer.getEntiteWithChildren(this.selectedEntiteDtoZoom.getIdEntite());
				} else {
					entiteDtoRoot = adsWSConsumer.getCurrentTreeWithVDNRoot();
				}
			} else {
				// Si on est en zoom, on filtre uniquement sur les entités zoomées
				EntiteDto entiteDtoAFiltre = null;
				if (this.selectedEntiteDtoZoom != null) {
					entiteDtoAFiltre = adsWSConsumer.getEntiteWithChildren(this.selectedEntiteDtoZoom.getIdEntite());

				} else {
					entiteDtoAFiltre = adsWSConsumer.getCurrentTreeWithVDNRoot();
				}
				entiteDtoRoot = filtrerEntiteDtoRootParStatut(entiteDtoAFiltre, this.selectedFiltreStatut);
			}

			treeViewModel.creeArbre(entiteDtoRoot);
			notifyChange(LISTE_PROP_A_NOTIFIER_ENTITE);
			refreshListeEntiteRemplace();
			Clients.evalJavaScript("refreshOrganigrammeWithoutSelectedEntite();");
		}
	}

	/**
	 * Renvoi une arborescence composé uniquement des statuts correspondant au filtre passé en paramétre
	 * @param entiteDto : l'entité root de l'arbre à filtrer
	 * @param filtreStatut : le filtre a appliquer
	 * @return un arbre ne contenant que les statuts correspondant au filtre
	 */
	private EntiteDto filtrerEntiteDtoRootParStatut(EntiteDto entiteDto, FiltreStatut filtreStatut) {
		if (filtreStatut == null || entiteDto == null) {
			return null;
		}

		removeEntiteDtoIfNotInFiltre(entiteDto, filtreStatut);

		return entiteDto;
	}

	/**
	 * Méthode récursive qui parcoure les enfants et les enlève de l'arbre si ils ne sont pas dans un des statuts du filtre
	 * @param entiteDto : l'entité parcourue
	 * @param filtreStatut : le filtre
	 */
	private void removeEntiteDtoIfNotInFiltre(EntiteDto entiteDto, FiltreStatut filtreStatut) {
		List<EntiteDto> listeEnfant = new ArrayList<EntiteDto>();
		listeEnfant.addAll(entiteDto.getEnfants());
		// On se le statut de l'entité
		entiteDto.setStatut(Statut.getStatutById(entiteDto.getIdStatut()));
		for (EntiteDto entiteDtoEnfant : listeEnfant) {
			// On se le statut de l'entité
			entiteDtoEnfant.setStatut(Statut.getStatutById(entiteDtoEnfant.getIdStatut()));
			if (!filtreStatut.getListeStatut().contains(entiteDtoEnfant.getStatut())) {
				entiteDto.getEnfants().remove(entiteDtoEnfant);
			} else {
				removeEntiteDtoIfNotInFiltre(entiteDtoEnfant, filtreStatut);
			}
		}
	}

	public List<FiltreStatut> getListeFiltreStatut() {
		return Arrays.asList(FiltreStatut.values());
	}

	public String getComboVide() {
		return null;
	}

	public List<FichePosteDto> getListeFicheDePoste() {
		if (this.entity == null) {
			return null;
		}

		return sirhWsConsumer.getFichePosteByIdEntite(this.entity.getIdEntite());
	}

	@Command
	public void ouvrirPopupCreateExport(@BindingParam("entity") EntiteDto entiteDto) {
		ExportDto exportDto = new ExportDto();
		exportDto.setEntiteDto(entiteDto);
		Map<String, Object> args = new HashMap<>();
		args.put("exportDto", exportDto);
		Executions.createComponents("/layout/createExporGraphML.zul", null, null);
		BindUtils.postGlobalCommand(null, null, "ouvrePopupCreationExport", args);
	}
}
