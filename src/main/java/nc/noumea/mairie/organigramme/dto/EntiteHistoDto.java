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
import nc.noumea.mairie.organigramme.enums.TypeHisto;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class EntiteHistoDto extends EntiteDto {

	private Integer	idEntiteHisto;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date	dateHisto;
	private Integer	idAgentHisto;
	private String	nomPrenomAgent;
	private Integer	typeHisto;

	public Integer getIdEntiteHisto() {
		return idEntiteHisto;
	}

	public void setIdEntiteHisto(Integer idEntiteHisto) {
		this.idEntiteHisto = idEntiteHisto;
	}

	public Date getDateHisto() {
		return dateHisto;
	}

	public void setDateHisto(Date dateHisto) {
		this.dateHisto = dateHisto;
	}

	public Integer getIdAgentHisto() {
		return idAgentHisto;
	}

	public void setIdAgentHisto(Integer idAgentHisto) {
		this.idAgentHisto = idAgentHisto;
	}

	public Integer getTypeHisto() {
		return typeHisto;
	}

	public void setTypeHisto(Integer typeHisto) {
		this.typeHisto = typeHisto;
	}

	public String getLibelleTypeHisto() {
		return typeHisto != null ? TypeHisto.getTypeHistoEnum(typeHisto).getLibelle() : "";
	}

	public String getNomPrenomAgent() {
		return nomPrenomAgent;
	}

	public void setNomPrenomAgent(String nomPrenomAgent) {
		this.nomPrenomAgent = nomPrenomAgent;
	}

}
