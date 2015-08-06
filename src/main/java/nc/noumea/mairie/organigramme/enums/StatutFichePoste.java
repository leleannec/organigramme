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


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public enum StatutFichePoste {
	EN_CREATION("1", "En création"), VALIDEE("2", "Validée"), INACTIVE("4", "Inactive"), TRANSITOIRE("5", "Transitoire"), GELEE(
			"6", "Gelée");

	/** L'attribut qui contient le libelle long associe a l'enum */
	private final String libLong;
	private final String id;

	/** Le constructeur qui associe une valeur a l'enum */
	private StatutFichePoste(String id, String libLong) {
		this.libLong = libLong;
		this.id = id;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getLibLong() {
		return this.libLong;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getId() {
		return this.id;
	}

	/**
	 * Renvoi la liste des ids de statut actif séparé par des virgules (utile
	 * pour le WS SIRH)
	 * 
	 * @return : la liste des ids de statut actif séparé par des virgules
	 */
	public static String getListIdStatutActif() {
		List<String> listIdStatutActive = new ArrayList<String>();
		for (StatutFichePoste statutFichePoste : StatutFichePoste.values()) {
			if (!statutFichePoste.equals(StatutFichePoste.INACTIVE)) {
				listIdStatutActive.add(statutFichePoste.getId());
			}
		}

		return StringUtils.join(listIdStatutActive, ",");
	}
}
