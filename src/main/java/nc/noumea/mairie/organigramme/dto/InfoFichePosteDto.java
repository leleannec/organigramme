package nc.noumea.mairie.organigramme.dto;

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
