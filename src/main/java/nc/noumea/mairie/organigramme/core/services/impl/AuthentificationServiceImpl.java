package nc.noumea.mairie.organigramme.core.services.impl;

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

import nc.noumea.mairie.organigramme.core.services.AuthentificationService;
import nc.noumea.mairie.organigramme.dto.ProfilAgentDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

@Service("authentificationService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthentificationServiceImpl implements AuthentificationService {

	private static Logger	log	= LoggerFactory.getLogger(AuthentificationServiceImpl.class);

	@Override
	public ProfilAgentDto getCurrentUser() {
		return (ProfilAgentDto) Sessions.getCurrent().getAttribute("currentUser");
	}

	@Override
	public void logout() {
		ProfilAgentDto currentUser = getCurrentUser();
		log.debug("Disconnect User : " + currentUser.getIdAgent());
		Executions.getCurrent().getSession().setAttribute("logout", "logout");
		Executions.getCurrent().getSession().setAttribute("currentUser", null);
		Executions.sendRedirect("/");
	}
}
