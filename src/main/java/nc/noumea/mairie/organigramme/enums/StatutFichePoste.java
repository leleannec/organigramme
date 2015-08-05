package nc.noumea.mairie.organigramme.enums;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public enum StatutFichePoste {
	EN_CREATION("1", "En création"), VALIDEE("2", "Validée"), INACTIVE("4", "Inactive"), TRANSITOIRE("5", "Transitoire"), GELEE(
			"6", "Gelée");

	/** L'attribut qui contient le libelle long associe a l'enum */
	private final String libLong;
	private final String id;

	/** Le constructeur qui associe une valeur a l'enum */
	private StatutFichePoste(String id, String libLong) {
		this.libLong = libLong;
		this.id = id;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getLibLong() {
		return this.libLong;
	}

	/** La methode accesseur qui renvoit la valeur de l'enum */
	public String getId() {
		return this.id;
	}

	/**
	 * Renvoi la liste des ids de statut actif séparé par des virgules (utile
	 * pour le WS SIRH)
	 * 
	 * @return : la liste des ids de statut actif séparé par des virgules
	 */
	public static String getListIdStatutActif() {
		List<String> listIdStatutActive = new ArrayList<String>();
		for (StatutFichePoste statutFichePoste : StatutFichePoste.values()) {
			if (!statutFichePoste.equals(StatutFichePoste.INACTIVE)) {
				listIdStatutActive.add(statutFichePoste.getId());
			}
		}

		return StringUtils.join(listIdStatutActive, ",");
	}
}
