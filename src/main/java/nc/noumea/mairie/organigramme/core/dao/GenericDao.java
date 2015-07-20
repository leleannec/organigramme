package nc.noumea.mairie.organigramme.core.dao;

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

public interface GenericDao<T> {

	T save(T abstractEntity);

	T update(T abstractEntity);

	void delete(AbstractEntity abstractEntity);

	void refresh(T abstractEntity);

	T findById(Class<? extends T> abstractEntity, Long id);

	List<T> findAllByProperty(Class<? extends T> abstractEntity, String property, Object value);

	T findSingleByProperty(Class<? extends T> abstractEntity, String property, Object value);

	List<T> findAll(Class<? extends T> abstractEntity);

	List<T> findAll(Class<? extends T> abstractEntity, int nombreMaxResultat);

	List<T> findAllOrderBy(Class<? extends T> abstractEntity, String orderByProperty);

	List<Object> findAllAndReturnProperty(Class<? extends T> abstractEntity, String property);

	Object selectMaxFromProperty(Class<? extends T> abstractEntity, String property);

	List<T> findAllByPropertyOrderBy(Class<? extends T> classe, String property, Object value, String orderByProperty);
}