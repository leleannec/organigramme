package nc.noumea.mairie.organigramme.core.event;

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

import nc.noumea.mairie.organigramme.core.entity.AbstractEntity;

import org.zkoss.zk.ui.event.Event;

/**
 * Evènement pour demander la fermeture d'un onglet concernant une entité particulière.
 * 
 * @author AgileSoft.NC
 */
public class FermeOngletAbstractEntityEvent extends Event {

	private static final long		serialVersionUID	= 1L;

	private final AbstractEntity	abstractEntity;

	public FermeOngletAbstractEntityEvent(AbstractEntity abstractEntity) {
		super("fermeOngletAbstractEntity", null, abstractEntity);
		this.abstractEntity = abstractEntity;
	}

	public AbstractEntity getAbstractEntity() {
		return abstractEntity;
	}
}
