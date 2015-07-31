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
 * Modélise une transition de workflow (libellé, statut source, statut cible).
 * 
 * @author AgileSoft.NC
 */
public enum Transition {
	// @formatter:off
	TRANSITOIRE("Transitoire", Statut.ACTIF, Statut.TRANSITOIRE), ACTIF_APRES_PREVISION("Activer", Statut.PREVISION,
			Statut.ACTIF), INACTIF_APRES_TRANSITOIRE("Inactif", Statut.TRANSITOIRE, Statut.INACTIF), INACTIF_APRES_ACTIF(
			"Inactif", Statut.ACTIF, Statut.INACTIF); // @formatter:on

	String libelle;
	Statut statutSource;
	Statut statut;

	Transition(String libelle, Statut statutSource, Statut statut) {
		this.libelle = libelle;
		this.statut = statut;
		this.statutSource = statutSource;
	}

	/**
	 * @return libellé de la transition, lisible pour l'utilisateur
	 */
	public String getLibelle() {
		return this.libelle;
	}

	/**
	 * @return le statut cible de la transition
	 */
	public Statut getStatut() {
		return this.statut;
	}

	/**
	 * @return le statut source (de départ) de la transition
	 */
	public Statut getStatutSource() {
		return this.statutSource;
	}

	/**
	 * @return le nom de l'image sur le bouton, déduit du nom de la transition
	 */
	public String getButtonImage() {
		return "/imgs/icon/statut-" + this.statut.name().replaceAll("_", "-").toLowerCase() + ".png";
	}

	/**
	 * @return le nom de la transition, ex : "TRANSITOIRE"
	 */
	public String getName() {
		return this.name();
	}

	@Override
	public String toString() {
		return statutSource.getLibelle() + " -> " + statut.getLibelle();
	}
};
