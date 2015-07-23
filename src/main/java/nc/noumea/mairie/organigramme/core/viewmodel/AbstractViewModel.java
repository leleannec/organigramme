package nc.noumea.mairie.organigramme.core.viewmodel;

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

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.organigramme.core.dto.AbstractEntityDto;
import nc.noumea.mairie.organigramme.core.entity.AbstractEntity;
import nc.noumea.mairie.organigramme.core.event.FermeOngletAbstractEntityEvent;
import nc.noumea.mairie.organigramme.core.event.OuvreOngletAbstractEntityEvent;
import nc.noumea.mairie.organigramme.core.event.RechargeOngletAbstractEntityEvent;
import nc.noumea.mairie.organigramme.core.event.UpdateOngletAbstractEntityEvent;
import nc.noumea.mairie.organigramme.core.services.GenericService;
import nc.noumea.mairie.organigramme.core.utility.ApplicationContextUtils;
import nc.noumea.mairie.organigramme.core.utility.MessageErreur;
import nc.noumea.mairie.organigramme.core.utility.MessageErreurUtil;
import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 * ViewModel abstrait parent des ViewModel de l'application qui manipulent une entité (création, modification, et même liste où on considère que l'entité est
 * celle sélectionnée dans la liste)
 * 
 * @author AgileSoft.NC
 * @param <T> Type paramétré (représente une classe d'entité en pratique)
 */
public abstract class AbstractViewModel<T extends AbstractEntityDto> {

	private static Logger	log	= LoggerFactory.getLogger(AbstractViewModel.class);

	protected T				entity;

	/**
	 * @return l'entité concernée
	 */
	public T getEntity() {
		return entity;
	}

	/**
	 * Fixe l'entité concerné par le ViewModel
	 * 
	 * @param entity entité concernée
	 */
	public void setEntity(T entity) {
		this.entity = entity;
	}

	/**
	 * Méthode utilitaire, pour lister les valeurs d'une énumération (dans l'ordre de leur déclaration).
	 * 
	 * @param enumClassName nom complet de la classe (avec le package, ex : "nc.noumea.mairie.organigramme.enums.Civilite")
	 * @return la liste des valeurs énumérées, dans l'ordre de leur déclaration.
	 */
	public ListModelList<?> getListeEnum(String enumClassName) {
		return getListeEnum(enumClassName, false);
	}

	/**
	 * Méthode utilitaire, pour lister les valeurs d'une énumération (dans l'ordre de leur déclaration), avec la possibilité d'insérer en tête la valeur null.
	 * 
	 * @param enumClassName nom complet de la classe (avec le package, ex : "nc.noumea.mairie.organigramme.enums.Civilite")
	 * @param insertNull indique s'il faut insérer en tête de la liste résultat la valeur null
	 * @return la liste des valeurs énumérées, dans l'ordre de leur déclaration (avec null en tête optionnellement)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ListModelList<?> getListeEnum(String enumClassName, boolean insertNull) {
		try {
			Class<?> classe = Class.forName(enumClassName);
			ListModelList result = new ListModelList(classe.getEnumConstants());
			if (insertNull) {
				result.add(0, null);
			}
			return result;
		} catch (ClassNotFoundException e) {
			log.error("erreur sur getListeEnum", e);
			Messagebox.show(e.toString());
		}
		return null;
	}

	/**
	 * Publie un évènement de demande d'ouverture d'une entity dans un nouvel onglet (ou dans un onglet existant si l'entity est déjà ouverte)
	 * 
	 * @param abstractEntity entité concernée
	 * @param selectedTabIndex index de l'onglet à ouvrir
	 */
	@Command
	public void ouvreOnglet(@BindingParam("entity") T abstractEntity, Integer selectedTabIndex) {
		EventQueues.lookup("organigrammeQueue", EventQueues.DESKTOP, true).publish(new OuvreOngletAbstractEntityEvent(abstractEntity, selectedTabIndex));
	}

	/**
	 * Publie un évènement de demande de fermeture d'un onglet qui concerne une entity
	 * 
	 * @param abstractEntity entité concernée
	 */
	public void fermeOnglet(@BindingParam("entity") T abstractEntity) {
		EventQueues.lookup("organigrammeQueue", EventQueues.DESKTOP, true).publish(new FermeOngletAbstractEntityEvent(abstractEntity));
	}

	/**
	 * Publie un évènement de demande de recharge de l'entity, dans l'onglet couramment sélectionné
	 */
	@Command
	public void rechargeOnglet() {
		rechargeOnglet(null);
	}

	/**
	 * Publie un évènement de demande de recharge de l'entity, dans l'onglet couramment sélectionné (ou dans l'onglet indiqué)
	 * @param indexSousOnglet si null ignoré, permet d'indiquer un éventuel sous-onglet sur lequel se positionner après rechargement de l'entity
	 */
	protected void rechargeOnglet(Integer indexSousOnglet) {
		EventQueues.lookup("organigrammeQueue", EventQueues.DESKTOP, true).publish(new RechargeOngletAbstractEntityEvent(entity, indexSousOnglet));
	}

	/**
	 * Publie un évènement de demande de mise à jour du libellé de l'onglet qui gère l'entity passée en argument.
	 * 
	 * @param abstractEntity entité concernée
	 */
	@Command
	public void updateOnglet(@BindingParam("entity") T abstractEntity) {
		EventQueues.lookup("organigrammeQueue", EventQueues.DESKTOP, true).publish(new UpdateOngletAbstractEntityEvent(abstractEntity));
	}

	/**
	 * Retourne le service spring associée à la classe de l'entité
	 * 
	 * @return le service spring associée à la classe de l'entité
	 */
	@SuppressWarnings("unchecked")
	public GenericService<T> getService() {
		return (GenericService<T>) ApplicationContextUtils.getApplicationContext().getBean(StringUtils.uncapitalize(getEntityName()) + "Service");
	}

	/**
	 * Retourne le nom simple de la classe de l'entité T
	 * 
	 * @return ex : "EntiteDTO"
	 */
	public String getEntityName() {
		return OrganigrammeUtil.getSimpleNameOfClass(getEntityClass());
	}

	/**
	 * Retourne la classe paramétrée de l'AbstractViewModel courant.
	 * 
	 * @return la classe paramétrée de l'AbstractViewModel courant.
	 */
	@SuppressWarnings("unchecked")
	private Class<T> getEntityClass() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * Instancie un nouvel objet T
	 * 
	 * @return instance créée
	 * @throws InstantiationException en cas d'erreur à la création de l'entity
	 * @throws IllegalAccessException en cas d'erreur à la création de l'entity
	 */
	protected T createEntity() throws InstantiationException, IllegalAccessException {
		return getEntityClass().newInstance();
	}

	/**
	 * Poste une commande globale pour signaler la mise à jour de l'entity gérée par le ViewModel courant, en précisant en argument l'entité concernée. Exemple
	 * de commande globale : "updateEntiteDto"
	 */
	public void notifyUpdateEntity() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("entity", entity);
		BindUtils.postGlobalCommand(null, null, "update" + getEntityName(), args);
	}

	/**
	 * Poste une commande globale pour signaler la mise à jour d'une entity quelconque. Exemple de commande globale : "updateDemandeur"
	 * 
	 * @param entityUpdated entity concernée
	 */
	protected void notifyUpdateEntity(AbstractEntity entityUpdated) {
		if (entityUpdated == null) {
			return;
		}
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("entity", entityUpdated);
		BindUtils.postGlobalCommand(null, null, "update" + OrganigrammeUtil.getSimpleClassNameOfObject(entityUpdated), args);
	}

	public static void notifyChange(String prop, Object bean) {
		BindUtils.postNotifyChange(null, null, bean, prop);
	}

	public void notifyChange(String prop) {
		notifyChange(prop, this);
	}

	public static void notifyChange(String[] listProperty, Object bean) {
		if (listProperty == null) {
			return;
		}
		if (Executions.getCurrent() == null) {
			return;
		}
		for (String prop : listProperty) {
			if (!StringUtils.isBlank(prop)) {
				notifyChange(prop, bean);
			}
		}
	}

	public void notifyChange(String[] listProperty) {
		notifyChange(listProperty, this);
	}

	public void postGlobalCommandRefreshListe() {
		BindUtils.postGlobalCommand(null, null, "refreshListe" + getEntityName(), null);
	}

	/**
	 * Affiche une popup d'erreur, concernant une liste d'erreurs (si la liste est null ou fait 0 élément, la méthode ne fait rien)
	 * 
	 * @param listeMessageErreur liste de messages d'erreur à afficher
	 * @return true si au moins un message a été affiché
	 */
	public static boolean showErrorPopup(List<MessageErreur> listeMessageErreur) {
		if (CollectionUtils.isEmpty(listeMessageErreur)) {
			return false;
		}
		Messagebox.show(MessageErreurUtil.construitReprListeMessageErreur(listeMessageErreur), "Erreur", Messagebox.OK, Messagebox.ERROR);
		return true;
	}

	/**
	 * Affiche une popup modale de messages d'erreur concernant l'entité (les erreurs spécifiques métier et les violations de contraintes observées sur
	 * l'entité)
	 * 
	 * @param entity entité concernée
	 * @return true si des erreurs ont été affiches, false si aucune erreur
	 */
	public static boolean showErrorPopup(AbstractEntity entity) {
		return showErrorPopup(entity.construitListeMessageErreur());
	}

	/**
	 * Affiche une popup d'erreur, concernant une erreur unique
	 * 
	 * @param message Message d'erreur (si le message est "blanc", la méthode ne fait rien)
	 * @return true si un message (non vide) a été affiché, false sinon
	 */
	public static boolean showErrorPopup(String message) {
		if (StringUtils.isBlank(message)) {
			return false;
		}
		List<MessageErreur> listeMessageErreur = new ArrayList<>();
		listeMessageErreur.add(new MessageErreur(message));
		return AbstractViewModel.showErrorPopup(listeMessageErreur);
	}

	public void showNotificationStandard(String message) {
		Clients.showNotification(message, "info", null, "top_center", 0);
	}

	public Integer getMaxLength(Object object, String property) throws Exception {
		return OrganigrammeUtil.getMaxLength(object, property);
	}

	public Integer getMaxLengthClassProperty(String className, String property) throws Exception {
		return OrganigrammeUtil.getMaxLengthClassProperty(className, property);
	}

	public void ouvrePopupCreation(String template) throws InstantiationException, IllegalAccessException {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("entity", getEntityClass().newInstance());
		Window window = (Window) Executions.createComponents(template, null, arguments);
		window.doModal();
	}

	public void initPopupEdition(T entity, String template) {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("entity", entity);
		Executions.createComponents(template, null, arguments);

		BindUtils.postGlobalCommand(null, null, "ouvrePopupEdition" + getEntityName(), arguments);
	}
}
