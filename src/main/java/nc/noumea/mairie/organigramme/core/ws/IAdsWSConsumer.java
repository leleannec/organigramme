package nc.noumea.mairie.organigramme.core.ws;

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
import java.util.Map;

import nc.noumea.mairie.organigramme.dto.ChangeStatutDto;
import nc.noumea.mairie.organigramme.dto.DuplicationDto;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.dto.EntiteHistoDto;
import nc.noumea.mairie.organigramme.dto.ReturnMessageDto;
import nc.noumea.mairie.organigramme.dto.TypeEntiteDto;

public interface IAdsWSConsumer {

	/**
	 * Renvoi l'entité root ainsi que tous ces enfants représentant l'arbre en
	 * cours
	 * 
	 * @return l'entité root ainsi que tous ces enfants
	 */
	EntiteDto getCurrentTreeWithVDNRoot();

	/**
	 * Appel le webservice ADS d'enregistrement de l'{@link EntiteDto} passé en
	 * paramètre
	 * 
	 * @param entiteDto
	 *            : l'{@link EntiteDto} a créer ou à mettre à jour
	 * @return le {@link ReturnMessageDto} contenant les erreurs ou les infos
	 */
	ReturnMessageDto saveOrUpdateEntite(EntiteDto entiteDto);

	/**
	 * Renvoi la liste des types d'entités
	 * 
	 * @return la liste des types d'entités
	 */
	List<TypeEntiteDto> getListeTypeEntite();

	/**
	 * Appel le webservice ADS d'enregistrement du {@link TypeEntiteDto} passé
	 * en paramètre
	 * 
	 * @param typeEntiteDto
	 *            : le {@link TypeEntiteDto} a créer ou à mettre à jour
	 * @return le {@link ReturnMessageDto} contenant les erreurs ou les infos
	 */
	ReturnMessageDto saveOrUpdateTypeEntite(TypeEntiteDto typeEntiteDto);

	/**
	 * Appel le webservice ADS de suppression du {@link TypeEntiteDto} passé en
	 * paramètre
	 * 
	 * @param typeEntiteDto
	 *            : le {@link TypeEntiteDto} a supprimer
	 * @return le {@link ReturnMessageDto} contenant les erreurs ou les infos
	 */
	ReturnMessageDto deleteTypeEntite(TypeEntiteDto typeEntiteDto);

	/**
	 * Appel le webservice ADS de suppression de l'{@link EntiteDto} passé en
	 * paramètre
	 * 
	 * @param entiteDto
	 *            : l'{@link EntiteDto} a supprimer
	 * @return le {@link ReturnMessageDto} contenant les erreurs ou les infos
	 */
	ReturnMessageDto deleteEntite(EntiteDto entiteDto);

	/**
	 * Appel le webservice ADS de changement de statut d'une entité
	 * 
	 * @param changeStatutDto
	 *            : le dto contenant toutes les informations nécessaires
	 * @return le {@link ReturnMessageDto} contenant les erreurs ou les infos
	 */
	ReturnMessageDto changeStatut(ChangeStatutDto changeStatutDto);

	/**
	 * Appel le webservice ADS de récupération d'une {@link EntiteDto} à partir
	 * de son id
	 * 
	 * @param idEntite
	 *            : l'id de l'{@link EntiteDto} qu'on souhaite récupérer
	 * @return l'{@link EntiteDto}
	 */
	EntiteDto getEntite(Integer idEntite);

	/**
	 * Renvoi l'entité dont l'id est en paramètre ainsi que tous ces enfants
	 * 
	 * @param idEntite
	 *            : l'id de l'{@link EntiteDto} qu'on souhaite récupérer
	 * @return l'entité ainsi que tous ces enfants
	 */
	EntiteDto getEntiteWithChildren(Integer idEntite);

	/**
	 * Renvoi l'historique de l'entité
	 * 
	 * @param idEntite
	 *            : l'entité pour laquelle on souhaite l'historique
	 * @param mapIdAgentNomPrenom
	 *            : la map contenant les id des agents et leurs noms prénoms
	 * @return l'historique de l'entité
	 */
	List<EntiteHistoDto> getListeEntiteHisto(Integer idEntite, Map<Integer, String> mapIdAgentNomPrenom);

	/**
	 * Duplique une entité ainsi que ses enfants (si besoin) vers une entité
	 * cible
	 * 
	 * @param duplicationDto
	 *            : le DTO permettant de dupliquer
	 * @return le {@link ReturnMessageDto} contenant les erreurs ou les infos
	 */
	ReturnMessageDto dupliqueEntite(DuplicationDto duplicationDto);
}
