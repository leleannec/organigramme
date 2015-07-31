package nc.noumea.mairie.organigramme.core.ws;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import nc.noumea.mairie.organigramme.dto.AccessRightOrganigrammeDto;
import nc.noumea.mairie.organigramme.dto.FichePosteDto;
import nc.noumea.mairie.organigramme.dto.InfoEntiteDto;
import nc.noumea.mairie.organigramme.dto.ProfilAgentDto;
import nc.noumea.mairie.organigramme.utils.ComparatorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service("sirhWSConsumer")
public class SirhWSConsumer extends BaseWsConsumer implements ISirhWSConsumer {

	private Logger logger = LoggerFactory.getLogger(SirhWSConsumer.class);

	@Autowired
	private String sirhWsBaseUrl;

	private static final String URL_AGENT = "agents/getAgent";
	private static final String URL_AUTORISATION_ORGANIGRAMME = "utilisateur/getAutorisationOrganigramme";
	private static final String URL_FICHE_POSTE_PAR_ENTITE = "fichePostes/listFichePosteByIdEntite";
	private static final String URL_FICHE_POSTE_INFO_PAR_ENTITE = "fichePostes/getInfoFDPByEntite";

	public ProfilAgentDto getAgent(Integer idAgent) {
		String url = String.format(sirhWsBaseUrl + URL_AGENT);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		logger.debug("getAgent with url " + url);

		ClientResponse res = createAndFireGetRequest(params, url);
		return readResponse(ProfilAgentDto.class, res, url);
	}

	public AccessRightOrganigrammeDto getAutorisationOrganigramme(Integer idAgent) {
		String url = String.format(sirhWsBaseUrl + URL_AUTORISATION_ORGANIGRAMME);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());
		logger.debug("getAutorisationOrganigramme with url " + url);
		ClientResponse res = createAndFireGetRequest(params, url);
		return readResponse(AccessRightOrganigrammeDto.class, res, url);
	}

	public List<FichePosteDto> getFichePosteByIdEntite(Integer idEntite, boolean withEntiteChildren) {
		String url = String.format(sirhWsBaseUrl + URL_FICHE_POSTE_PAR_ENTITE);
		HashMap<String, String> params = new HashMap<>();
		params.put("idEntite", idEntite.toString());
		params.put("withEntiteChildren", String.valueOf(withEntiteChildren));
		logger.debug("getFichePosteByIdEntite with url " + url);
		ClientResponse res = createAndFireGetRequest(params, url);
		List<FichePosteDto> result = readResponseAsList(FichePosteDto.class, res, url);

		Collections.sort(result, new ComparatorUtil.FichePosteComparator());

		return result;
	}

	public InfoEntiteDto getInfoFDPByEntite(Integer idEntite, boolean withEntiteChildren) {
		String url = String.format(sirhWsBaseUrl + URL_FICHE_POSTE_INFO_PAR_ENTITE);
		HashMap<String, String> params = new HashMap<>();
		params.put("idEntite", idEntite.toString());
		params.put("withEntiteChildren", String.valueOf(withEntiteChildren));
		logger.debug("getInfoFDPByEntite with url " + url);
		ClientResponse res = createAndFireGetRequest(params, url);
		return readResponse(InfoEntiteDto.class, res, url);
	}
}
