package nc.noumea.mairie.organigramme.dto;

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
