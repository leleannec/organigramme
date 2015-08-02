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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import nc.noumea.mairie.organigramme.core.entity.AbstractEntity;
import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PersistentManager<T> {

	@PersistenceContext
	private EntityManager em;

	public T save(T abstractEntity) {
		if (abstractEntity == null) {
			return null;
		}
		em.persist(abstractEntity);
		return abstractEntity;
	}

	public T update(T genericEntity) {
		if (genericEntity == null) {
			return null;
		}
		return em.merge(genericEntity);
	}

	public void delete(AbstractEntity abstractEntity) {
		if (abstractEntity == null) {
			return;
		}
		if (em.contains(abstractEntity)) {
			em.remove(abstractEntity);
		} else {
			// On doit recharger l'objet en session avant de pouvoir le
			// supprimer sinon on aura une erreur "Removing a detached instance"
			T t = this.findById(abstractEntity.getClass(), abstractEntity.getId());
			em.remove(t);
		}

	}

	public void refresh(T abstractEntity) {
		if (abstractEntity == null) {
			return;
		}
		em.refresh(abstractEntity);
	}

	public List<T> findAllByProperty(Class<? extends T> classe, String property, Object value) {
		return findAllByPropertyOrderBy(classe, property, value, null);
	}

	public T findSingleByProperty(Class<? extends T> classe, String property, Object value) {
		TypedQuery<T> q = constructTypedQueryByPropertyOrderBy(classe, property, value, null);
		return q.getSingleResult();
	}

	public List<T> findAllByPropertyOrderBy(Class<? extends T> classe, String property, Object value,
			String orderByProperty) {
		TypedQuery<T> q = constructTypedQueryByPropertyOrderBy(classe, property, value, orderByProperty);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	private TypedQuery<T> constructTypedQueryByPropertyOrderBy(Class<? extends T> classe, String property,
			Object value, String orderByProperty) {
		CriteriaBuilder qb = em.getCriteriaBuilder();
		CriteriaQuery<T> c = (CriteriaQuery<T>) qb.createQuery(classe);
		Root<T> p = (Root<T>) c.from(classe);
		Predicate condition = qb.equal(p.get(property), value);
		c.where(condition);
		if (orderByProperty != null) {
			c.orderBy(qb.asc(p.get(orderByProperty)));
		}
		TypedQuery<T> q = em.createQuery(c);
		return q;
	}

	@SuppressWarnings("unchecked")
	public T findById(@SuppressWarnings("rawtypes") Class classe, Long id) {
		return (T) em.find(classe, id);
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll(@SuppressWarnings("rawtypes") Class classe) {
		return em.createQuery("SELECT res FROM " + OrganigrammeUtil.getSimpleNameOfClass(classe) + " res")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll(@SuppressWarnings("rawtypes") Class classe, int nombreMaxResultat) {
		return em.createQuery("SELECT res FROM " + OrganigrammeUtil.getSimpleNameOfClass(classe) + " res")
				.setMaxResults(nombreMaxResultat).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<T> findAllOrderBy(@SuppressWarnings("rawtypes") Class classe, String orderByProperty) {
		return em.createQuery(
				"SELECT res FROM " + OrganigrammeUtil.getSimpleNameOfClass(classe) + " res ORDER BY res."
						+ orderByProperty).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Object> findAllAndReturnProperty(@SuppressWarnings("rawtypes") Class classe, String property) {
		return em.createQuery(
				"SELECT res." + property + " FROM " + OrganigrammeUtil.getSimpleNameOfClass(classe) + " res")
				.getResultList();
	}

	public Object selectMaxFromProperty(@SuppressWarnings("rawtypes") Class classe, String property) {
		return em.createQuery(
				"SELECT MAX(res." + property + ") FROM " + OrganigrammeUtil.getSimpleNameOfClass(classe) + " res")
				.getSingleResult();
	}

	public EntityManager getEntityManager() {
		return em;
	}
}
