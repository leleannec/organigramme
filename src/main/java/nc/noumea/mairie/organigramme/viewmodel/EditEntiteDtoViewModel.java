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
import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;
import nc.noumea.mairie.organigramme.core.viewmodel.AbstractEditViewModel;
import nc.noumea.mairie.organigramme.core.ws.IAdsWSConsumer;
import nc.noumea.mairie.organigramme.core.ws.ISirhWSConsumer;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.dto.EntiteHistoDto;
import nc.noumea.mairie.organigramme.dto.FichePosteDto;
import nc.noumea.mairie.organigramme.dto.FichePosteTreeNodeDto;
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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Li;
import org.zkoss.zhtml.Ul;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Vlayout;

@Init(superclass = true)
@VariableResolver(DelegatingVariableResolver.class)
public class EditEntiteDtoViewModel extends AbstractEditViewModel<EntiteDto> implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(EditEntiteDtoViewModel.class);
	
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
	
	/** Le vlayout général dans lequel sera ajouté l'arbre **/
	Vlayout vlayoutTreeFichesPoste;
	Component view;

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
		afterCompose(getView());
	}

	/**
	 * Rafraichi l'entité depuis la base de donnée
	 * 
	 * @param entiteDto
	 *            : l'entité à rafraîchir
	 */
	@Command
	@NotifyChange({ "*" })
	public void refreshEntite(@BindingParam("entity") EntiteDto entiteDto) {
		refreshEntiteGeneric(entiteDto, true);
	}

	@GlobalCommand
	@NotifyChange({ "*" })
	public void refreshEntiteGlobalCommand(@BindingParam("entity") EntiteDto entiteDto) {
		if (entiteDto.getId().equals(this.entity.getId())) {
			refreshEntiteGeneric(entiteDto, false);
		}
	}

	private void refreshEntiteGeneric(EntiteDto entiteDto, boolean showNotification) {
		this.entity = adsWSConsumer.getEntiteWithChildren(entiteDto.getIdEntite());

		// On force à null pour que ce soit rafraîchi
		this.fichePosteGroupingModel = null;
		this.listeFichePoste = null;
		this.listeHistorique = null;

		setEntiteDirty(false);
		updateTitreOnglet();
		if (showNotification) {
			Clients.showNotification("Entité " + this.entity.getSigle() + " rafraîchie.", "info", null, "top_center", 0);
		}
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
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		
		if(null != view && null == getView()) {
			setView(view);
		}
		
		if(getOngletSelectionne().equals(EntiteOnglet.ORGANIGRAMME_FICHES_POSTE)) {
			vlayoutTreeFichesPoste = (Vlayout) view.getFellow("tb").getFellow("tabpanelTreeFichesPoste").getFellow("includeTreeFichesPoste")
					.getFellow("windowsTreeFichesPoste").getFellow("treeFichesPoste").getFellow("vlayoutTreeFichesPoste");
			
			refreshArbreComplet();
			
			try {
				Executions.createComponentsDirectly(
						createTreeFichesPoste(this.entity.getIdEntite()), 
						null, 
						view.getFellow("tb").getFellow("tabpanelTreeFichesPoste").getFellow("includeTreeFichesPoste")
						.getFellow("windowsTreeFichesPoste").getFellow("treeFichesPoste"), null);
			} catch (Exception e) {
				log.debug("Une erreur est survenue dans la creation du planning : " + e.getMessage());
			}
		}
	}
	
	private void refreshArbreComplet() {
		creeArbre(this.entity);
	}
	
	public void creeArbre(EntiteDto entiteDtoRoot) {

		List<FichePosteTreeNodeDto> listFichePosteTreeNodeDto = sirhWSConsumer.getTreeFichesPosteByEntite(entiteDtoRoot.getIdEntite());
		
		Vlayout vlayout = vlayoutTreeFichesPoste;
		Component arbre = genereArbre(listFichePosteTreeNodeDto, null, null);
		if (!CollectionUtils.isEmpty(vlayout.getChildren())) {
			vlayout.removeChild(vlayout.getChildren().get(0));
		}
		if (arbre == null) {
			return;
		}
		vlayout.appendChild(arbre);
	}
	
	private String createTreeFichesPoste(Integer idEntite) {
		
		StringBuffer result = new StringBuffer();
				
		result.append("<html>");
		result.append("<script type=\"text/javascript\">");
		result.append("		$(\"#organigramme-fichesPoste-"+idEntite+"\").orgChart({container: $('#chart"+idEntite+"')});");
		result.append("     $('#chart"+idEntite+"').kinetic();");
		result.append("     var hauteurFenetre = $(\"#panel-entier"+idEntite+"\").parent().parent().parent().height();");
		result.append("     $('#chart"+idEntite+"').height(hauteurFenetre - 10);");
		result.append("     $('#panel-entier"+idEntite+"').height(hauteurFenetre);");
		result.append("     $(window).trigger('resize');");
		result.append("</script>");
		result.append("<div id=\"panel-entier"+idEntite+"\">");
		result.append("<vlayout vflex=\"1\" hflex=\"1\"> ");
		result.append("<div id=\"chart"+idEntite+"\" class=\"chart\" /> ");
		result.append("</vlayout>");
		result.append("</div>");
		result.append("</html>");
		
		return result.toString();
	}
	
	/**
	 * Génére les composants {@link Ul}/{@link Li} qui forment l'arbre (ces
	 * {@link Ul}/{@link Li} html seront retravaillées par le plugin jQuery Org
	 * Chart afin de les transformer en div/table/tr/td et de les afficher sous
	 * forme d'arbre)
	 * 
	 * @param entiteDto
	 *            : le {@link EntiteDto} racine qui contient tout l'arbre
	 * @param mapIdTypeEntiteCouleurEntite
	 *            : la map des différentes couleurs pour les types d'entités qui
	 *            va nous permettre de setter chaque couleur de chaque type
	 *            d'entité
	 * @param mapIdTypeEntiteCouleurTexte
	 *            : la map des différentes couleurs pour les types d'entités qui
	 *            va nous permettre de setter chaque couleur de chaque type
	 *            d'entité
	 * @return le {@link Ul} de plus haut niveau qui est ajouté au
	 *         {@link Vlayout}
	 */
	public Component genereArbre(List<FichePosteTreeNodeDto> listFichePosteTreeNodeDto, Map<Long, String> mapIdTypeEntiteCouleurEntite,
			Map<Long, String> mapIdTypeEntiteCouleurTexte) {

		if (listFichePosteTreeNodeDto == null || listFichePosteTreeNodeDto.isEmpty()) {
			return null;
		}

		// On vide la map de correspondance id HTML<->EntiteDTO car on va la
		// renseigner dans creeLiEntite()
//		organigrammeViewModel.mapIdLiEntiteDto = new HashMap<String, EntiteDto>();

		// On recrée la liste de toutes les entités
//		organigrammeViewModel.setListeEntite(new ArrayList<EntiteDto>());

		// Initialisation de l'entité ROOT
		Ul ulRoot = new Ul();
		ulRoot.setId("organigramme-fichesPoste-" + this.entity.getIdEntite());
		ulRoot.setSclass("hide");
//		setCouleur(mapIdTypeEntiteCouleurEntite, mapIdTypeEntiteCouleurTexte, null);
//		for(FichePosteTreeNodeDto node : listFichePosteTreeNodeDto) {
//			
//			Li li = creeLiEntite(ulRoot, node);
//	//		organigrammeViewModel.getListeEntite().add(entiteDto);
//	
//			// Initialisation du reste de l'arbre
//			genereArborescenceHtml(node.getFichePostesEnfant(), li, mapIdTypeEntiteCouleurEntite, mapIdTypeEntiteCouleurTexte);
//
//		}
		FichePosteTreeNodeDto rootFictif = new FichePosteTreeNodeDto();
		rootFictif.setIdFichePoste(0);
		rootFictif.setNumero("0000/00");
		Li li = creeLiEntite(ulRoot, rootFictif);

		// Initialisation du reste de l'arbre
		genereArborescenceHtml(listFichePosteTreeNodeDto, li, mapIdTypeEntiteCouleurEntite, mapIdTypeEntiteCouleurTexte);

		
		// Maintenant qu'on a setté la liste de entités disponibles à la
		// recherche, on renseigne la ListModel
//		organigrammeViewModel.setEntiteDtoQueryListModelRecherchable(new EntiteDtoQueryListModel(organigrammeViewModel
//				.getListeEntite()));

		return ulRoot;
	}
	
	/**
	 * Crée une feuille unitaire et lui ajoute un événement onClick qui
	 * permettra d'effecture des opérations sur cette feuille
	 * 
	 * @param ul
	 *            : le {@link Ul} parent
	 * @param entiteDto
	 *            : l'{@link EntiteDto} a transformer en {@link Li}
	 * @return le composant {@link Li} représentant l'entité
	 */
	public Li creeLiEntite(Ul ul, final FichePosteTreeNodeDto node) {

		Li li = new Li();
		String numFichePoste = OrganigrammeUtil.splitByNumberAndSeparator(node.getNumero(), 8, "\n");
		
		Div divNumFichePoste = new Div();
		divNumFichePoste.appendChild(new Label(numFichePoste));
		li.appendChild(divNumFichePoste);
		Div divAgent = new Div();
		divAgent.appendChild(new Label(node.getAgent()));
		li.appendChild(divAgent);
		li.setParent(ul);
		li.setSclass("statut-" + StringUtils.lowerCase(node.getStatutFDP()) + " fichePoste");
//		li.setDynamicProperty("title", creeTooltipEntite(entiteDto));
//		boolean couleurEntiteTypeEntiteRenseigne = node.getTypeEntite() != null
//				&& !StringUtils.isBlank(entiteDto.getTypeEntite().getCouleurEntite());
//		if (couleurEntiteTypeEntiteRenseigne) {
//			li.setStyle("background-color:" + entiteDto.getTypeEntite().getCouleurEntite() + "; color:"
//					+ entiteDto.getTypeEntite().getCouleurTexte() + ";");
//		} else {
			li.setStyle("background-color:#FFFFCF; color:#000000;");
//		}

		li.setId("fiche-poste-id-" + node.getIdFichePoste().toString());
//		if (this.organigrammeViewModel.mapIdLiOuvert.get(li.getId()) == null) {
//			this.organigrammeViewModel.mapIdLiOuvert.put(li.getId(), false);
//		}

		// On maintient une map permettant d'aller plus vite lors d'un click
		// event pour retrouver l'EntiteDto correspondant à l'id du Li
//		organigrammeViewModel.mapIdLiEntiteDto.put(li.getId(), entiteDto);

		return li;
	}
	
	public Component genereArborescenceHtml(List<FichePosteTreeNodeDto> listeFichePosteTreeNodeDto, Component component,
			Map<Long, String> mapIdTypeEntiteCouleur, Map<Long, String> mapIdTypeEntiteCouleurTexte) {

		Ul ul = new Ul();
		ul.setParent(component);

		for (final FichePosteTreeNodeDto fichePosteTreeNode : listeFichePosteTreeNodeDto) {
//			setCouleur(mapIdTypeEntiteCouleur, mapIdTypeEntiteCouleurTexte, entiteDto);

			Li li = creeLiEntite(ul, fichePosteTreeNode);

//			organigrammeViewModel.getListeEntite().add(entiteDto);

			if (null != fichePosteTreeNode.getFichePostesEnfant()
					&& !fichePosteTreeNode.getFichePostesEnfant().isEmpty()) {
				genereArborescenceHtml(fichePosteTreeNode.getFichePostesEnfant(), li, mapIdTypeEntiteCouleur, mapIdTypeEntiteCouleurTexte);
			}
		}

		return ul;
	}

	public Vlayout getVlayoutTreeFichesPoste() {
		return vlayoutTreeFichesPoste;
	}

	public void setVlayoutTreeFichesPoste(Vlayout vlayoutTreeFichesPoste) {
		this.vlayoutTreeFichesPoste = vlayoutTreeFichesPoste;
	}

	public Component getView() {
		return view;
	}

	public void setView(Component view) {
		this.view = view;
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
