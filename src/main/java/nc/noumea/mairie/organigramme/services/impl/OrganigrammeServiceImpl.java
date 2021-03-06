package nc.noumea.mairie.organigramme.services.impl;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nc.noumea.mairie.organigramme.core.services.AuthentificationService;
import nc.noumea.mairie.organigramme.core.services.impl.GenericServiceImpl;
import nc.noumea.mairie.organigramme.core.ws.AdsWSConsumer;
import nc.noumea.mairie.organigramme.dto.ChangeStatutDto;
import nc.noumea.mairie.organigramme.dto.DuplicationDto;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.dto.ProfilAgentDto;
import nc.noumea.mairie.organigramme.dto.ReturnMessageDto;
import nc.noumea.mairie.organigramme.enums.Statut;
import nc.noumea.mairie.organigramme.services.OrganigrammeService;
import nc.noumea.mairie.organigramme.services.ReturnMessageService;
import nc.noumea.mairie.organigramme.utils.ComparatorUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service("organigrammeService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OrganigrammeServiceImpl extends GenericServiceImpl<EntiteDto> implements OrganigrammeService {

	@Autowired
	ReturnMessageService returnMessageService;

	@Autowired
	AdsWSConsumer adsWSConsumer;

	@Autowired
	AuthentificationService authentificationService;

	@Override
	public boolean updateStatut(EntiteDto entiteDto, Statut statutCible) {
		if (entiteDto == null || statutCible == null) {
			return false;
		}

		ChangeStatutDto changeStatutDto = initChangeStatutDto(entiteDto, statutCible);

		ReturnMessageDto returnMessageDto = adsWSConsumer.changeStatut(changeStatutDto);
		if (!returnMessageService.gererReturnMessage(returnMessageDto)) {
			return false;
		}

		return entiteDto.updateStatut(statutCible);
	}

	/**
	 * Initialise le dto de changement de statut avec les bonnes valeurs
	 * 
	 * @param entiteDto
	 *            : l'{@link EntiteDto} sur laquelle on souhaite changer le
	 *            statut
	 * @param statut
	 *            : la statut cible
	 * @return le {@link ChangeStatutDto} contenant toutes les informations à
	 *         envoyer au WS ADS
	 */
	private ChangeStatutDto initChangeStatutDto(EntiteDto entiteDto, Statut statut) {
		ChangeStatutDto changeStatutDto = new ChangeStatutDto();
		changeStatutDto.setIdEntite(entiteDto.getIdEntite());
		changeStatutDto.setIdStatut(statut.getIdStatut());
		changeStatutDto.setMajEntitesEnfant(false);
		changeStatutDto.setDateDeliberation(entiteDto.getDateDeliberationActif());
		changeStatutDto.setRefDeliberation(entiteDto.getRefDeliberationActif());

		ProfilAgentDto profilAgentDto = authentificationService.getCurrentUser();

		changeStatutDto.setIdAgent(profilAgentDto.getIdAgent());

		if (statut == Statut.INACTIF) {
			changeStatutDto.setDateDeliberation(entiteDto.getDateDeliberationInactif());
			changeStatutDto.setRefDeliberation(entiteDto.getRefDeliberationInactif());
		}

		return changeStatutDto;
	}

	public boolean deleteEntite(EntiteDto entiteDto) {

		ReturnMessageDto returnMessageDto = adsWSConsumer.deleteEntite(entiteDto);
		if (!returnMessageService.gererReturnMessage(returnMessageDto)) {
			return false;
		}

		return true;
	}

	@Override
	public List<EntiteDto> findAllNotPrevision() {
		EntiteDto entiteDtoRoot = adsWSConsumer.getCurrentTreeWithVDNRoot();
		List<EntiteDto> result = getAllEntiteNotPrevision(entiteDtoRoot.getEnfants(), new ArrayList<EntiteDto>());

		Collections.sort(result, new ComparatorUtil.EntiteComparator());

		return result;
	}

	@Override
	public List<EntiteDto> findAllActifOuPrevision() {
		EntiteDto entiteDtoRoot = adsWSConsumer.getCurrentTreeWithVDNRoot();
		List<EntiteDto> result = getAllEntiteActifOuPrevision(entiteDtoRoot.getEnfants(), new ArrayList<EntiteDto>());

		Collections.sort(result, new ComparatorUtil.EntiteComparator());

		return result;
	}

	private List<EntiteDto> getAllEntiteActifOuPrevision(List<EntiteDto> listeEnfant, List<EntiteDto> result) {
		for (EntiteDto entiteDto : listeEnfant) {

			if (entiteDto.getStatut().equals(Statut.ACTIF) || entiteDto.getStatut().equals(Statut.PREVISION)) {
				result.add(entiteDto);
			}

			getAllEntiteActifOuPrevision(entiteDto.getEnfants(), result);
		}

		return result;
	}

	private List<EntiteDto> getAllEntiteNotPrevision(List<EntiteDto> listeEnfant, List<EntiteDto> result) {
		for (EntiteDto entiteDto : listeEnfant) {

			if (!entiteDto.getStatut().equals(Statut.PREVISION)) {
				result.add(entiteDto);
			}

			getAllEntiteNotPrevision(entiteDto.getEnfants(), result);
		}

		return result;
	}

	@Override
	public ReturnMessageDto dupliqueEntite(DuplicationDto duplicationDto) {

		ProfilAgentDto profilAgentDto = authentificationService.getCurrentUser();
		duplicationDto.setIdAgent(profilAgentDto.getIdAgent());

		return adsWSConsumer.dupliqueEntite(duplicationDto);
	}
}
