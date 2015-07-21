package nc.noumea.mairie.organigramme.services;

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

import nc.noumea.mairie.organigramme.core.services.GenericService;
import nc.noumea.mairie.organigramme.dto.TypeEntiteDto;

public interface TypeEntiteService extends GenericService<TypeEntiteDto> {

	/**
	 * Renvoie la liste des types d'entités actifs triés par nom
	 * @return la liste des types d'entités actifs triés par nom
	 */
	List<TypeEntiteDto> getListeTypeEntiteActif();

	/**
	 * Renvoie la liste des types d'entités actifs et inactifs triés par nom (d'abord les actifs, puis les inactifs postfixés par "(inactif)"
	 * @return la liste des types d'entités actifs et inactifs triés par nom
	 */
	List<TypeEntiteDto> getListeTypeEntiteActifInactif();
}
