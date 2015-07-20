package nc.noumea.mairie.organigramme.core.services.impl;

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
import java.util.List;

import nc.noumea.mairie.organigramme.core.dao.GenericDao;
import nc.noumea.mairie.organigramme.core.entity.AbstractEntity;
import nc.noumea.mairie.organigramme.core.services.GenericService;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class GenericServiceImpl<T> implements GenericService<T> {

	@Autowired
	GenericDao<T>	genericDao;

	@Override
	public T save(T abstractEntity) {
		return genericDao.save(abstractEntity);
	}

	@Override
	public T saveOrUpdate(T abstractEntity) {
		if (((AbstractEntity) abstractEntity).getId() != null) {
			return update(abstractEntity);
		} else {
			return save(abstractEntity);
		}
	}

	@Override
	public T update(T abstractEntity) {
		return genericDao.update(abstractEntity);
	}

	@Override
	public void delete(AbstractEntity abstractEntity) {
		genericDao.delete(abstractEntity);
	}

	@Override
	public void refresh(T abstractEntity) {
		genericDao.refresh(abstractEntity);
	}

	@Override
	public T findById(Long id) {
		return genericDao.findById(getClasseReferente(), id);
	}

	@Override
	public List<T> findAll() {
		return genericDao.findAll(getClasseReferente());
	}

	@Override
	public List<T> findAll(int nombreMaxResultat) {
		return genericDao.findAll(getClasseReferente(), nombreMaxResultat);
	}

	@Override
	public List<T> findAllOrderBy(String orderByProperty) {
		return findAllOrderBy(orderByProperty, false);
	}

	@Override
	public List<T> findAllOrderBy(String orderByProperty, boolean withNull) {
		List<T> result = genericDao.findAllOrderBy(getClasseReferente(), orderByProperty);
		if (withNull) {
			result.add(0, null);
		}
		return result;
	}

	@Override
	public List<T> findAllWithNull() {
		List<T> listeAbstractEntity = this.findAll();
		listeAbstractEntity.add(0, null);
		return listeAbstractEntity;
	}

	@Override
	public List<T> findAllByProperty(String property, Object value) {
		return genericDao.findAllByProperty(getClasseReferente(), property, value);
	}

	@Override
	public T findSingleByProperty(String property, Object value) {
		return genericDao.findSingleByProperty(getClasseReferente(), property, value);
	}

	@Override
	public List<T> findAllByPropertyOrderBy(String property, Object value, String orderByProperty) {
		return genericDao.findAllByPropertyOrderBy(getClasseReferente(), property, value, orderByProperty);
	}

	@Override
	public List<Object> findAllAndReturnProperty(String property) {
		return genericDao.findAllAndReturnProperty(getClasseReferente(), property);
	}

	@Override
	public Object selectMaxFromProperty(String property) {
		return genericDao.selectMaxFromProperty(getClasseReferente(), property);
	}

	/**
	 * Implémentation par défaut, peut-être redéfinie si besoin
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends T> getClasseReferente() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

}
