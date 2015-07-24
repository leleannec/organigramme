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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;
import nc.noumea.mairie.organigramme.core.viewmodel.AbstractViewModel;
import nc.noumea.mairie.organigramme.dto.EntiteDto;
import nc.noumea.mairie.organigramme.entity.CouleurTypeEntite;
import nc.noumea.mairie.organigramme.enums.Statut;
import nc.noumea.mairie.organigramme.query.EntiteDtoQueryListModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import org.zkoss.zhtml.Li;
import org.zkoss.zhtml.Ul;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;

/**
 * Classe utilitaire permettant de créer l'organigramme
 */
public class TreeViewModel extends AbstractViewModel<EntiteDto> implements Serializable {

	private static final long		serialVersionUID	= 1L;

	private OrganigrammeViewModel	organigrammeViewModel;

	public TreeViewModel(OrganigrammeViewModel organigrammeViewModel) {
		this.organigrammeViewModel = organigrammeViewModel;
	}

	public EntiteDto entity() {
		return organigrammeViewModel.getEntity();
	}

	/**
	 * Crée l'arbre et l'ajoute au {@link Vlayout} client
	 * @param entiteDtoRoot : l'ancien arbre
	 */
	public void creeArbre(EntiteDto entiteDtoRoot) {

		// On renseigne deux map<idTypeEntite, couleur> qui permettront de setter chaque couleur de chaque EntiteDto de l'arbre
		Map<Long, String> mapIdTypeEntiteCouleurEntite = new HashMap<Long, String>();
		Map<Long, String> mapIdTypeEntiteCouleurTexte = new HashMap<Long, String>();
		List<CouleurTypeEntite> listeCouleurTypeEntite = organigrammeViewModel.couleurTypeEntiteService.findAll();
		for (CouleurTypeEntite couleurTypeEntite : listeCouleurTypeEntite) {
			mapIdTypeEntiteCouleurEntite.put(couleurTypeEntite.getIdTypeEntite(), couleurTypeEntite.getCouleurEntite());
			mapIdTypeEntiteCouleurTexte.put(couleurTypeEntite.getIdTypeEntite(), couleurTypeEntite.getCouleurTexte());
		}

		Vlayout vlayout = organigrammeViewModel.vlayout;
		Component arbre = genereArbre(organigrammeViewModel.entiteDtoRoot, mapIdTypeEntiteCouleurEntite, mapIdTypeEntiteCouleurTexte);
		if (!CollectionUtils.isEmpty(vlayout.getChildren())) {
			vlayout.removeChild(vlayout.getChildren().get(0));
		}
		if (arbre == null) {
			return;
		}
		vlayout.appendChild(arbre);
	}

	/**
	 * Génére les composants {@link Ul}/{@link Li} qui forment l'arbre (ces {@link Ul}/{@link Li} html seront retravaillées par le plugin jQuery Org Chart afin
	 * de les transformer en div/table/tr/td et de les afficher sous forme d'arbre)
	 * @param entiteDto : le {@link EntiteDto} racine qui contient tout l'arbre
	 * @param mapIdTypeEntiteCouleurEntite : la map des différentes couleurs pour les types d'entités qui va nous permettre de setter chaque couleur de chaque
	 *            type d'entité
	 * @param mapIdTypeEntiteCouleurTexte : la map des différentes couleurs pour les types d'entités qui va nous permettre de setter chaque couleur de chaque
	 *            type d'entité
	 * @return le {@link Ul} de plus haut niveau qui est ajouté au {@link Vlayout}
	 */
	public Component genereArbre(EntiteDto entiteDto, Map<Long, String> mapIdTypeEntiteCouleurEntite, Map<Long, String> mapIdTypeEntiteCouleurTexte) {

		if (entiteDto == null) {
			return null;
		}

		// On vide la map de correspondance id HTML<->EntiteDTO car on va la renseigner dans creeLiEntite()
		organigrammeViewModel.mapIdLiEntiteDto = new HashMap<String, EntiteDto>();

		// On recrée la liste de toutes les entités
		organigrammeViewModel.setListeEntite(new ArrayList<EntiteDto>());

		// Initialisation de l'entité ROOT
		Ul ulRoot = new Ul();
		ulRoot.setId("organigramme-root");
		ulRoot.setSclass("hide");
		setStatutEtCouleur(mapIdTypeEntiteCouleurEntite, mapIdTypeEntiteCouleurTexte, entiteDto);
		Li li = creeLiEntite(ulRoot, entiteDto);
		organigrammeViewModel.getListeEntite().add(entiteDto);

		// Initialisation du reste de l'arbre
		genereArborescenceHtml(entiteDto.getEnfants(), li, mapIdTypeEntiteCouleurEntite, mapIdTypeEntiteCouleurTexte);

		// Maintenant qu'on a setté la liste de entités disponibles à la recherche et/ou au zoom, on renseigne la ListModel
		organigrammeViewModel.setEntiteDtoQueryListModel(new EntiteDtoQueryListModel(organigrammeViewModel.getListeEntite()));

		return ulRoot;
	}

	/**
	 * Méthode récursive pour générer tous les {@link Ul}/{@link Li} formant l'arbre
	 * @param listeEntiteDTO : la liste des enfants de l'entité courante
	 * @param component : le composant sous lequel ajouter les enfants
	 * @param mapIdTypeEntiteCouleur : la map des différentes couleurs pour les types d'entités qui va nous permettre de setter chaque couleur de chaque type
	 *            d'entité mapIdTypeEntiteCouleurTexte : la map des différentes couleurs pour les types d'entités qui va nous permettre de setter chaque couleur
	 *            de chaque type d'entité
	 * @return l'Ul représentant l'arbre complet (sans l'entité root qui est initialisée dans "genereArbre")
	 */
	public Component genereArborescenceHtml(List<EntiteDto> listeEntiteDTO, Component component, Map<Long, String> mapIdTypeEntiteCouleur,
			Map<Long, String> mapIdTypeEntiteCouleurTexte) {

		Ul ul = new Ul();
		ul.setParent(component);

		for (final EntiteDto entiteDto : listeEntiteDTO) {
			setStatutEtCouleur(mapIdTypeEntiteCouleur, mapIdTypeEntiteCouleurTexte, entiteDto);

			Li li = creeLiEntite(ul, entiteDto);

			organigrammeViewModel.getListeEntite().add(entiteDto);

			if (entiteDto.hasChildren()) {
				genereArborescenceHtml(entiteDto.getEnfants(), li, mapIdTypeEntiteCouleur, mapIdTypeEntiteCouleurTexte);
			}
		}

		return ul;
	}

	public void setStatutEtCouleur(Map<Long, String> mapIdTypeEntiteCouleurEntite, Map<Long, String> mapIdTypeEntiteCouleurTexte, final EntiteDto entiteDto) {
		// On se le statut de l'entité
		entiteDto.setStatut(Statut.getStatutById(entiteDto.getIdStatut()));

		// On set la couleur du type d'entité
		if (entiteDto.getTypeEntite() != null) {
			entiteDto.getTypeEntite().setCouleurEntite(mapIdTypeEntiteCouleurEntite.get(entiteDto.getTypeEntite().getId()));
			entiteDto.getTypeEntite().setCouleurTexte(mapIdTypeEntiteCouleurTexte.get(entiteDto.getTypeEntite().getId()));
		}
	}

	/**
	 * Crée une feuille unitaire et lui ajoute un événement onClick qui permettra d'effecture des opérations sur cette feuille
	 * @param ul : le {@link Ul} parent
	 * @param entiteDto : l'{@link EntiteDto} a transformer en {@link Li}
	 * @return le composant {@link Li} représentant l'entité
	 */
	public Li creeLiEntite(Ul ul, final EntiteDto entiteDto) {

		Li li = new Li();
		String sigleSplit = OrganigrammeUtil.splitByNumberAndSeparator(entiteDto.getSigle(), 8, "\n");
		li.appendChild(new Label(sigleSplit));
		li.setParent(ul);
		li.setSclass("statut-" + StringUtils.lowerCase(entiteDto.getStatut().name()));
		boolean couleurEntiteTypeEntiteRenseigne = entiteDto.getTypeEntite() != null && !StringUtils.isBlank(entiteDto.getTypeEntite().getCouleurEntite());
		if (couleurEntiteTypeEntiteRenseigne) {
			li.setStyle("background-color:" + entiteDto.getTypeEntite().getCouleurEntite() + "; color:" + entiteDto.getTypeEntite().getCouleurTexte() + ";");
		} else {
			li.setStyle("background-color:#FFFFCF; color:#000000;");
		}

		li.setId("entite-id-" + entiteDto.getId().toString());
		if (this.organigrammeViewModel.mapIdLiOuvert.get(li.getId()) == null) {
			this.organigrammeViewModel.mapIdLiOuvert.put(li.getId(), false);
		}

		entiteDto.setLi(li);

		// On maintient une map permettant d'aller plus vite lors d'un click event pour retrouver l'EntiteDto correspondant à l'id du Li
		organigrammeViewModel.mapIdLiEntiteDto.put(li.getId(), entiteDto);

		return li;
	}
}
