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

import java.util.List;

import nc.noumea.mairie.organigramme.dto.AccessRightOrganigrammeDto;
import nc.noumea.mairie.organigramme.dto.FichePosteDto;
import nc.noumea.mairie.organigramme.dto.FichePosteTreeNodeDto;
import nc.noumea.mairie.organigramme.dto.InfoEntiteDto;
import nc.noumea.mairie.organigramme.dto.ProfilAgentDto;

public interface ISirhWSConsumer {

	ProfilAgentDto getAgent(Integer idAgent);

	AccessRightOrganigrammeDto getAutorisationOrganigramme(Integer idAgent);

	List<FichePosteDto> getFichePosteByIdEntite(Integer idEntite, String listIdStatutFDP, boolean withEntiteChildren);

	InfoEntiteDto getInfoFDPByEntite(Integer idEntite, boolean withEntiteChildren);

	List<FichePosteTreeNodeDto> getTreeFichesPosteByEntite(Integer idEntite);
}
