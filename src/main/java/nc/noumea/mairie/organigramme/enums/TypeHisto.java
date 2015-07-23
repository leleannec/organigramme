package nc.noumea.mairie.organigramme.enums;

public enum TypeHisto {

	CREATION(0, "Création"),
	MODIFICATION(1, "Modification"),
	SUPPRESSION(2, "Suppression"),
	CHANGEMENT_STATUT(3, "Chgt. Statut");

	private Integer	idRefTypeHisto;
	private String	libelle;

	TypeHisto(Integer idRefTypeHisto, String libelle) {
		this.idRefTypeHisto = idRefTypeHisto;
		this.libelle = libelle;
	}

	public static TypeHisto getTypeHistoEnum(Integer idRefTypeHisto) {

		switch (idRefTypeHisto) {
			case 0:
				return CREATION;
			case 1:
				return MODIFICATION;
			case 2:
				return SUPPRESSION;
			case 3:
				return CHANGEMENT_STATUT;
			default:
				return null;
		}
	}

	public Integer getIdRefTypeHisto() {
		return idRefTypeHisto;
	}

	public String getLibelle() {
		return libelle;
	}
}
