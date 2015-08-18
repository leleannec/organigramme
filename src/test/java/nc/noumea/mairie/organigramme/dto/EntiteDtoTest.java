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
import nc.noumea.mairie.organigramme.enums.Transition;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EntiteDtoTest {

	@Test
	public void checkFeuille() {
		EntiteDto entiteDto = new EntiteDto();

		Assert.assertTrue(entiteDto.isFeuille());

		List<EntiteDto> listeEnfant = new ArrayList<EntiteDto>();
		entiteDto.setEnfants(listeEnfant);
		Assert.assertTrue(entiteDto.isFeuille());

		listeEnfant.add(new EntiteDto());
		Assert.assertFalse(entiteDto.isFeuille());
	}

	@Test
	public void checkStatut() {
		EntiteDto entiteDto = new EntiteDto();

		Assert.assertFalse(entiteDto.isActif());
		Assert.assertFalse(entiteDto.isInactif());
		Assert.assertFalse(entiteDto.isTransitoire());
		Assert.assertFalse(entiteDto.isPrevision());
		Assert.assertFalse(entiteDto.isTransitoireOuInactif());

		// Prévision
		entiteDto.setIdStatut(0);
		Assert.assertFalse(entiteDto.isActif());
		Assert.assertFalse(entiteDto.isInactif());
		Assert.assertFalse(entiteDto.isTransitoire());
		Assert.assertTrue(entiteDto.isPrevision());
		Assert.assertFalse(entiteDto.isTransitoireOuInactif());

		// Actif
		entiteDto.setIdStatut(1);
		Assert.assertTrue(entiteDto.isActif());
		Assert.assertFalse(entiteDto.isInactif());
		Assert.assertFalse(entiteDto.isTransitoire());
		Assert.assertFalse(entiteDto.isPrevision());
		Assert.assertFalse(entiteDto.isTransitoireOuInactif());

		// Transitoire
		entiteDto.setIdStatut(2);
		Assert.assertFalse(entiteDto.isActif());
		Assert.assertFalse(entiteDto.isInactif());
		Assert.assertTrue(entiteDto.isTransitoire());
		Assert.assertFalse(entiteDto.isPrevision());
		Assert.assertTrue(entiteDto.isTransitoireOuInactif());

		// Inactif
		entiteDto.setIdStatut(3);
		Assert.assertFalse(entiteDto.isActif());
		Assert.assertTrue(entiteDto.isInactif());
		Assert.assertFalse(entiteDto.isTransitoire());
		Assert.assertFalse(entiteDto.isPrevision());
		Assert.assertTrue(entiteDto.isTransitoireOuInactif());
	}

	@Test
	public void getListeTransitionAutorise() {
		EntiteDto entiteDto = new EntiteDto();

		Assert.assertEquals(entiteDto.getListeTransitionAutorise(), new ArrayList<Transition>());

		// Prévision sans parent
		entiteDto.setIdStatut(0);
		Assert.assertEquals(entiteDto.getListeTransitionAutorise(), new ArrayList<Transition>());

		EntiteDto entiteDtoParent = new EntiteDto();
		entiteDto.setEntiteParent(entiteDtoParent);

		// Parent en prévision
		entiteDtoParent.setIdStatut(0);
		Assert.assertEquals(entiteDto.getListeTransitionAutorise(), new ArrayList<Transition>());

		// Parent en inactif
		entiteDtoParent.setIdStatut(3);
		Assert.assertEquals(entiteDto.getListeTransitionAutorise(), new ArrayList<Transition>());

		// Parent en actif
		entiteDtoParent.setIdStatut(1);
		List<Transition> result = new ArrayList<Transition>();
		result.add(Transition.ACTIF_APRES_PREVISION);
		Assert.assertEqualsNoOrder(entiteDto.getListeTransitionAutorise().toArray(), result.toArray());

		// Parent en transitoire
		entiteDtoParent.setIdStatut(2);
		Assert.assertEqualsNoOrder(entiteDto.getListeTransitionAutorise().toArray(), result.toArray());

		// Actif
		entiteDto.setIdStatut(1);
		result = new ArrayList<Transition>();
		result.add(Transition.INACTIF_APRES_ACTIF);
		result.add(Transition.TRANSITOIRE);
		Assert.assertEqualsNoOrder(entiteDto.getListeTransitionAutorise().toArray(), result.toArray());

		// Transitoire
		entiteDto.setIdStatut(2);
		result = new ArrayList<Transition>();
		result.add(Transition.INACTIF_APRES_TRANSITOIRE);
		Assert.assertEqualsNoOrder(entiteDto.getListeTransitionAutorise().toArray(), result.toArray());

		// Inactif
		entiteDto.setIdStatut(3);
		result = new ArrayList<Transition>();
		Assert.assertEquals(entiteDto.getListeTransitionAutorise(), new ArrayList<Transition>());

	}

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
