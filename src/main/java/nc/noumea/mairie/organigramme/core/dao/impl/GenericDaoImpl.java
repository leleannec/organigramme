package nc.noumea.mairie.organigramme.core.dao.impl;

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

import java.util.List;

import nc.noumea.mairie.organigramme.core.dao.GenericDao;
import nc.noumea.mairie.organigramme.core.dao.PersistentManager;
import nc.noumea.mairie.organigramme.core.entity.AbstractEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("genericDao")
public class GenericDaoImpl<T> implements GenericDao<T> {

	private static Logger log = LoggerFactory.getLogger(GenericDaoImpl.class);

	@Autowired
	protected PersistentManager<T> persistentManager;

	@Override
	public T save(T abstractEntity) {
		log.debug("Sauvegarde de l'entité : " + abstractEntity.toString());
		return persistentManager.save(abstractEntity);
	}

	@Override
	public T update(T abstractEntity) {
		return persistentManager.update(abstractEntity);
	}

	@Override
	public void delete(AbstractEntity abstractEntity) {
		try {
			persistentManager.delete(abstractEntity);
		} catch (Exception e) {
			throw new RuntimeException("Cette entité ne peut pas être supprimée car elle est utilisée par ailleurs");
		}
	}

	@Override
	public void refresh(T abstractEntity) {
		persistentManager.refresh(abstractEntity);
	}

	@Override
	public T findById(Class<? extends T> classe, Long id) {
		return persistentManager.findById(classe, id);
	}

	@Override
	public List<T> findAllByProperty(Class<? extends T> classe, String property, Object value) {
		return persistentManager.findAllByProperty(classe, property, value);
	}

	@Override
	public T findSingleByProperty(Class<? extends T> classe, String property, Object value) {
		return persistentManager.findSingleByProperty(classe, property, value);
	}

	@Override
	public List<T> findAllByPropertyOrderBy(Class<? extends T> classe, String property, Object value,
			String orderByProperty) {
		return persistentManager.findAllByPropertyOrderBy(classe, property, value, orderByProperty);
	}

	@Override
	public List<T> findAll(Class<? extends T> classe) {
		return persistentManager.findAll(classe);
	}

	@Override
	public List<T> findAll(Class<? extends T> classe, int nombreMaxResultat) {
		return persistentManager.findAll(classe, nombreMaxResultat);
	}

	@Override
	public List<T> findAllOrderBy(Class<? extends T> classe, String orderByProperty) {
		return persistentManager.findAllOrderBy(classe, orderByProperty);
	}

	@Override
	public Object selectMaxFromProperty(Class<? extends T> classe, String property) {
		return persistentManager.selectMaxFromProperty(classe, property);
	}

	@Override
	public List<Object> findAllAndReturnProperty(Class<? extends T> classe, String property) {
		return persistentManager.findAllAndReturnProperty(classe, property);
	}
}