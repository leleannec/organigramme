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

import java.util.Arrays;
import java.util.List;

/**
 * Les différentes combinaisons possibles pour trier l'organigramme par statut
 */
public enum FiltreStatut {

	ACTIF("Actif", Arrays.asList(Statut.ACTIF)), ACTIF_PREVISION("Actif + Prévision", Arrays.asList(Statut.ACTIF,
			Statut.PREVISION)), ACTIF_TRANSITOIRE("Actif + Transitoire", Arrays
			.asList(Statut.ACTIF, Statut.TRANSITOIRE)), ACTIF_TRANSITOIRE_INACTIF("Actif + Transitoire + Inactif",
			Arrays.asList(Statut.ACTIF, Statut.TRANSITOIRE, Statut.INACTIF)), TOUS("Tous", null);

	final String libelle;
	final List<Statut> listeStatut;

	FiltreStatut(String libelle, List<Statut> listeStatut) {
		this.libelle = libelle;
		this.listeStatut = listeStatut;
	}

	public String getLibelle() {
		return libelle;
	}

	public List<Statut> getListeStatut() {
		return listeStatut;
	}

	@Override
	public String toString() {
		return this.libelle;
	}
};
