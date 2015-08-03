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
import nc.noumea.mairie.organigramme.dto.DuplicationDto;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.enums.Statut;

public interface OrganigrammeService extends GenericService<EntiteDto> {

	/**
	 * Met à jour le statut de l'entité
	 * 
	 * @param entiteDto
	 *            {@link EntiteDto} concerné
	 * @param statutCible
	 *            Statut cible (opération ignorée si null)
	 * @return true si l'entité a changé de statut
	 */
	boolean updateStatut(EntiteDto entiteDto, Statut statutCible);

	/**
	 * Supprime l'{@link EntiteDto} de l'arbre
	 * 
	 * @param entiteDto
	 *            : l'{@link EntiteDto} a supprimer
	 * @return true si l'entité a bien été supprimée
	 */
	boolean deleteEntite(EntiteDto entiteDto);

	/**
	 * Renvoi la liste de toutes les {@link EntiteDto} qui ne sont pas en
	 * prévision
	 * 
	 * @return la liste de toutes les {@link EntiteDto} qui ne sont pas en
	 *         prévision
	 */
	List<EntiteDto> findAllNotPrevision();

	/**
	 * Renvoi la liste de toutes les {@link EntiteDto} qui sont en statut actif
	 * ou prévision
	 * 
	 * @return la liste de toutes les {@link EntiteDto} qui sont en statut actif
	 *         ou prévision
	 */
	List<EntiteDto> findAllActifOuPrevision();

	/**
	 * Duplique une entité ainsi que ses enfants (si besoin) vers une entité
	 * cible
	 * 
	 * @param duplicationDto
	 *            : le DTO permettant de dupliquer
	 */
	boolean dupliqueEntite(DuplicationDto duplicationDto);
}
