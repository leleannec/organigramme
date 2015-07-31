package nc.noumea.mairie.organigramme.core.dto;

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

import nc.noumea.mairie.organigramme.core.entity.AbstractEntity;
import nc.noumea.mairie.organigramme.core.utility.MessageErreur;
import nc.noumea.mairie.organigramme.core.utility.MessageErreurUtil;

/**
 * Entité DTO abstraite parente des DT0 de l'application.
 * 
 * @author AgileSoft.NC
 */
public abstract class AbstractEntityDto extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * @return une liste de message d'erreur concernant l'entité (qui empêche
	 *         typiquement sa sauvegarde). Ces erreurs peuvent être des
	 *         violations de contraintes déclarées ou des erreurs spécifiques
	 *         métier. Ne doit pas retourner null (mais une liste vide dans le
	 *         cas où il n'y a pas d'erreur). La liste retournée doit être
	 *         mutable pour permettre aux classes filles d'ajouter d'autres
	 *         erreurs.
	 */
	public List<MessageErreur> construitListeMessageErreur() {
		return MessageErreurUtil.construitListeMessageErreurViolationContrainte(this);
	}
}