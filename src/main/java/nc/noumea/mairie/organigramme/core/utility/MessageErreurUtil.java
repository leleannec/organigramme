package nc.noumea.mairie.organigramme.core.utility;

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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import nc.noumea.mairie.organigramme.core.entity.AbstractEntity;

import org.springframework.util.CollectionUtils;

/**
 * Classe utilitaire pour gérer les erreurs sur les entités
 * 
 * @author AgileSoft.NC
 */
public class MessageErreurUtil {

	private static final ValidatorFactory	factory	= Validation.buildDefaultValidatorFactory();

	/**
	 * Retourne une liste de violations concernant l'entity
	 * 
	 * @param entity entité concernée
	 * @return une liste de ConstraintViolation (null si aucune violation, ou si l'entité en entrée est null)
	 */
	private static <T> Set<ConstraintViolation<?>> validate(final T entity) {
		if (entity == null) {
			return null; // aucune erreur
		}
		final Set<ConstraintViolation<T>> violations = factory.getValidator().validate(entity, Default.class);
		return CollectionUtils.isEmpty(violations) ? null : new HashSet<ConstraintViolation<?>>(violations);
	}

	/**
	 * Construit une liste de message d'erreurs (de type violation de contrainte) concernant l'entité
	 * 
	 * @param entity entité concernée
	 * @return liste de message d'erreur, liste vide si aucune erreur (jamais null)
	 */
	public static List<MessageErreur> construitListeMessageErreurViolationContrainte(final AbstractEntity entity) {

		List<MessageErreur> result = new ArrayList<MessageErreur>();

		if (entity == null) {
			return result;
		}

		// On ajoute ensuite les contraintes de violoation (typiquement les champs qui ne peuvent être null)
		final Set<ConstraintViolation<?>> listeConstraintViolation = MessageErreurUtil.validate(entity);
		if (listeConstraintViolation != null) {
			for (final ConstraintViolation<?> violation : listeConstraintViolation) {
				result.add(new MessageErreur(violation.getMessage()));
			}
		}
		return result;
	}

	/**
	 * Retourne une représentation textuelle de la liste des messages d'erreurs (un message par ligne, préfixés par des tirets s'il y a plusieurs messages)
	 * 
	 * @param listeMessageErreur liste concernée
	 * @return une représentation multi-lignes, "" si la liste en entrée est vide ou null
	 */
	public static String construitReprListeMessageErreur(List<MessageErreur> listeMessageErreur) {
		if (CollectionUtils.isEmpty(listeMessageErreur)) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		boolean plusieursErreurs = listeMessageErreur.size() >= 2;
		for (MessageErreur messageErreur : listeMessageErreur) {
			if (messageErreur == null) {
				continue; // ne devrait pas arriver
			}
			if (plusieursErreurs) { // tirets seulement intéressants visuellement si plusieurs erreurs
				result.append("- ");
			}
			result.append(messageErreur.getMessage());
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * Méthode pratique, pour construire une liste de message d'erreur sur une liste d'entité
	 * 
	 * @param collectionEntity liste des entités concernées, si null, la méthode retourne une liste vide
	 * @return une liste (jamais null) de messages d'erreur concernant la liste des entités
	 */
	public static List<MessageErreur> construitListeMessageErreurCollection(Collection<? extends AbstractEntity> collectionEntity) {
		List<MessageErreur> result = new ArrayList<MessageErreur>();
		if (CollectionUtils.isEmpty(collectionEntity)) {
			return result; // liste vide
		}
		for (AbstractEntity entity : collectionEntity) {
			if (entity == null) {
				continue;
			}
			result.addAll(entity.construitListeMessageErreur());
		}
		return result;
	}

}