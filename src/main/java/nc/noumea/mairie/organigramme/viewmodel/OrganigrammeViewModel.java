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
import nc.noumea.mairie.organigramme.dto.DuplicationDto;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.dto.ExportDto;
import nc.noumea.mairie.organigramme.dto.ProfilAgentDto;
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

	private static final long serialVersionUID = 1L;

	public static Logger log = LoggerFactory.getLogger(OrganigrammeViewModel.class);

	// @formatter:off
	@WireVariable
	IAdsWSConsumer adsWSConsumer;
	@WireVariable
	ISirhWSConsumer sirhWSConsumer;
	@WireVariable
	ExportGraphMLService exportGraphMLService;
	@WireVariable
	OrganigrammeService organigrammeService;
	@WireVariable
	AuthentificationService authentificationService;
	@WireVariable
	CouleurTypeEntiteService couleurTypeEntiteService;
	@WireVariable
	TypeEntiteService typeEntiteService;
	@WireVariable
	ReturnMessageService returnMessageService;
	// @formatter:on

	private static final String CREATE_ENTITE_VIEW = "/layout/createEntite.zul";

	private static final String[] LISTE_PROP_A_NOTIFIER_ENTITE = new String[] { "statut", "entity", "creable",
			"duplicable", "listeTransitionAutorise", "listeEntite", "mapIdLiEntiteDto", "selectedEntiteDtoRecherche",
			"selectedEntiteDtoZoom", "entiteDtoQueryListModel", "selectedFiltreStatut" };

	private OrganigrammeWorkflowViewModel organigrammeWorkflowViewModel = new OrganigrammeWorkflowViewModel(this);
	public TreeViewModel treeViewModel = new TreeViewModel(this);

	/** Le vlayout général dans lequel sera ajouté l'arbre **/
	Vlayout vlayout;

	/** L'{@link EntiteDto} représentant l'arbre au complet **/
	EntiteDto entiteDtoRoot;

	/** L'{@link EntiteDto} représentant l'entité recherchée **/
	EntiteDto selectedEntiteDtoRecherche;

	/** L'{@link EntiteDto} représentant l'entité zommé **/
	EntiteDto selectedEntiteDtoZoom;

	/**
	 * Le {@link FiltreStatut} représentant le filtre actif, par défaut, on
	 * affiche les ACTIFS #17105
	 **/
	FiltreStatut selectedFiltreStatut = FiltreStatut.ACTIF;

	/**
	 * Map permettant rapidement d'accèder à un nomPrenom d'agent à partir de
	 * son id agent
	 **/
	Map<Integer, String> mapIdAgentNomPrenom = new HashMap<Integer, String>();

	/**
	 * Map permettant rapidement d'accèder à une {@link EntiteDto} à partir de
	 * son id html client
	 **/
	Map<String, EntiteDto> mapIdLiEntiteDto;

	/**
	 * Map permettant de savoir si le Li est ouvert ou non à partir de son id
	 * html client
	 **/
	Map<String, Boolean> mapIdLiOuvert;

	/** Le currentUser connecté **/
	ProfilAgentDto profilAgentDto;

	/** Liste de toutes les entités zoomables et/ou recherchables **/
	private List<EntiteDto> listeEntite = new ArrayList<EntiteDto>();

	/** ListModel de toutes les entités zoomables et/ou recherchables **/
	private EntiteDtoQueryListModel entiteDtoQueryListModel;

	public List<EntiteDto> getListeEntite() {

		// On filtre la liste des entités zoomables et accessibles selon le
		// statut selectionné
		if (this.selectedFiltreStatut != null && !this.selectedFiltreStatut.equals(FiltreStatut.TOUS)) {
			List<EntiteDto> listeEntiteClone = new ArrayList<EntiteDto>();
			listeEntiteClone.addAll(this.listeEntite);
			for (EntiteDto entiteDto : listeEntiteClone) {
				if (!this.selectedFiltreStatut.getListeStatut().contains(entiteDto.getStatut())) {
					listeEntite.remove(entiteDto);
				}
			}
		}

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
	 * Point d'entrée du viewModel. Fait un appel à ADS afin de récupérer
	 * l'arbre et le crée
	 * 
	 * @param view
	 *            : la vue permettant de récuperer le {@link Vlayout} dans
	 *            lequel sera ajouté l'arbre
	 */
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		// permet de gérer le onClickEntite
		Selectors.wireEventListeners(view, this);

		mapIdLiOuvert = new HashMap<String, Boolean>();
		vlayout = (Vlayout) Selectors.iterable(view, "#organigramme").iterator().next();
		profilAgentDto = authentificationService.getCurrentUser();

		// On recharge l'arbre complet d'ADS et on rafraichi le client. Ainsi on
		// est sur d'avoir une version bien à jour
		refreshArbreComplet();
	}

	/**
	 * Crée l'arbre et l'ajoute au {@link Vlayout} client et rafraichi le client
	 */
	@GlobalCommand
	public void creeArbreEtRafraichiClient() {
		refreshArbreComplet();
		Clients.evalJavaScript("refreshOrganigramme();");
	}

	@GlobalCommand
	public void refreshOrganigrammeSuiteAjout(@BindingParam("entiteDto") EntiteDto entiteDto) {
		refreshArbreComplet();
		Clients.evalJavaScript("refreshOrganigrammeSuiteAjout('" + entiteDto.getLi().getId() + "');");
	}

	@GlobalCommand
	public void refreshOrganigrammeWithoutSelectedEntite() {
		refreshArbreComplet();
		Clients.evalJavaScript("refreshOrganigrammeWithoutSelectedEntite();");
	}

	private void refreshArbreComplet() {
		majEntiteRootByFiltreAndZoom();
		treeViewModel.creeArbre(entiteDtoRoot);
		notifyChange(LISTE_PROP_A_NOTIFIER_ENTITE);
	}

	/**
	 * Evenement qui se déclenche lors d'un click sur une entité côté client
	 * 
	 * @param event
	 *            : l'évenement click qui contient dans getData() l'id du li
	 *            selectionné
	 */
	@Listen("onClickEntite = #organigramme")
	public void onClickEntite(Event event) {
		EntiteDto entiteDto = mapIdLiEntiteDto.get(event.getData());
		EntiteDto entiteDtoFromBdd = entiteDto;
		if (entiteDto != null) {
			entiteDtoFromBdd = adsWSConsumer.getEntiteWithChildren(entiteDto.getIdEntite());
			entiteDtoFromBdd.setLi(entiteDto.getLi());
		}
		setEntity(entiteDtoFromBdd);
		notifyChange(LISTE_PROP_A_NOTIFIER_ENTITE);
	}

	/**
	 * Evenement qui se déclenche lors d'un double click sur une entité côté
	 * client
	 * 
	 * @param event
	 *            : l'évenement click qui contient dans getData() l'id du li
	 *            selectionné
	 */
	@Listen("onDblClickEntite = #organigramme")
	public void onDblClickEntite(Event event) {
		EntiteDto entiteDto = mapIdLiEntiteDto.get(event.getData());
		ouvreOnglet(entiteDto, 0);
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
	 * 
	 * @param listeEntiteDto
	 *            : la liste à parcourir
	 * @param ouvert
	 *            : ouvert ou fermé
	 */
	private void setLiOuvertOuFermeArbre(List<EntiteDto> listeEntiteDto, boolean ouvert) {
		for (EntiteDto entiteDto : listeEntiteDto) {
			mapIdLiOuvert.put(entiteDto.getLi().getId(), ouvert);
			setLiOuvertOuFermeArbre(entiteDto.getEnfants(), ouvert);
		}
	}

	/**
	 * Initialise un {@link EntiteDto} avec son parent et rend visible la popup
	 * de création de l'entité
	 * 
	 * @param entiteDto
	 *            : le {@link EntiteDto} parent
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Command
	public void createEntite(@BindingParam("entity") EntiteDto entiteDto) throws InstantiationException,
			IllegalAccessException {

		if (!profilAgentDto.isEdition()) {
			return;
		}

		// Le parent doit être en statut P ou A
		if (entiteDto.getStatut() != Statut.PREVISION && entiteDto.getStatut() != Statut.ACTIF) {
			AbstractViewModel
					.showErrorPopup("Vous ne pouvez créer une entité que si son parent est dans l'état Actif ou Prévision");
			return;
		}

		EntiteDto newEntiteDto = new EntiteDto();
		newEntiteDto.setEntiteParent(entiteDto);
		initPopupEdition(newEntiteDto, CREATE_ENTITE_VIEW);
	}

	/**
	 * Recharge l'arbre complet, rafraichi le client et selectionne l'entité
	 * crée
	 * 
	 * @param entiteDtoParent
	 *            : l'entité parente
	 * @param newEntiteDto
	 *            : la nouvelle entitée
	 */
	@GlobalCommand
	public void refreshArbreSuiteAjout(@BindingParam("entiteDtoParent") EntiteDto entiteDtoParent,
			@BindingParam("newEntiteDto") EntiteDto newEntiteDto) {

		// Comme on est en train de créer une entité en statut prévisionnel, on
		// force l'affichage du statut pour pouvoir voir cette nouvelle entité
		boolean filtreStatutPrevisionVisible = selectedFiltreStatut != null
				&& (selectedFiltreStatut.equals(FiltreStatut.ACTIF_PREVISION) || selectedFiltreStatut
						.equals(FiltreStatut.TOUS));
		if (!filtreStatutPrevisionVisible) {
			setSelectedFiltreStatut(FiltreStatut.ACTIF_PREVISION);
		}
		refreshArbreComplet();

		// Vu qu'on vient de reconstruire l'arbre complet on recharge le nouveau
		// DTO
		newEntiteDto = OrganigrammeUtil.findEntiteDtoDansArbreById(entiteDtoRoot, newEntiteDto.getIdEntite(), null);

		setEntity(newEntiteDto);
		mapIdLiOuvert.put(newEntiteDto.getLi().getId(), false);
		// Appel de la fonction javascript correspondante
		Clients.evalJavaScript("refreshOrganigrammeSuiteAjout('" + newEntiteDto.getLi().getId() + "', '"
				+ entiteDtoParent.getLi().getId() + "');");

		if (!filtreStatutPrevisionVisible) {
			Messagebox.show("Le filtre d'affichage a été changé pour vous permettre de visualiser la nouvelle entité");
		}

		ouvreOnglet(newEntiteDto, 0);
	}

	/**
	 * Déplie {@link EntiteDto} côté client.
	 * 
	 * @param entiteDto
	 *            : l'{@link EntiteDto} à déplier
	 */
	@Command
	public void deplierEntite(@BindingParam("entity") EntiteDto entiteDto) {
		Clients.evalJavaScript("expandEntiteFromIdDiv('" + entiteDto.getLi().getId() + "');");
	}

	/**
	 * Exporte au format GraphML l'arbre ayant pour racine l'{@link EntiteDto}
	 * entiteDto
	 * 
	 * @param exportDto
	 *            : l'exportDto contenant l'entité à partir de laquelle on
	 *            souhaite exporter et si on souhaite ou non exporter les FDP
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
	 * Effectue un changement d'état sur l'entité
	 * 
	 * @param transition
	 *            Transition concernée
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

		notifyChange(listePropANotifier);

		// Appel de la fonction javascript correspondante
		Clients.evalJavaScript("refreshOrganigramme();");
	}

	/**
	 * L'entité est-elle créable ?
	 * 
	 * @return true si l'entité est créable, false sinon
	 */
	public boolean isCreable() {
		// On ne peux créer que si on a le rôle édition
		return profilAgentDto.isEdition() && this.entity != null;
	}

	/**
	 * L'entité est-elle duplicable ?
	 * 
	 * @return true si l'entité est duplicable, false sinon
	 */
	public boolean isDuplicable() {
		// On ne peux créer que si on a le rôle édition
		return profilAgentDto.isEdition() && this.entity != null
				&& (this.entity.isPrevision() || this.entity.isActif());
	}

	/**
	 * Met à jour l'entité avec les dates et les références de délibérations
	 * saisies et passe la transition
	 * 
	 * @param entiteDto
	 *            : l'{@link EntiteDto} sur laquelle on souhaite passer la
	 *            transition
	 * @param transition
	 *            : la transition a passer
	 * @param popup
	 *            : la popup de changement de statut
	 */
	@GlobalCommand
	public void saveStatutWithRefAndDateGenerique(@BindingParam("entity") EntiteDto entiteDto,
			@BindingParam("transition") final Transition transition, @BindingParam("popup") final Window popup) {

		if (entiteDto == null || !OrganigrammeUtil.sameIdAndNotNull(entiteDto.getId(), this.entity.getId())) {
			return;
		}

		if (transition.getStatut().equals(Statut.ACTIF)) {
			String messageConfirmation = "Êtes-vous sur de vouloir passer cette entité en statut 'ACTIF' ? Plus aucune information de cette entité ne pourra être modifiée.";
			Messagebox.show(messageConfirmation, "Confirmation", new Messagebox.Button[] { Messagebox.Button.YES,
					Messagebox.Button.NO }, Messagebox.EXCLAMATION, new EventListener<Messagebox.ClickEvent>() {
				@Override
				public void onEvent(ClickEvent evt) {
					if (evt.getName().equals("onYes")) {
						if (organigrammeWorkflowViewModel.executerTransitionGeneric(transition)) {
							popup.detach();
						}
					}
				}
			});
		} else {
			if (organigrammeWorkflowViewModel.executerTransitionGeneric(transition)) {
				popup.detach();
			}
		}
	}

	public Map<String, EntiteDto> getMapIdLiEntiteDto() {
		return mapIdLiEntiteDto;
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
		refreshArbreComplet();
		Clients.evalJavaScript("refreshOrganigrammeReplie();");
	}

	@Command
	public void selectionneEntiteZoom() {
		if (this.selectedEntiteDtoZoom != null) {
			setEntity(null);
			notifyChange(LISTE_PROP_A_NOTIFIER_ENTITE);
			refreshArbreComplet();
			Clients.evalJavaScript("refreshOrganigrammeSuiteZoom();");
		}
	}

	/**
	 * Permet de zoomer sur une entité
	 * 
	 * @param entiteDto
	 *            : l'entité sur laquelle zoomer
	 */
	@Command
	public void zoomSurEntite(@BindingParam("entity") EntiteDto entiteDto) {
		setSelectedEntiteDtoZoom(entiteDto);
		selectionneEntiteZoom();
	}

	@Command
	public void selectionneFiltreStatut() {
		if (this.selectedFiltreStatut != null) {

			// Si une entité était en édition, on vide pour ne pas se retrouver
			// dans des cas bizarre d'entité ouverte en édition alors qu'elle
			// n'est
			// pas présente avec le fitre actif
			setEntity(null);
			majEntiteRootByFiltreAndZoom();
			treeViewModel.creeArbre(entiteDtoRoot);
			notifyChange(LISTE_PROP_A_NOTIFIER_ENTITE);
			Clients.evalJavaScript("refreshOrganigrammeWithoutSelectedEntite();");
		}
	}

	private void majEntiteRootByFiltreAndZoom() {
		// On recharge toujours à partir d'ADS avant de filtrer pour être sur de
		// tout récupérer à jour
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
	}

	/**
	 * Renvoi une arborescence composé uniquement des statuts correspondant au
	 * filtre passé en paramétre
	 * 
	 * @param entiteDto
	 *            : l'entité root de l'arbre à filtrer
	 * @param filtreStatut
	 *            : le filtre a appliquer
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
	 * Méthode récursive qui parcoure les enfants et les enlève de l'arbre si
	 * ils ne sont pas dans un des statuts du filtre
	 * 
	 * @param entiteDto
	 *            : l'entité parcourue
	 * @param filtreStatut
	 *            : le filtre
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

	@Command
	public void ouvrirPopupCreateExport(@BindingParam("entity") EntiteDto entiteDto) {
		ExportDto exportDto = new ExportDto();
		exportDto.setEntiteDto(entiteDto);
		Map<String, Object> args = new HashMap<>();
		args.put("exportDto", exportDto);
		Executions.createComponents("/layout/createExportGraphML.zul", null, null);
		BindUtils.postGlobalCommand(null, null, "ouvrePopupCreationExport", args);
	}

	@Command
	public void ouvrirPopupCreateDuplication(@BindingParam("entity") EntiteDto entiteDto) {
		DuplicationDto duplicationDto = new DuplicationDto();
		duplicationDto.setEntiteDto(entiteDto);
		Map<String, Object> args = new HashMap<>();
		args.put("duplicationDto", duplicationDto);
		Executions.createComponents("/layout/createDuplication.zul", null, null);
		BindUtils.postGlobalCommand(null, null, "ouvrePopupCreationDuplication", args);
	}
}
