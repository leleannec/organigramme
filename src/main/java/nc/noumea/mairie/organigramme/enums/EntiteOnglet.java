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

/**
 * Les différents onglets disponibles sur une entité
 */
public enum EntiteOnglet {

	CARACTERISTIQUE(0),
	FDP(1),
	HISTORIQUE(2);

	final int	position;

	public static EntiteOnglet getEntiteOngletByPosition(int position) {

		switch (position) {
			case 0:
				return CARACTERISTIQUE;
			case 1:
				return FDP;
			case 2:
				return HISTORIQUE;
			default:
				return null;
		}
	}

	EntiteOnglet(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

};
