package nc.noumea.mairie.organigramme.core.query;

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

import nc.noumea.mairie.organigramme.core.utility.ApplicationContextUtils;
import nc.noumea.mairie.organigramme.core.utility.OrganigrammeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.SimpleListModel;

/**
 * SimpleListModel générique pour gérer les combos filtrées.
 */
public abstract class AbstractQueryListModel<T> extends SimpleListModel<T> {

	private static final int NOMBRE_CAR_MIN_DEFAUT = 2;

	private static final long serialVersionUID = 1L;

	private List<T> currentList = null;

	private static Logger log = LoggerFactory.getLogger(AbstractQueryListModel.class);

	public AbstractQueryListModel() {
		super(new ArrayList<T>());
	}

	public AbstractQueryListModel(List<T> liste) {
		super(liste);
	}

	/**
	 * Effectue une recherche d'entités T suivant un critère de recherche,
	 * éventuellement limité sur un nombre donné (indicatif).
	 * 
	 * @param critereRecherche
	 *            Critère de recherche, typiquement une chaine de caractères
	 * @param nombreResultatMaxIndicatif
	 *            nombre indicatif max d'éléments à retourner (la méthode peut
	 *            en tenir compte ou non)
	 * @return une liste d'éléments qui vérifient le critère de recherche
	 */
	@Override
	public ListModel<T> getSubModel(Object critereRecherche, int nombreResultatMaxIndicatif) {
		String chaineRecherche = OrganigrammeUtil.majusculeSansAccentTrim(critereRecherche == null ? ""
				: critereRecherche.toString());
		currentList = isChaineRechercheValide(chaineRecherche) ? findByQuery(chaineRecherche,
				nombreResultatMaxIndicatif) : Collections.<T> emptyList();
		return new ListModelList<T>(currentList);
	}

	/**
	 * Méthode à redéfinir dans les QueryListModel concrets
	 * 
	 * @param chaineRecherche
	 *            chaîne de recherche (typiquement tapée par l'utilisateur dans
	 *            une liste déroulante)
	 * @param nombreResultatMaxIndicatif
	 *            nombre indicatif limitatif (qu'on peut respecter ou non)
	 * @return une liste d'entités qui respectent le critère de recherche
	 */
	public abstract List<T> findByQuery(String chaineRecherche, int nombreResultatMaxIndicatif);

	/**
	 * @param chaineRecherche
	 *            chaineRecherche
	 * @return true si la chaîne recherchée est valide et que la recherche doit
	 *         être déclenchée (typiquement si la chaîne de recherche est
	 *         suffisamment longue). Cette méthode peut être redéfinie.
	 */
	protected boolean isChaineRechercheValide(String chaineRecherche) {
		return chaineRecherche != null && chaineRecherche.length() >= getNombreCarMin();
	}

	/**
	 * @return le nombre minimal de caractères pour déclencher la recherche
	 *         (dans l'implémentation par défaut de isChaineRechercheValide)
	 */
	protected int getNombreCarMin() {
		return NOMBRE_CAR_MIN_DEFAUT;
	}

	/**
	 * @param j
	 *            index de l'élément à retourner
	 * @return l'élément à la j ème position, null en cas de problème
	 */
	@Override
	public T getElementAt(int j) {
		try {
			return currentList.get(j);
		} catch (Exception e) {
			log.error("Erreur sur getElementAt", e);
			return null;
		}
	}

	/**
	 * Méthode raccourci pour récupérer un bean par son nom dans le contexte
	 * applicatif Spring.
	 * 
	 * @param nomBean
	 *            nom du bean à trouver
	 * @return le bean du contexte spring dont le nom est passé en argument
	 */
	protected Object getBean(String nomBean) {
		return ApplicationContextUtils.getApplicationContext().getBean(nomBean);
	}

}
