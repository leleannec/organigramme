package nc.noumea.mairie.organigramme.core.entity;

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


import java.util.List;

import nc.noumea.mairie.organigramme.core.utility.MessageErreur;
import nc.noumea.mairie.organigramme.core.utility.MessageErreurUtil;
import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;

/**
 * Entité abstraite parente des entités de l'application.
 * 
 * @author AgileSoft.NC
 */
public abstract class AbstractEntity {

	public abstract String getLibelleCourt();

	public abstract Long getId();

	public abstract Integer getVersion();

	public int getMaxLength(String property) throws Exception {
		return OrganigrammeUtil.getMaxLength(this, property);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? super.hashCode() : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractEntity other = (AbstractEntity) obj;

		if (getId() == null && other.getId() == null) {
			return super.equals(obj);
		}

		// cas où les 2 possèdent un id : ils sont considérés égaux si c'est le même id
		if (getId() != null && other.getId() != null) {
			return getId().equals(other.getId());
		}

		// un id est null, l'autre non
		return false;
	}

	/**
	 * Implémentation par défaut, peut-être redéfinie par les classes filles
	 */
	@Override
	public String toString() {
		return this.getLibelleCourt();
	}

	/**
	 * @return une liste de message d'erreur concernant l'entité (qui empêche typiquement sa sauvegarde). Ces erreurs peuvent être des violations de contraintes
	 *         déclarées ou des erreurs spécifiques métier. Ne doit pas retourner null (mais une liste vide dans le cas où il n'y a pas d'erreur). La liste
	 *         retournée doit être mutable pour permettre aux classes filles d'ajouter d'autres erreurs.
	 */
	public List<MessageErreur> construitListeMessageErreur() {
		return MessageErreurUtil.construitListeMessageErreurViolationContrainte(this);
	}
}