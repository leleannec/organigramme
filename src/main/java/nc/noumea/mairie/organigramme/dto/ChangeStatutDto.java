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

import java.util.Date;

import nc.noumea.mairie.organigramme.core.utility.JsonDateDeserializer;
import nc.noumea.mairie.organigramme.core.utility.JsonDateSerializer;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class ChangeStatutDto {

	/**
	 * l id de l entite à modifier
	 */
	private Integer idEntite;

	/**
	 * l id du nouveau statut
	 */
	private Integer idStatut;

	/**
	 * TRUE pour changer le statut des entites fille egalement, sinon FALSE
	 */
	private boolean majEntitesEnfant;

	/**
	 * reference de la deliberation
	 */
	private String refDeliberation;

	/**
	 * date de la deliberation
	 */
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateDeliberation;

	/**
	 * l id de l agent effectuant le changement
	 */
	private Integer idAgent;

	public Integer getIdEntite() {
		return idEntite;
	}

	public void setIdEntite(Integer idEntite) {
		this.idEntite = idEntite;
	}

	public Integer getIdStatut() {
		return idStatut;
	}

	public void setIdStatut(Integer idStatut) {
		this.idStatut = idStatut;
	}

	public boolean isMajEntitesEnfant() {
		return majEntitesEnfant;
	}

	public void setMajEntitesEnfant(boolean majEntitesEnfant) {
		this.majEntitesEnfant = majEntitesEnfant;
	}

	public String getRefDeliberation() {
		return refDeliberation;
	}

	public void setRefDeliberation(String refDeliberation) {
		this.refDeliberation = refDeliberation;
	}

	public Date getDateDeliberation() {
		return dateDeliberation;
	}

	public void setDateDeliberation(Date dateDeliberation) {
		this.dateDeliberation = dateDeliberation;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}
}
