package nc.noumea.mairie.organigramme.core.services;

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

import nc.noumea.mairie.organigramme.core.entity.AbstractEntity;

public interface GenericService<T> {

	T save(T abstractEntity);

	T saveOrUpdate(T abstractEntity);

	T update(T abstractEntity);

	void delete(AbstractEntity abstractEntity);

	void refresh(T abstractEntity);

	T findById(Long id);

	List<T> findAll();

	List<T> findAll(int nombreMaxResultat);

	List<T> findAllOrderBy(String orderByProperty);

	List<T> findAllOrderBy(String orderByProperty, boolean withNull);

	List<T> findAllByProperty(String property, Object value);

	T findSingleByProperty(String property, Object value);

	List<T> findAllByPropertyOrderBy(String property, Object value, String orderByProperty);

	List<T> findAllWithNull();

	List<Object> findAllAndReturnProperty(String property);

	Object selectMaxFromProperty(String property);

	Class<? extends T> getClasseReferente();
}
