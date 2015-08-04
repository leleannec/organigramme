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

import java.util.Comparator;
import java.util.List;

import nc.noumea.mairie.organigramme.dto.FichePosteDto;

import org.zkoss.zul.GroupsModelArray;

public class FichePosteGroupingModel extends GroupsModelArray<FichePosteDto, String, String, Object> {
	private static final long serialVersionUID = 1L;

	public FichePosteGroupingModel(List<FichePosteDto> data, Comparator<FichePosteDto> cmpr, String sigleEntite) {
		super(data.toArray(new FichePosteDto[0]), cmpr);

		// On replie tous les groupes sauf celui de l'entité
		for (int i = 0; i < this.getGroupCount(); i++) {
			FichePosteDto fichePosteDto = this.getChild(i, 0);
			if (!fichePosteDto.getSigle().equals(sigleEntite)) {
				removeOpenGroup(i);
			}
		}
	}

	protected String createGroupHead(FichePosteDto[] groupdata, int index, int col) {
		if (groupdata.length > 0) {
			return groupdata[0].getSigle() + "(" + groupdata.length + ")";
		}

		return "";
	}
}
