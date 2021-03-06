package nc.noumea.mairie.organigramme.utils;

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
import java.util.Comparator;

import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.dto.EntiteHistoDto;
import nc.noumea.mairie.organigramme.dto.FichePosteDto;
import nc.noumea.mairie.organigramme.dto.TypeEntiteDto;

import org.zkoss.zul.GroupComparator;

public class ComparatorUtil {

	public static class TypeEntiteComparator implements Comparator<TypeEntiteDto>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public int compare(TypeEntiteDto o1, TypeEntiteDto o2) {
			return o1.getLabel().compareTo(o2.getLabel());
		}
	}

	public static class EntiteComparator implements Comparator<EntiteDto>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public int compare(EntiteDto o1, EntiteDto o2) {
			return o1.getSigle().compareTo(o2.getSigle());
		}
	}

	public static class FichePosteComparator implements Comparator<FichePosteDto>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public int compare(FichePosteDto o1, FichePosteDto o2) {
			return o1.getSigle().compareTo(o2.getSigle());
		}
	}

	public static class FichePosteComparatorAvecSigleEnTete implements Comparator<FichePosteDto>,
			GroupComparator<FichePosteDto>, Serializable {

		private static final long serialVersionUID = 1L;

		String sigle;

		public FichePosteComparatorAvecSigleEnTete(String sigle) {
			this.sigle = sigle;
		}

		@Override
		public int compare(FichePosteDto o1, FichePosteDto o2) {
			return o1.getSigle().compareTo(o2.getSigle());
		}

		@Override
		public int compareGroup(FichePosteDto o1, FichePosteDto o2) {
			if (o1.getSigle().equals(o2.getSigle())) {
				return 0;
			}
			if (o1.getSigle().equals(sigle)) {
				return -1;
			}
			if (o2.getSigle().equals(sigle)) {
				return 1;
			}

			return o1.getSigle().compareTo(o2.getSigle());
		}
	}

	public static class EntiteHistoComparator implements Comparator<EntiteHistoDto>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public int compare(EntiteHistoDto o1, EntiteHistoDto o2) {
			return o2.getDateHisto().compareTo(o1.getDateHisto());
		}
	}
}
