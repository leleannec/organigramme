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

public class InfoFichePosteDto {

	private Integer nbFDP;
	private String titreFDP;
	private Double tauxETP;

	public Integer getNbFDP() {
		return nbFDP;
	}

	public void setNbFDP(Integer nbFDP) {
		this.nbFDP = nbFDP;
	}

	public String getTitreFDP() {
		return titreFDP;
	}

	public void setTitreFDP(String titreFDP) {
		this.titreFDP = titreFDP;
	}

	public Double getTauxETP() {
		return tauxETP;
	}

	public void setTauxETP(Double tauxETP) {
		this.tauxETP = tauxETP;
	}

}
