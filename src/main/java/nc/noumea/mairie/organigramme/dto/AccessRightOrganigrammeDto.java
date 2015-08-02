package nc.noumea.mairie.organigramme.dto;

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

public class AccessRightOrganigrammeDto {

	private boolean visualisation;
	private boolean edition;
	private boolean administrateur;

	public AccessRightOrganigrammeDto() {
	}

	public boolean isVisualisation() {
		return administrateur || visualisation;
	}

	public void setVisualisation(boolean visualisation) {
		this.visualisation = visualisation;
	}

	public boolean isEdition() {
		return administrateur || edition;
	}

	public void setEdition(boolean edition) {
		this.edition = edition;
	}

	public boolean isAdministrateur() {
		return administrateur;
	}

	public void setAdministrateur(boolean administrateur) {
		this.administrateur = administrateur;
	}

	public boolean isAucunRole() {
		return !this.administrateur && !this.edition && !this.visualisation;
	}

	@Override
	public String toString() {
		return "Visualisation : " + visualisation + "; Edition : " + edition + "; Administrateur: " + administrateur;
	}
}
