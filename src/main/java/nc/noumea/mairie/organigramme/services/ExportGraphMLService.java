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

import java.util.Map;

import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.enums.FiltreStatut;

/**
 * Service pour gérer les exports graphML.
 * 
 * @author AgileSoft.NC
 */
public interface ExportGraphMLService {

	/**
	 * Exporte au format GraphML l'arbre ayant pour racine l'{@link EntiteDto}
	 * entiteDto
	 * 
	 * @param entiteDto
	 *            : l'entité à partir de laquelle on souhaite exporter
	 * @param filtreStatut
	 *            : le filtre en cours
	 * @param mapIdLiOuvert
	 *            : permet de savoir quel entité est dépliée
	 */
	void exportGraphMLFromEntite(EntiteDto entiteDto, FiltreStatut filtreStatut, Map<String, Boolean> mapIdLiOuvert);
}
