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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nc.noumea.mairie.organigramme.core.transformer.MSDateTransformer;
import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;
import nc.noumea.mairie.organigramme.dto.ChangeStatutDto;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.dto.EntiteHistoDto;
import nc.noumea.mairie.organigramme.dto.ReturnMessageDto;
import nc.noumea.mairie.organigramme.dto.TypeEntiteDto;
import nc.noumea.mairie.organigramme.enums.Statut;
import nc.noumea.mairie.organigramme.utils.ComparatorUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

import flexjson.JSONSerializer;

@Service("adsWSConsumer")
public class AdsWSConsumer extends BaseWsConsumer implements IAdsWSConsumer {

	private static Logger	log								= LoggerFactory.getLogger(AdsWSConsumer.class);
	private final String	URL_TREE						= "api/arbre";
	private final String	URL_LISTE_ENTITE				= "api/typeEntite";
	private final String	URL_GET_ENTITE					= "api/entite";
	private final String	URL_SAVE_OR_UPDATE_ENTITE		= "api/entite/save";
	private final String	URL_SAVE_OR_UPDATE_TYPE_ENTITE	= "api/typeEntite/save";
	private final String	URL_DELETE_ENTITE				= "api/entite/delete";
	private final String	URL_DELETE_TYPE_ENTITE			= "api/typeEntite/deleteOrDisable";
	private final String	URL_CHANGE_STATUT				= "api/statut/change";

	@Autowired(required = true)
	String					adsWsBaseUrl;

	private boolean valideAdsWsBaseUrl() {
		if (StringUtils.isBlank(adsWsBaseUrl)) {
			log.error("L'URL de récupération des révisions ADS n'est pas définie");
			return false;
		}

		return true;
	}

	@Override
	public EntiteDto getCurrentTreeWithVDNRoot() {
		if (!valideAdsWsBaseUrl()) {
			return null;
		}

		String url = adsWsBaseUrl + URL_TREE;

		ClientResponse res = createAndFireGetRequest(new HashMap<String, String>(), url);

		if (res.getStatus() != HttpStatus.OK.value()) {
			log.error("Une erreur est survenue dans la récupération de l'arbre ADS");
			return null;
		}

		EntiteDto entiteDtoRoot = readResponse(EntiteDto.class, res, url);
		for (EntiteDto entiteDto : entiteDtoRoot.getEnfants()) {
			if (entiteDto.getSigle().equals("VDN")) {
				return entiteDto;
			}
		}

		return null;
	}

	@Override
	public EntiteDto getEntite(Integer idEntite) {
		String url = adsWsBaseUrl + URL_GET_ENTITE + "/" + idEntite.toString();
		ClientResponse res = createAndFireGetRequest(new HashMap<String, String>(), url);
		EntiteDto entiteDto = readResponse(EntiteDto.class, res, url);

		// On se le statut de l'entité
		entiteDto.setStatut(Statut.getStatutById(entiteDto.getIdStatut()));

		return entiteDto;
	}

	@Override
	public EntiteDto getEntiteWithChildren(Integer idEntite) {
		String url = adsWsBaseUrl + URL_GET_ENTITE + "/" + idEntite.toString() + "/withChildren";
		ClientResponse res = createAndFireGetRequest(new HashMap<String, String>(), url);
		return readResponse(EntiteDto.class, res, url);
	}

	@Override
	public ReturnMessageDto saveOrUpdateEntite(EntiteDto entiteDto) {
		if (!valideAdsWsBaseUrl()) {
			return null;
		}

		entiteDto.setSigle(OrganigrammeUtil.majusculeSansAccentTrim(entiteDto.getSigle()));

		String url = adsWsBaseUrl + URL_SAVE_OR_UPDATE_ENTITE;
		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(entiteDto);

		ClientResponse res = createAndFirePostRequest(new HashMap<String, String>(), url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<TypeEntiteDto> getListeTypeEntite() {
		if (!valideAdsWsBaseUrl()) {
			return null;
		}

		String url = adsWsBaseUrl + URL_LISTE_ENTITE;

		ClientResponse res = createAndFireGetRequest(new HashMap<String, String>(), url);

		if (res.getStatus() != HttpStatus.OK.value()) {
			log.error("Une erreur est survenue dans la récupération de la liste des types d'entité ADS");
			return null;
		}

		List<TypeEntiteDto> result = readResponseAsList(TypeEntiteDto.class, res, url);

		Collections.sort(result, new ComparatorUtil.TypeEntiteComparator());

		return result;
	}

	@Override
	public ReturnMessageDto saveOrUpdateTypeEntite(TypeEntiteDto typeEntiteDto) {

		String url = adsWsBaseUrl + URL_SAVE_OR_UPDATE_TYPE_ENTITE;
		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(typeEntiteDto);

		ClientResponse res = createAndFirePostRequest(new HashMap<String, String>(), url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto deleteTypeEntite(TypeEntiteDto typeEntiteDto) {
		String url = adsWsBaseUrl + URL_DELETE_TYPE_ENTITE + "/" + typeEntiteDto.getId().toString();
		ClientResponse res = createAndFireGetRequest(new HashMap<String, String>(), url);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto deleteEntite(EntiteDto entiteDto) {
		String url = adsWsBaseUrl + URL_DELETE_ENTITE + "/" + entiteDto.getId();

		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", entiteDto.getIdAgentSuppression().toString());

		ClientResponse res = createAndFireGetRequest(params, url);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto changeStatut(ChangeStatutDto changeStatutDto) {
		String url = adsWsBaseUrl + URL_CHANGE_STATUT;
		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(changeStatutDto);

		ClientResponse res = createAndFirePostRequest(new HashMap<String, String>(), url, json);
		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<EntiteHistoDto> getEntityHisto(Integer idEntite) {
		String url = adsWsBaseUrl + URL_GET_ENTITE + "/" + idEntite.toString() + "/histo";
		ClientResponse res = createAndFireGetRequest(new HashMap<String, String>(), url);
		List<EntiteHistoDto> result = readResponseAsList(EntiteHistoDto.class, res, url);

		Collections.sort(result, new ComparatorUtil.EntiteHistoComparator());

		return result;
	}
}
