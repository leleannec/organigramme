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

import nc.noumea.mairie.organigramme.core.dto.AbstractEntityDto;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

/**
 * ViewModel abstrait parent des ViewModel de liste (qui permettent de
 * visualiser dans une grille des entités).
 * 
 * @author AgileSoft.NC
 * @param <T>
 *            Type paramétré (représente une classe d'entité en pratique)
 */
public abstract class AbstractListeViewModel<T extends AbstractEntityDto> extends AbstractViewModel<T> {

	protected ListModelList<T> listeEntity;

	/**
	 * @return la liste des entités affichées
	 */
	public ListModelList<T> getListeEntity() {
		return listeEntity;
	}

	public void setListeEntity(ListModelList<T> listeEntity) {
		this.listeEntity = listeEntity;
	}

	@Init
	public void init() {
		listeEntity = new ListModelList<T>();
	}

	/**
	 * Méthode à redéfinir, en cas de volumétrie importante
	 */
	public void refreshListe() {
		listeEntity.clear();
		listeEntity.addAll(getService().findAll()); // par défaut, charge tout
	}

	/**
	 * Met à jour l'entity dans la liste (si elle s'y trouve)
	 * 
	 * @param abstractEntity
	 *            entité concernée
	 */
	protected void updateEntity(T abstractEntity) {
		if (abstractEntity == null) {
			return;
		}
		boolean removed = listeEntity.remove(abstractEntity);
		if (removed) {
			listeEntity.add(0, abstractEntity);
		}
	}
}
