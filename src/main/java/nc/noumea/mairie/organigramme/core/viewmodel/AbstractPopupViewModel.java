package nc.noumea.mairie.organigramme.core.viewmodel;

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

import nc.noumea.mairie.organigramme.core.dto.AbstractEntityDto;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;

/**
 * ViewModel abstrait parent des ViewModel de popup 
 * 
 * @author AgileSoft.NC
 * @param <T> Type paramétré (représente une classe d'entité en pratique)
 */
@Init(superclass = true)
public abstract class AbstractPopupViewModel<T extends AbstractEntityDto> extends AbstractViewModel<T> {

	Window 									popup;
	
	public Window getPopup() {
		return popup;
	}

	public void setPopup(Window popup) {
		this.popup = popup;
	}

	@Command
	public void cancel(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
		event.stopPropagation();
		if(popup != null) {
			popup.detach();
		}
	}
}
