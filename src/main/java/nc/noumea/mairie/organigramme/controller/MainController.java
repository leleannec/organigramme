package nc.noumea.mairie.organigramme.controller;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.organigramme.core.entity.AbstractEntity;
import nc.noumea.mairie.organigramme.core.event.FermeOngletAbstractEntityEvent;
import nc.noumea.mairie.organigramme.core.event.OuvreOngletAbstractEntityEvent;
import nc.noumea.mairie.organigramme.core.event.RechargeOngletAbstractEntityEvent;
import nc.noumea.mairie.organigramme.core.event.UpdateOngletAbstractEntityEvent;
import nc.noumea.mairie.organigramme.core.services.AuthentificationService;
import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;
import nc.noumea.mairie.organigramme.core.utility.ZkUtil;
import nc.noumea.mairie.organigramme.dto.ProfilAgentDto;

import org.apache.commons.lang.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;

/**
 * MainController permettant de gérer tous les comportements généraux de
 * l'application. Construction de la sidebar, gestion des ouvertures d'onglets,
 * ...
 * 
 * @author Thomas
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MainController extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;

	@Wire
	Grid sideBarGrid;

	@Wire
	Borderlayout mainBorderLayout;

	@WireVariable
	AuthentificationService authentificationService;

	/**
	 * Rempli la sidebar avec les items par défaut de l'application
	 * 
	 * @param comp
	 *            Component description
	 */
	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		ProfilAgentDto currentUser = authentificationService.getCurrentUser();
		if (currentUser != null) {
			mainBorderLayout.setVisible(true);
		} else {
			messageBoxAccesRefuse();
			return;
		}

		// to initial view after view constructed.
		Rows rows = sideBarGrid.getRows();
		List<SidebarItem> listeItems = this.initSideBar();

		// Si on est administrateur on ouvre l'onglet de paramètrage des type
		// d'entités
		if (currentUser.isAdministrateur()) {
			// Onglet à afficher par défaut
			ouvreOnglet(null, "Types d'entité", "/layout/listeTypeEntite.zul", true, false, null);
		}

		for (SidebarItem item : listeItems) {

			// certains items sont réservés uniquement à l'administrateur :
			if (item.isOnlyAdmin() && !currentUser.isAdministrateur()) {
				continue;
			}

			Row row = constructSidebarRow(item.getLabel(), item.getIconUri(), item.getUri());
			rows.appendChild(row);
			if (item.isOngletFixe()) {
				// Onglet à afficher par défaut
				ouvreOnglet(null, item.getLabel(), item.getUri(), true, false, null);
			}
		}

		if (currentUser.isAdministrateur()) {
			// se positionne sur le premier onglet
			getTabbox().setSelectedIndex(1);
		} else {
			// se positionne sur le premier onglet
			getTabbox().setSelectedIndex(0);
		}

		// On n'affiche pas la le menu de gauche
		sideBarGrid.getParent().setVisible(false);

		EventQueues.lookup("organigrammeQueue", EventQueues.DESKTOP, true).subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				if (event instanceof OuvreOngletAbstractEntityEvent) {
					OuvreOngletAbstractEntityEvent ouvreOngletAbstractEntityEvent = (OuvreOngletAbstractEntityEvent) event;

					AbstractEntity abstractEntity = ouvreOngletAbstractEntityEvent.getAbstractEntity();
					Integer selectedTabIndex = ouvreOngletAbstractEntityEvent.getSelectedTabIndex();

					ouvreOnglet(abstractEntity, abstractEntity.getLibelleCourt(), getEditViewURI(abstractEntity),
							false, false, selectedTabIndex);
				}
				if (event instanceof FermeOngletAbstractEntityEvent) {
					AbstractEntity abstractEntity = ((FermeOngletAbstractEntityEvent) event).getAbstractEntity();
					fermeOnglet(abstractEntity);
				}
				if (event instanceof RechargeOngletAbstractEntityEvent) {
					RechargeOngletAbstractEntityEvent rechargeEvent = (RechargeOngletAbstractEntityEvent) event;
					rechargeOnglet(rechargeEvent.getAbstractEntity(), rechargeEvent.getSelectedTabIndex());
				}
				if (event instanceof UpdateOngletAbstractEntityEvent) {
					AbstractEntity abstractEntity = ((UpdateOngletAbstractEntityEvent) event).getAbstractEntity();
					String suffixe = ((UpdateOngletAbstractEntityEvent) event).getSuffixe();
					updateLibelleOnglet(abstractEntity, suffixe);
				}
			}

		});
	}

	private void messageBoxAccesRefuse() {
		Messagebox
				.show("Vous avez bien été authentifié, mais votre compte n'est pas configuré pour vous permettre d'accéder à l'application. Veuillez demander les droits à l'administrateur",
						"Accès refusé", new Messagebox.Button[] { Messagebox.Button.OK }, Messagebox.ERROR,
						new EventListener<Messagebox.ClickEvent>() {
							@Override
							public void onEvent(ClickEvent evt) {
								authentificationService.logout();
							}
						});
	}

	private String getEditViewURI(AbstractEntity abstractEntity) {
		return "/layout/edit" + OrganigrammeUtil.getSimpleClassNameOfObject(abstractEntity) + ".zul";
	}

	/**
	 * Initialise la liste des items de la sidebar
	 * 
	 * @return la liste contenant les items à ajouter à la sidebar
	 */
	private List<SidebarItem> initSideBar() {

		List<SidebarItem> listeItems = new ArrayList<SidebarItem>();
		// @formatter:off
		listeItems.add(new SidebarItem("Organigramme", "/imgs/icon/organigramme.png", "/layout/organigramme.zul", true,
				false));
		// @formatter:on

		return listeItems;
	}

	/**
	 * Renvoie un item (i.e. une ligne) à ajouter dans la sidebar
	 * 
	 * @param label
	 *            : le libellé de l'item
	 * @param imageSrc
	 *            : l'url de l'icone
	 * @param locationUri
	 *            : l'url de la vue à afficher
	 * @return une ligne prêt a être ajoutée à la sidebar
	 */
	private Row constructSidebarRow(final String label, final String imageSrc, final String locationUri) {

		// construct component and hierarchy
		Row row = new Row();
		Image image = new Image(imageSrc);
		Label lab = new Label(label);

		row.appendChild(image);
		row.appendChild(lab);

		// set style attribute
		row.setSclass("sidebar-fn");

		// new and register listener for events
		EventListener<Event> onActionListener = new SerializableEventListener<Event>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onEvent(Event event) throws Exception {
				// redirect current url to new location
				if (locationUri.startsWith("http")) {
					// open a new browser tab
					Executions.getCurrent().sendRedirect(locationUri);
					return;
				}

				ouvreOnglet(null, label, locationUri, false, false, null);
			}
		};
		row.addEventListener(Events.ON_CLICK, onActionListener);

		return row;
	}

	/**
	 * Recharge l'onglet couramment sélectionné
	 * 
	 * @param abstractEntity
	 */
	private void rechargeOnglet(AbstractEntity abstractEntity, Integer selectedTabIndex) {
		if (abstractEntity == null) {
			return;
		}
		Tabbox tabbox = getTabbox();
		Tab tab = tabbox.getSelectedTab();
		if (tab == null) {
			return;
		}
		Tabpanel tabPanel = tabbox.getSelectedPanel();

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("entity", abstractEntity);

		Components.removeAllChildren(tabPanel);
		Executions.createComponents(getEditViewURI(abstractEntity), tabPanel, map);

		selectionneSousOnglet(abstractEntity, selectedTabIndex);
	}

	private void selectionneSousOnglet(AbstractEntity abstractEntity, Integer selectedTabIndex) {
		if (abstractEntity == null || selectedTabIndex == null) {
			return;
		}
		// sélectionne le sous-onglet
		final Map<String, Object> mapSelectionneSousOnglet = new HashMap<String, Object>();
		mapSelectionneSousOnglet.put("entity", abstractEntity);
		mapSelectionneSousOnglet.put("selectedTabIndex", selectedTabIndex);
		BindUtils.postGlobalCommand(null, null, "selectionneSousOnglet", mapSelectionneSousOnglet);
	}

	private Tabbox getTabbox() {
		Page currentPage = sideBarGrid.getPage();
		Tabs mainTabboxTabs = getMainTabboxTabs(currentPage);
		return (Tabbox) mainTabboxTabs.getParent();
	}

	/**
	 * Ouvre un onglet et le sélectionne (ou le sélectionne uniquement si il est
	 * déjà ouvert)
	 * 
	 * @param abstractEntity
	 *            : l'entity rattachée à l'onglet. null si c'est un onglet
	 *            "simple" sans entity
	 * @param labelOnglet
	 *            : le libellé a afficher dans l'onglet
	 * @param locationUri
	 *            : l'url de la vue
	 * @param selectedTabIndex
	 */
	private void ouvreOnglet(final AbstractEntity abstractEntity, final String labelOnglet, final String locationUri,
			final boolean ongletFixe, boolean disabled, Integer selectedTabIndex) {
		Page currentPage = sideBarGrid.getPage();
		Tabs mainTabboxTabs = getMainTabboxTabs(currentPage);
		Tabpanels mainTabboxTabPanels = getMainTabboxTabPanels(currentPage);
		Tabbox tabbox = getTabbox();

		// important de recharger pour limiter les problèmes de modifications
		// concurrentes (pour éviter bug du style #7642)
		if (selectionneOngletSiPresent(abstractEntity, labelOnglet, mainTabboxTabs, tabbox)) {
			rechargeOnglet(abstractEntity, selectedTabIndex);
			return;
		}

		// Si l'onglet n'est pas déjà ouvert on le crée
		Tab tab = new Tab();
		tab.setLabel(labelOnglet);
		if (abstractEntity != null) {
			String className = OrganigrammeUtil.getSimpleClassNameOfObject(abstractEntity);
			tab.setAttribute(className + "ongletId", abstractEntity.getId());
		} else {
			tab.setAttribute("ongletId", labelOnglet);
		}
		tab.setClosable(!ongletFixe);
		mainTabboxTabs.appendChild(tab);

		Tabpanel tabPanel = new Tabpanel();
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("entity", abstractEntity);

		mainTabboxTabPanels.appendChild(tabPanel);
		Executions.createComponents(locationUri, tabPanel, map);

		if (disabled) {
			ZkUtil.disableComponentAndChildren(tabPanel);
		}

		tabbox.setSelectedTab(tab);
		selectionneSousOnglet(abstractEntity, selectedTabIndex);
	}

	public void fermeOnglet(AbstractEntity abstractEntity) {
		if (abstractEntity != null) {
			String className = OrganigrammeUtil.getSimpleClassNameOfObject(abstractEntity);
			Page currentPage = sideBarGrid.getPage();
			Tabs mainTabboxTabs = getMainTabboxTabs(currentPage);
			for (Tab tab : mainTabboxTabs.<Tab> getChildren()) {
				if (abstractEntity.getId() != null
						&& abstractEntity.getId().equals(tab.getAttribute(className + "ongletId"))) {
					tab.close();
					return;
				}
			}
		}
	}

	/**
	 * Met à jour le libellé de l'onglet qui gère l'entity passée en argument
	 * 
	 * @param abstractEntity
	 *            abstractEntity description
	 * @param suffixe
	 *            : un suffixe facultatif
	 */
	public void updateLibelleOnglet(AbstractEntity abstractEntity, String suffixe) {
		if (abstractEntity == null) {
			return;
		}

		String className = OrganigrammeUtil.getSimpleClassNameOfObject(abstractEntity);
		Page currentPage = sideBarGrid.getPage();
		Tabs mainTabboxTabs = getMainTabboxTabs(currentPage);
		for (Tab tab : mainTabboxTabs.<Tab> getChildren()) {
			if (abstractEntity.getId() != null
					&& abstractEntity.getId().equals(tab.getAttribute(className + "ongletId"))) {
				tab.setLabel(abstractEntity.getLibelleCourt() + StringUtils.trimToEmpty(suffixe));
				return;
			}
		}
	}

	private boolean selectionneOngletSiPresent(AbstractEntity abstractEntity, String labelOnglet, Tabs mainTabboxTabs,
			Tabbox tabbox) {
		if (abstractEntity != null) {
			// Sélectionne l'onglet si il est déjà ouvert (en fonction de son
			// id)
			String className = OrganigrammeUtil.getSimpleClassNameOfObject(abstractEntity);
			for (Tab tab : mainTabboxTabs.<Tab> getChildren()) {
				if (abstractEntity.getId() != null
						&& abstractEntity.getId().equals(tab.getAttribute(className + "ongletId"))) {
					tabbox.setSelectedTab(tab);
					return true;
				}
			}
		} else {
			// Sélectionne l'onglet si il est déjà ouvert (en fonction de son
			// label)
			for (Tab tab : mainTabboxTabs.<Tab> getChildren()) {
				if (labelOnglet != null && labelOnglet.equals(tab.getAttribute("ongletId"))) {
					tabbox.setSelectedTab(tab);
					return true;
				}
			}
		}
		return false;
	}

	private Tabpanels getMainTabboxTabPanels(Page currentPage) {
		return (Tabpanels) Selectors.iterable(currentPage, "#mainTabbox-tabpanels").iterator().next();
	}

	private Tabs getMainTabboxTabs(Page currentPage) {
		return (Tabs) Selectors.iterable(currentPage, "#mainTabbox-tabs").iterator().next();
	}

	static class SidebarItem {
		String label;
		String iconUri;
		String uri;
		boolean ongletFixe;
		boolean onlyAdmin;

		public SidebarItem(String label, String iconUri, String uri, boolean ongletFixe, boolean onlyAdmin) {
			this.label = label;
			this.iconUri = iconUri;
			this.uri = uri;
			this.ongletFixe = ongletFixe;
			this.onlyAdmin = onlyAdmin;
		}

		public String getLabel() {
			return label;
		}

		public String getIconUri() {
			return iconUri;
		}

		public String getUri() {
			return uri;
		}

		public boolean isOngletFixe() {
			return ongletFixe;
		}

		public boolean isOnlyAdmin() {
			return onlyAdmin;
		}
	}
}
