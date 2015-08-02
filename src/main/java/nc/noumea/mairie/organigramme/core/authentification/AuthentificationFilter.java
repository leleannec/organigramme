package nc.noumea.mairie.organigramme.core.authentification;

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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nc.noumea.mairie.organigramme.core.ws.IRadiWSConsumer;
import nc.noumea.mairie.organigramme.core.ws.ISirhWSConsumer;
import nc.noumea.mairie.organigramme.dto.AccessRightOrganigrammeDto;
import nc.noumea.mairie.organigramme.dto.LightUserDto;
import nc.noumea.mairie.organigramme.dto.ProfilAgentDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.zk.ui.select.annotation.VariableResolver;

@Component
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class AuthentificationFilter implements Filter {

	public static final String ACCES_CONNEXION = "/connexion";
	public static final String ATT_SESSION_USER = "sessionUtilisateur";

	public static final List<String> PAGES_STATIQUES = Arrays.asList("/401.jsp", "/404.jsp", "/incident.jsp",
			"/maintenance.jsp", "/version.jsp");

	private Logger logger = LoggerFactory.getLogger(AuthentificationFilter.class);

	private IRadiWSConsumer radiWSConsumer;

	private ISirhWSConsumer sirhWSConsumer;

	public void init(FilterConfig config) throws ServletException {
		ServletContext servletContext = config.getServletContext();
		WebApplicationContext webApplicationContext = WebApplicationContextUtils
				.getWebApplicationContext(servletContext);

		AutowireCapableBeanFactory autowireCapableBeanFactory = webApplicationContext.getAutowireCapableBeanFactory();

		radiWSConsumer = (IRadiWSConsumer) autowireCapableBeanFactory.getBean("radiWSConsumer");
		sirhWSConsumer = (ISirhWSConsumer) autowireCapableBeanFactory.getBean("sirhWSConsumer");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {

		/* Cast des objets request et response */
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession hSess = ((HttpServletRequest) request).getSession();

		// Hack pour pouvoir récupérer les images sur la page de connexion
		if (request.getRequestURI().contains("imgs")) {
			chain.doFilter(request, response);
			return;
		}

		// on laisse passer pour le rproxy et ainsi permettre de deployer l
		// application sur le 2e noeud tomcat
		if (PAGES_STATIQUES.contains(request.getServletPath())) {
			chain.doFilter(request, response);
			return;
		}

		if (null != hSess.getAttribute("logout")) {
			if (!request.getRequestURI().contains("zkau") && !request.getRequestURI().contains("login.zul")
					&& !request.getRequestURI().contains("css")) {

				// dans le cas ou la personne a clique sur ce deconnecte et ne
				// ferme pas le navigateur
				logger.debug("User disconnect");
				hSess.setAttribute("logout", "logout");
				request.getRequestDispatcher("login.zul").forward(request, response);
				return;
			}
			chain.doFilter(request, response);
			return;
		}

		if (null != hSess.getAttribute("currentUser")) {
			chain.doFilter(request, response);
			return;
		}

		if ((null == request.getHeader("x-krb_remote_user") || "".equals(request.getHeader("x-krb_remote_user").trim()))
				&& !request.getHeader("host").contains("localhost")) {
			logger.debug("x-krb_remote_user is NULL");
			// hSess.invalidate();
			// request.logout();
			// response.sendError(HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED,
			// "You are logged out.");
			// #15803
			hSess.setAttribute("logout", "logout");
			request.getRequestDispatcher("login.zul").forward(request, response);
			return;
		}

		String remoteUser = request.getHeader("x-krb_remote_user");

		if (null == remoteUser && request.getHeader("host").contains("localhost")) {
			remoteUser = "chata73";
		}

		remoteUser = convertRemoteUser(remoteUser);

		LightUserDto userDto = radiWSConsumer.getAgentCompteADByLogin(remoteUser);
		if (null == userDto) {
			logger.debug("User not exist in Radi WS with RemoteUser : " + remoteUser);
			// request.logout();

			hSess.setAttribute("logout", "logout");
			request.getRequestDispatcher("login.zul").forward(request, response);
			return;
		}

		if (0 == userDto.getEmployeeNumber()) {
			logger.debug("User not exist in Radi WS with RemoteUser : " + remoteUser);

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					"Vous n'êtes pas un agent de la mairie, vous n'êtes pas autorisé à accéder à cette application.");
			return;
		}

		ProfilAgentDto profilAgent = recupereProfilAgent(request, userDto.getEmployeeNumber());
		if (profilAgent == null) {
			return;
		}

		AccessRightOrganigrammeDto accessRightOrganigrammeDto = recupereAccessRightOrganigramme(request,
				userDto.getEmployeeNumber());
		if (accessRightOrganigrammeDto == null || accessRightOrganigrammeDto.isAucunRole()) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					"Vous n'êtes pas autorisé à accéder à cette application.");
			return;
		}

		renseigneAccessRightOnUser(profilAgent, accessRightOrganigrammeDto);

		hSess.setAttribute("currentUser", profilAgent);
		logger.debug("Authentification du user ok : " + remoteUser);
		logger.debug("Droits du user ok : " + accessRightOrganigrammeDto);

		chain.doFilter(request, response);
	}

	private void renseigneAccessRightOnUser(ProfilAgentDto profilAgent,
			AccessRightOrganigrammeDto accessRightOrganigrammeDto) {
		profilAgent.setAdministrateur(accessRightOrganigrammeDto.isAdministrateur());
		profilAgent.setEdition(accessRightOrganigrammeDto.isEdition());
		profilAgent.setVisualisation(accessRightOrganigrammeDto.isVisualisation());
	}

	private AccessRightOrganigrammeDto recupereAccessRightOrganigramme(HttpServletRequest request,
			Integer employeeNumber) throws ServletException {
		AccessRightOrganigrammeDto accessRightOrganigrammeDto = null;
		try {
			accessRightOrganigrammeDto = sirhWSConsumer.getAutorisationOrganigramme(employeeNumber);
		} catch (Exception e) {
			// le SIRH-WS ne semble pas repondre
			logger.debug("L'application SIRH-WS ne semble pas répondre.");
			request.logout();
			return null;
		}

		if (null == accessRightOrganigrammeDto) {
			logger.debug("ProfilAgent not exist in SIRH WS with EmployeeNumber : " + employeeNumber);
			request.logout();
			return null;
		}

		return accessRightOrganigrammeDto;
	}

	private ProfilAgentDto recupereProfilAgent(HttpServletRequest request, Integer employeeNumber)
			throws ServletException {
		ProfilAgentDto profilAgent = null;
		try {
			profilAgent = sirhWSConsumer.getAgent(employeeNumber);
		} catch (Exception e) {
			// le SIRH-WS ne semble pas repondre
			logger.debug("L'application SIRH-WS ne semble pas répondre.");
			request.logout();
			return null;
		}

		if (null == profilAgent) {
			logger.debug("ProfilAgent not exist in SIRH WS with EmployeeNumber : " + employeeNumber);
			request.logout();
			return null;
		}

		return profilAgent;
	}

	public void destroy() {
	}

	public String convertRemoteUser(String remoteUser) {
		if (null != remoteUser && remoteUser.contains("@")) {
			remoteUser = remoteUser.substring(0, remoteUser.indexOf("@"));
		}

		return remoteUser;
	}
}
