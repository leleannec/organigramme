package nc.noumea.mairie.organigramme.controller;

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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LogoutController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Label labelDeconnexion;
	@Wire
	Label labelPrenomNomUsageEtRole;

	@WireVariable
	AuthentificationService authentificationService;

	/**
	 * Passe l'attribut currentUser stocké en session à null et redirige vers la
	 * fenêtre de login
	 */
	@Listen("onClick=#labelDeconnexion")
	public void doLogout() {
		authentificationService.logout();
	}

	/**
	 * Récupére le currentUser en session et, s'il existe, affiche le bouton de
	 * déconnexion et le nom de la personne connéctée
	 */
	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		ProfilAgentDto currentUser = (ProfilAgentDto) Sessions.getCurrent().getAttribute("currentUser");
		if (currentUser != null) {
			labelDeconnexion.setVisible(true);
			String role = "";
			if (currentUser.isVisualisation()) {
				role = "Visualisation";
			}
			if (currentUser.isEdition()) {
				role = "Edition";
			}
			if (currentUser.isAdministrateur()) {
				role = "Administrateur";
			}
			labelPrenomNomUsageEtRole.setValue(currentUser.getPrenomNomUsage() + " (" + role + ")");
		}
	}
}
