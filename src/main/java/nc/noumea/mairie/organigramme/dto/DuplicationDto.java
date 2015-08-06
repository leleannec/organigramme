package nc.noumea.mairie.organigramme.dto;

import nc.noumea.mairie.organigramme.core.dto.AbstractEntityDto;

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

public class DuplicationDto extends AbstractEntityDto {

	Integer idAgent;
	EntiteDto entiteDto;
	EntiteDto entiteDtoCible;
	boolean withChildren = false;

	public EntiteDto getEntiteDto() {
		return entiteDto;
	}

	public void setEntiteDto(EntiteDto entiteDto) {
		this.entiteDto = entiteDto;
	}

	public EntiteDto getEntiteDtoCible() {
		return entiteDtoCible;
	}

	public void setEntiteDtoCible(EntiteDto entiteDtoCible) {
		this.entiteDtoCible = entiteDtoCible;
	}

	public boolean isWithChildren() {
		return withChildren;
	}

	public void setWithChildren(boolean withChildren) {
		this.withChildren = withChildren;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	@Override
	public String getLibelleCourt() {
		return null;
	}

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public Integer getVersion() {
		return null;
	}

}
