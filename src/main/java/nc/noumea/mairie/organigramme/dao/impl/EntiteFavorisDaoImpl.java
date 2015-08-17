package nc.noumea.mairie.organigramme.dao.impl;

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
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import nc.noumea.mairie.organigramme.core.dao.impl.GenericDaoImpl;
import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;
import nc.noumea.mairie.organigramme.dao.EntiteFavorisDao;
import nc.noumea.mairie.organigramme.entity.EntiteFavoris;

import org.springframework.stereotype.Repository;

@Repository("entiteFavorisDao")
public class EntiteFavorisDaoImpl extends GenericDaoImpl<EntiteFavoris> implements EntiteFavorisDao {

	@Override
	public List<EntiteFavoris> findByIdAgent(Integer idAgent) {
		try {
			return findAllByProperty(EntiteFavoris.class, "idAgent", idAgent);
		} catch (NoResultException e) {
			return new ArrayList<EntiteFavoris>();
		}
	}

	@Override
	public EntiteFavoris findByIdAgentAndIdEntite(Integer idAgent, Integer idEntite) {
		String jpaQuery = "SELECT entiteFavoris FROM EntiteFavoris entiteFavoris WHERE entiteFavoris.idAgent = :idAgent AND entiteFavoris.idEntite = :idEntite";
		Query query = persistentManager.getEntityManager().createQuery(jpaQuery);
		OrganigrammeUtil.setParameter(query, "idAgent", idAgent);
		OrganigrammeUtil.setParameter(query, "idEntite", idEntite);
		return (EntiteFavoris) query.getSingleResult();
	}
}
