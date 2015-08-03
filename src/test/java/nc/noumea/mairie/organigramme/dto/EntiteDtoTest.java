package nc.noumea.mairie.organigramme.dto;

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
import java.util.List;

import nc.noumea.mairie.organigramme.enums.Statut;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EntiteDtoTest {

	@Test
	public void tousEnfantEnStatut() {

		List<Statut> listeStatut = new ArrayList<Statut>();
		listeStatut.add(Statut.INACTIF);

		EntiteDto entiteDto = new EntiteDto();
		entiteDto.setIdStatut(1);

		List<EntiteDto> listeEnfantNiveau1 = new ArrayList<EntiteDto>();
		EntiteDto entiteDtoEnfant1 = new EntiteDto();
		entiteDtoEnfant1.setIdStatut(1);
		listeEnfantNiveau1.add(entiteDtoEnfant1);

		entiteDto.setEnfants(listeEnfantNiveau1);

		Assert.assertFalse(entiteDto.tousEnfantEnStatut(listeStatut, entiteDto.getEnfants()));

		entiteDtoEnfant1.setIdStatut(3);

		Assert.assertTrue(entiteDto.tousEnfantEnStatut(listeStatut, entiteDto.getEnfants()));

		EntiteDto entiteDtoEnfant2 = new EntiteDto();
		entiteDtoEnfant2.setIdStatut(3);

		List<EntiteDto> listeEnfantNiveau2 = new ArrayList<EntiteDto>();
		EntiteDto entiteDtoEnfant3 = new EntiteDto();
		entiteDtoEnfant3.setIdStatut(1);
		listeEnfantNiveau2.add(entiteDtoEnfant3);

		entiteDtoEnfant1.setEnfants(listeEnfantNiveau2);

		Assert.assertFalse(entiteDto.tousEnfantEnStatut(listeStatut, entiteDto.getEnfants()));

		entiteDtoEnfant3.setIdStatut(3);

		Assert.assertTrue(entiteDto.tousEnfantEnStatut(listeStatut, entiteDto.getEnfants()));
	}
}
