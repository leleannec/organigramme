package nc.noumea.mairie.organigramme.viewmodel;

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

import java.io.Serializable;

import javax.servlet.ServletException;

import nc.noumea.mairie.organigramme.core.authentification.LDAP;
import nc.noumea.mairie.organigramme.core.viewmodel.UserForm;
import nc.noumea.mairie.organigramme.core.ws.IRadiWSConsumer;
import nc.noumea.mairie.organigramme.core.ws.ISirhWSConsumer;
import nc.noumea.mairie.organigramme.dto.AccessRightOrganigrammeDto;
import nc.noumea.mairie.organigramme.dto.LightUserDto;
import nc.noumea.mairie.organigramme.dto.ProfilAgentDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ConnexionViewModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(ConnexionViewModel.class);

	// @formatter:off
	@WireVariable
	private IRadiWSConsumer radiWSConsumer;
	@WireVariable
	private ISirhWSConsumer sirhWSConsumer;
	// @formatter:on

	private ProfilAgentDto currentUser;

	private UserForm userForm;

	private String errorMessage;

	public ProfilAgentDto getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(ProfilAgentDto currentUser) {
		this.currentUser = currentUser;
	}

	public UserForm getUserForm() {
		return userForm;
	}

	public void setUserForm(UserForm userForm) {
		this.userForm = userForm;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Init
	public void initMenu() {
		currentUser = (ProfilAgentDto) Sessions.getCurrent().getAttribute("currentUser");
		userForm = new UserForm();
	}

	@Command
	@NotifyChange({ "userForm", "errorMessage" })
	public void connexion() throws ServletException {
		logger.debug("connexion User : " + userForm.getUser());

		if (LDAP.controlerHabilitation(userForm.getUser(), userForm.getPassword())) {

			String remoteUser = userForm.getUser();

			LightUserDto userDto = radiWSConsumer.getAgentCompteADByLogin(remoteUser);
			if (null == userDto) {
				logger.debug("User not exist in Radi WS with RemoteUser : " + remoteUser);
				returnError();
			}

			if (0 == userDto.getEmployeeNumber()) {
				logger.debug("User not exist in Radi WS with RemoteUser : " + remoteUser);
				returnError();
			}

			ProfilAgentDto profilAgent = recupereProfilAgent(userDto);

			AccessRightOrganigrammeDto accessRightOrganigrammeDto = recupereAccessRightOrganigramme(userDto
					.getEmployeeNumber());
			if (accessRightOrganigrammeDto == null || accessRightOrganigrammeDto.isAucunRole()) {
				return;
			}

			renseigneAccessRightOnUser(profilAgent, accessRightOrganigrammeDto);

			Sessions.getCurrent().setAttribute("currentUser", profilAgent);
			logger.debug("Authentification du user ok : " + remoteUser);
			Executions.getCurrent().getSession().setAttribute("logout", null);
			Executions.sendRedirect("/index.zul");
		}

		returnError();
	}

	private AccessRightOrganigrammeDto recupereAccessRightOrganigramme(Integer employeeNumber) throws ServletException {
		AccessRightOrganigrammeDto accessRightOrganigrammeDto = null;
		try {
			accessRightOrganigrammeDto = sirhWSConsumer.getAutorisationOrganigramme(employeeNumber);
		} catch (Exception e) {
			// le SIRH-WS ne semble pas repondre
			logger.debug("L'application SIRH-WS ne semble pas répondre.");
			return null;
		}

		if (null == accessRightOrganigrammeDto) {
			logger.debug("ProfilAgent not exist in SIRH WS with EmployeeNumber : " + employeeNumber);
			return null;
		}

		return accessRightOrganigrammeDto;
	}

	private void renseigneAccessRightOnUser(ProfilAgentDto profilAgent,
			AccessRightOrganigrammeDto accessRightOrganigrammeDto) {
		profilAgent.setAdministrateur(accessRightOrganigrammeDto.isAdministrateur());
		profilAgent.setEdition(accessRightOrganigrammeDto.isEdition());
		profilAgent.setVisualisation(accessRightOrganigrammeDto.isVisualisation());
	}

	private ProfilAgentDto recupereProfilAgent(LightUserDto userDto) {
		ProfilAgentDto profilAgent = null;
		try {
			profilAgent = sirhWSConsumer.getAgent(userDto.getEmployeeNumber());
		} catch (Exception e) {
			// le SIRH-WS ne semble pas repondre
			logger.debug("L'application SIRH-WS ne semble pas répondre.");
			returnError();
		}

		if (null == profilAgent) {
			logger.debug("ProfilAgent not exist in SIRH WS with EmployeeNumber : " + userDto.getEmployeeNumber());
			returnError();
		}
		return profilAgent;
	}

	private void returnError() {
		setErrorMessage("Le nom d'utilisateur ou le mot de passe est incorrect.");
		userForm.reset();
	}
}
