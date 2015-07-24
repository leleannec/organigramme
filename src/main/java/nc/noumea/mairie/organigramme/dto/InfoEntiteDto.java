package nc.noumea.mairie.organigramme.dto;

import java.util.ArrayList;
import java.util.List;

public class InfoEntiteDto {

	public InfoEntiteDto() {
		super();
		this.listeInfoFDP = new ArrayList<InfoFichePosteDto>();
	}

	private Integer idEntite;
	private List<InfoFichePosteDto> listeInfoFDP;

	public Integer getIdEntite() {
		return idEntite;
	}

	public void setIdEntite(Integer idEntite) {
		this.idEntite = idEntite;
	}

	public List<InfoFichePosteDto> getListeInfoFDP() {
		return listeInfoFDP;
	}

	public void setListeInfoFDP(List<InfoFichePosteDto> listeInfoFDP) {
		this.listeInfoFDP = listeInfoFDP;
	}

}
