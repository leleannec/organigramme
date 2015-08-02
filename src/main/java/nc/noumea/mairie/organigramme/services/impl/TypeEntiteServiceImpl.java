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

import nc.noumea.mairie.organigramme.core.services.impl.GenericServiceImpl;
import nc.noumea.mairie.organigramme.core.ws.AdsWSConsumer;
import nc.noumea.mairie.organigramme.dto.TypeEntiteDto;
import nc.noumea.mairie.organigramme.services.TypeEntiteService;
import nc.noumea.mairie.organigramme.utils.ComparatorUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service("typeEntiteService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TypeEntiteServiceImpl extends GenericServiceImpl<TypeEntiteDto> implements TypeEntiteService {

	@Autowired
	AdsWSConsumer adsWSConsumer;

	@Override
	public List<TypeEntiteDto> getListeTypeEntiteActif() {
		List<TypeEntiteDto> listeTypeEntiteDto = adsWSConsumer.getListeTypeEntite();
		List<TypeEntiteDto> result = new ArrayList<TypeEntiteDto>();

		for (TypeEntiteDto typeEntiteDto : listeTypeEntiteDto) {
			if (typeEntiteDto.isActif()) {
				result.add(typeEntiteDto);
			}
		}

		Collections.sort(result, new ComparatorUtil.TypeEntiteComparator());

		return result;
	}

	@Override
	public List<TypeEntiteDto> getListeTypeEntiteActifInactif() {
		List<TypeEntiteDto> listeTypeEntiteDto = adsWSConsumer.getListeTypeEntite();
		List<TypeEntiteDto> listeTypeEntiteDtoActif = new ArrayList<TypeEntiteDto>();
		List<TypeEntiteDto> listeTypeEntiteDtoInactif = new ArrayList<TypeEntiteDto>();
		List<TypeEntiteDto> result = new ArrayList<TypeEntiteDto>();

		for (TypeEntiteDto typeEntiteDto : listeTypeEntiteDto) {
			if (typeEntiteDto.isActif()) {
				listeTypeEntiteDtoActif.add(typeEntiteDto);
			} else {
				listeTypeEntiteDtoInactif.add(typeEntiteDto);
			}
		}

		Collections.sort(listeTypeEntiteDtoActif, new ComparatorUtil.TypeEntiteComparator());
		Collections.sort(listeTypeEntiteDtoInactif, new ComparatorUtil.TypeEntiteComparator());
		result.addAll(listeTypeEntiteDtoActif);
		result.addAll(listeTypeEntiteDtoInactif);

		return result;
	}
}
