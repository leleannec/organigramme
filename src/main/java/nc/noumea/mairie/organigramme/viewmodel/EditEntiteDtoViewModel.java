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
import java.util.HashMap;
import java.util.List;

import nc.noumea.mairie.organigramme.core.viewmodel.AbstractEditViewModel;
import nc.noumea.mairie.organigramme.core.ws.IAdsWSConsumer;
import nc.noumea.mairie.organigramme.core.ws.ISirhWSConsumer;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.dto.EntiteHistoDto;
import nc.noumea.mairie.organigramme.dto.FichePosteDto;
import nc.noumea.mairie.organigramme.dto.TypeEntiteDto;
import nc.noumea.mairie.organigramme.services.OrganigrammeService;
import nc.noumea.mairie.organigramme.services.TypeEntiteService;
import nc.noumea.mairie.organigramme.utils.ComparatorUtil;

import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

@Init(superclass = true)
@VariableResolver(DelegatingVariableResolver.class)
public class EditEntiteDtoViewModel extends AbstractEditViewModel<EntiteDto> implements Serializable {

	private static final long	serialVersionUID	= 1L;

	//@formatter:off
	@WireVariable IAdsWSConsumer			adsWSConsumer;
	@WireVariable ISirhWSConsumer			sirhWSConsumer;
	@WireVariable TypeEntiteService			typeEntiteService; 
	@WireVariable OrganigrammeService		organigrammeService;
	//@formatter:on

	@Override
	public void initSetup(@ExecutionArgParam("entity") EntiteDto entiteDto) {
		this.entity = adsWSConsumer.getEntiteWithChildren(entiteDto.getIdEntite());
	}

	public boolean isEditable() {
		return false;
	}

	/**
	 * Classe recopiée depuis {@link OrganigrammeViewModel}
	 * @return la liste des types d'entités actifs et inactifs triés par nom
	 */
	public List<TypeEntiteDto> getListeTypeEntiteActifInactif() {
		return typeEntiteService.getListeTypeEntiteActifInactif();
	}

	/**
	 * Classe recopiée depuis {@link OrganigrammeViewModel}
	 * @return la liste des entités remplaçables
	 */
	public List<EntiteDto> getListeEntiteRemplace() {
		return organigrammeService.findAllNotPrevision();
	}

	/**
	 * Classe recopiée depuis {@link OrganigrammeViewModel}
	 * @return la liste des fiches de postes groupées par Sigle
	 */
	public FichePosteGroupingModel getFichePosteGroupingModel() {
		if (this.entity == null) {
			return null;
		}

		List<FichePosteDto> listeFichePosteDto = sirhWSConsumer.getFichePosteByIdEntite(this.entity.getIdEntite(), true);

		return new FichePosteGroupingModel(listeFichePosteDto, new ComparatorUtil.FichePosteComparator());
	}

	/**
	 * Classe recopiée depuis {@link OrganigrammeViewModel}
	 * @return la liste de l'historique de l'entité
	 */
	public List<EntiteHistoDto> getListeHistorique() {
		if (this.entity == null) {
			return null;
		}

		return adsWSConsumer.getListeEntiteHisto(this.entity.getIdEntite(), new HashMap<Integer, String>());
	}
}
