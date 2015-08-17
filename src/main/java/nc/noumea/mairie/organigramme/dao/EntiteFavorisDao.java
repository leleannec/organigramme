package nc.noumea.mairie.organigramme.dao;

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
import nc.noumea.mairie.organigramme.entity.EntiteFavoris;

public interface EntiteFavorisDao extends GenericDao<EntiteFavoris> {

	/**
	 * Recherche la liste des favoris par agent
	 * 
	 * @param idAgent
	 *            : l'id de l'agent
	 * @return la liste des entités favoris de l'agent
	 */
	List<EntiteFavoris> findByIdAgent(Integer idAgent);

	/**
	 * Recherche un favoris pour un agent et une entité
	 * 
	 * @param idAgent
	 *            : l'id de l'agent
	 * @param idEntite
	 *            : l'id de l'entité
	 * @return un favoris pour un agent et une entité
	 */
	EntiteFavoris findByIdAgentAndIdEntite(Integer idAgent, Integer idEntite);
}
