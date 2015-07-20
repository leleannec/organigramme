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

import nc.noumea.mairie.organigramme.core.services.impl.GenericServiceImpl;
import nc.noumea.mairie.organigramme.dao.CouleurTypeEntiteDao;
import nc.noumea.mairie.organigramme.entity.CouleurTypeEntite;
import nc.noumea.mairie.organigramme.services.CouleurTypeEntiteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service("couleurTypeEntiteService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CouleurTypeEntiteServiceImpl extends GenericServiceImpl<CouleurTypeEntite> implements CouleurTypeEntiteService {

	@Autowired
	CouleurTypeEntiteDao	couleurTypeEntiteDao;

	public CouleurTypeEntite findByIdTypeEntite(Long idTypeEntite) {
		return couleurTypeEntiteDao.findByIdTypeEntite(idTypeEntite);
	}
}