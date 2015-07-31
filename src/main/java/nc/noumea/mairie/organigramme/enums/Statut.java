package nc.noumea.mairie.organigramme.enums;

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

import nc.noumea.mairie.organigramme.dto.EntiteDto;

/**
 * Le statut d'une {@link EntiteDto}
 */
public enum Statut {

	PREVISION("Prévision", 0), ACTIF("Actif", 1), TRANSITOIRE("Transitoire", 2), INACTIF("Inactif", 3);

	final String libelle;
	final Integer idStatut;

	Statut(String libelle, Integer idStatut) {
		this.libelle = libelle;
		this.idStatut = idStatut;
	}

	public static Statut getStatutById(Integer idStatut) {

		if (idStatut == null)
			return null;

		switch (idStatut) {
			case 0:
				return PREVISION;
			case 1:
				return ACTIF;
			case 2:
				return TRANSITOIRE;
			case 3:
				return INACTIF;
			default:
				return null;
		}
	}

	public String getName() {
		return this.name();
	}

	public String getLibelle() {
		return libelle;
	}

	public Integer getIdStatut() {
		return idStatut;
	}

	@Override
	public String toString() {
		return this.libelle;
	}
};
