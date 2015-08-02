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

import javax.persistence.OptimisticLockException;

import nc.noumea.mairie.organigramme.core.dto.AbstractEntityDto;
import nc.noumea.mairie.organigramme.core.entity.AbstractEntity;
import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;

/**
 * ViewModel abstrait parent des ViewModel de modification (qui permettent de
 * modifier une entité dans un onglet).
 * 
 * @author AgileSoft.NC
 * @param <T>
 *            Type paramétré (représente une classe d'entité en pratique)
 */
public abstract class AbstractEditViewModel<T extends AbstractEntityDto> extends AbstractViewModel<T> {

	private static Logger log = LoggerFactory.getLogger(AbstractEditViewModel.class);

	/**
	 * Initilisation du ViewModel : charge l'entité depuis la base de données
	 * 
	 * @param abstractEntity
	 *            entité concernée
	 */
	@Init
	public void initSetup(@ExecutionArgParam("entity") T abstractEntity) {
		this.entity = getService().findById(abstractEntity.getId());
	}

	/**
	 * Met à jour en base de données l'entité (typiquement sur clic d'un bouton
	 * enregistrer), et notifie par une commande globale la mise à jour de
	 * l'entité. Si l'entité présente des contraintes de violation, une popup
	 * apparait et la sauvegarde en base n'a pas lieu.
	 */
	@Command
	public void update() {
		if (showErrorPopup(this.entity)) {
			return;
		}

		try {
			this.entity = getService().update(this.entity);
		} catch (OptimisticLockException e) {
			showErrorPopup("Sauvegarde impossible, l'enregistrement a été modifié par un autre utilisateur (veuillez recharger et resaisir vos modifications)");
			log.warn("Modification concurrente (l'utilisateur a été averti avec un message compréhensible)", e);
		}

		showNotificationEntityEnregistre();

		notifyUpdateEntity();
		this.updateOnglet(this.entity);
	}

	protected void showNotificationEntityEnregistre() {
		showNotificationStandard(getMessageNotificationEnregistre());
	}

	protected String getMessageNotificationEnregistre() {
		return OrganigrammeUtil.getSimpleClassNameOfObject(entity) + " enregistré";
	}

	/**
	 * Propose à l'utilisateur la suppression de l'entité. S'il confirme,
	 * l'entité est supprimé, l'onglet est fermé et une commande globale est
	 * lancée pour prévenir de la suppression de l'entité.
	 */
	@Command
	public void delete() {
		Messagebox.show("Voulez-vous vraiment supprimer?", "Suppression", new Messagebox.Button[] {
				Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION,
				new EventListener<Messagebox.ClickEvent>() {

					@Override
					public void onEvent(ClickEvent evt) {
						if (evt.getName().equals("onYes")) {
							getService().delete(entity);
							fermeOnglet(entity);
							postGlobalCommandRefreshListe();
						}
					}
				});
	}

	/**
	 * Recharge l'entity depuis la base de données, si l'entity a été mise à
	 * jour par ailleurs.
	 * 
	 * @param abstractEntity
	 *            entité
	 */
	protected void reloadOnEntityUpdate(AbstractEntity abstractEntity) {
		if (abstractEntity == null) {
			return;
		}
		if (this.entity.getId().equals(abstractEntity.getId())
				&& abstractEntity.getVersion() > this.entity.getVersion()) {
			reloadEntity();
		}
	}

	/**
	 * Recharge l'entity depuis la base de données
	 */
	protected void reloadEntity() {
		this.entity = getService().findById(this.entity.getId());
	}

}
