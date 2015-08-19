package nc.noumea.mairie.organigramme.dto;

import java.util.ArrayList;
import java.util.List;

public class FichePosteTreeNodeDto extends FichePosteDto {

	private Integer idFichePoste;
	private Integer idFichePosteParent;
	private Integer idAgent;
	private List<FichePosteTreeNodeDto> fichePostesEnfant;
	private FichePosteTreeNodeDto fichePosteParent;
	
	public FichePosteTreeNodeDto() {
		fichePostesEnfant = new ArrayList<FichePosteTreeNodeDto>();
	}
	
	public Integer getIdFichePoste() {
		return idFichePoste;
	}
	public void setIdFichePoste(Integer idFichePoste) {
		this.idFichePoste = idFichePoste;
	}
	public Integer getIdFichePosteParent() {
		return idFichePosteParent;
	}
	public void setIdFichePosteParent(Integer idFichePosteParent) {
		this.idFichePosteParent = idFichePosteParent;
	}
	public Integer getIdAgent() {
		return idAgent;
	}
	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}
	public List<FichePosteTreeNodeDto> getFichePostesEnfant() {
		return fichePostesEnfant;
	}
	public void setFichePostesEnfant(List<FichePosteTreeNodeDto> fichePostesEnfant) {
		this.fichePostesEnfant = fichePostesEnfant;
	}
	public FichePosteTreeNodeDto getFichePosteParent() {
		return fichePosteParent;
	}
	public void setFichePosteParent(FichePosteTreeNodeDto fichePosteParent) {
		this.fichePosteParent = fichePosteParent;
	}
}
