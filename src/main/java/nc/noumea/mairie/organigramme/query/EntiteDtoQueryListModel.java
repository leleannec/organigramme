package nc.noumea.mairie.organigramme.query;

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

import nc.noumea.mairie.organigramme.core.query.AbstractQueryListModel;
import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;
import nc.noumea.mairie.organigramme.dto.EntiteDto;

/**
 * Classe pour gérer les combo filtrées d'{@link EntiteDto}
 */
public class EntiteDtoQueryListModel extends AbstractQueryListModel<EntiteDto> {

	private static final long serialVersionUID = 1L;

	private List<EntiteDto> listeEntite;

	public EntiteDtoQueryListModel(List<EntiteDto> listeEntite) {
		super(listeEntite);
		this.listeEntite = listeEntite;
	}

	/**
	 * @param chaineRecherche
	 *            partie du sigle de l'entité
	 * @param nombreResultatMaxIndicatif
	 *            ignoré
	 * @return une liste d'entité DTO qui ont un sigle qui contient la chaîne
	 *         désaccentuée (sans tenir compte de sa casse)
	 */
	@Override
	public List<EntiteDto> findByQuery(String chaineRecherche, int nombreResultatMaxIndicatif) {
		List<EntiteDto> result = new ArrayList<EntiteDto>();
		for (EntiteDto entiteDto : listeEntite) {
			if (OrganigrammeUtil.majusculeSansAccentTrim(entiteDto.getSigle()).contains(
					OrganigrammeUtil.majusculeSansAccentTrim(chaineRecherche))) {
				result.add(entiteDto);
			}
		}

		return result;
	}
}
