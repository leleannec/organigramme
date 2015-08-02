package nc.noumea.mairie.organigramme.core.utility;

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

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;

public class ZkUtil {

	private static Logger log = LoggerFactory.getLogger(ZkUtil.class);

	public static void disableComponentAndChildren(Component component) {
		tryDisableComponent(component);
		for (Component child : component.getChildren()) {
			disableComponentAndChildren(child);
		}
	}

	private static void tryDisableComponent(Component component) {
		try {
			if (isComponentToBeDisabled(component)) {
				Method m = component.getClass().getMethod("setDisabled", Boolean.TYPE);
				m.invoke(component, true);
			}
		} catch (Exception e) {
			log.error("component = " + component, ", classe du component = " + component.getClass().getName(), e);
		}
	}

	private static boolean isComponentToBeDisabled(Component component) {
		String componentClassName = component.getClass().getName();
		return componentClassName.equals("org.zkoss.zul.Combobox") || //
				componentClassName.equals("org.zkoss.zul.Textbox") || //
				componentClassName.equals("org.zkoss.zul.Datebox") || //
				componentClassName.equals("org.zkoss.zul.Checkbox") || //
				componentClassName.equals("org.zkoss.zul.Intbox") || //
				componentClassName.equals("org.zkoss.zul.Doublebox");
	}

}
