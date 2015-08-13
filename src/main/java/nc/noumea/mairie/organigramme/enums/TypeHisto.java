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

public enum TypeHisto {

	CREATION(0, "Création"), MODIFICATION(1, "Modification"), SUPPRESSION(2, "Suppression"), CHANGEMENT_STATUT(3,
			"Chgt. Statut"), CREATION_DUPLICATION(4, "Création via duplication");

	private Integer idRefTypeHisto;
	private String libelle;

	TypeHisto(Integer idRefTypeHisto, String libelle) {
		this.idRefTypeHisto = idRefTypeHisto;
		this.libelle = libelle;
	}

	public static TypeHisto getTypeHistoEnum(Integer idRefTypeHisto) {

		switch (idRefTypeHisto) {
			case 0:
				return CREATION;
			case 1:
				return MODIFICATION;
			case 2:
				return SUPPRESSION;
			case 3:
				return CHANGEMENT_STATUT;
			case 4:
				return CREATION_DUPLICATION;
			default:
				return null;
		}
	}

	public Integer getIdRefTypeHisto() {
		return idRefTypeHisto;
	}

	public String getLibelle() {
		return libelle;
	}
}
