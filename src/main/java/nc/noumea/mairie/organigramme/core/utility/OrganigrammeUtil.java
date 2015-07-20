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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Query;

import nc.noumea.mairie.organigramme.dto.EntiteDto;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class OrganigrammeUtil {

	private static final String			HEX_PATTERN													= "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
	private static Logger				log															= LoggerFactory.getLogger(OrganigrammeUtil.class);

	public static String capitalizeFullyFrench(String str) {
		return WordUtils.capitalizeFully(str, new char[] { '-', ' ' });
	}

	/**
	 * Convertit la chaine en entrée en majuscule sans accent, et sans blanc devant/derrière.
	 * 
	 * @param str chaîne concernée
	 * @return "" si la chaine en entrée est null
	 */
	public static String majusculeSansAccentTrim(String str) {
		if (str == null) {
			return "";
		}
		String s = StringUtils.trimToEmpty(str).toUpperCase();
		s = s.replaceAll("[ÀÂÄ]", "A");
		s = s.replaceAll("[ÈÉÊË]", "E");
		s = s.replaceAll("[ÏÎ]", "I");
		s = s.replaceAll("Ô", "O");
		s = s.replaceAll("[ÛÙÜ]", "U");
		s = s.replaceAll("Ç", "C");
		return s;
	}

	public static boolean sameIdAndNotNull(Long id1, Long id2) {
		if (id1 == null || id2 == null) {
			return false;
		}
		return id1.longValue() == id2.longValue();
	}

	public static String paddingZeroAGaucheSaufSiVide(String str, int nombreChiffre) {
		return StringUtils.isBlank(str) ? null : StringUtils.leftPad(str, nombreChiffre, "0");
	}

	public static void setParameter(Query query, String parameter, Object value) {
		log.debug("addParameter : " + parameter + " = " + value);
		query.setParameter(parameter, value);
	}

	public static String getMimeType(byte[] content) {
		try {
			MagicMatch match = Magic.getMagicMatch(content);
			return match.getMimeType();
		} catch (Exception e) {
			log.error("Erreur sur la détermination du type mime", e);
			return null;
		}
	}

	/**
	 * Méthode pour "charger" un élément, pour éviter pb de lazy loading
	 * @param element élément à charger
	 */
	public static void chargeElement(Object element) {
		// ne pas enlever cette ligne de debug, volontaire
		if (element != null) {
			log.debug(element.toString());
		}
	}

	/**
	 * Méthode pour "charger" une collection, pour éviter pb de lazy loading
	 * @param collection collection à charger
	 */
	public static void chargeCollection(@SuppressWarnings("rawtypes") Collection collection) {
		// ne pas enlever cette ligne de debug, volontaire
		if (collection != null) {
			log.debug(collection.toString());
		}
	}

	/**
	 * retourne la taille maximale annotée sur la propriété d'un objet
	 * @param object objet
	 * @param property nom de la propriété concernée
	 * @return taille max. déclarée
	 * @throws Exception exception en cas d'erreur
	 */
	public static Integer getMaxLength(Object object, String property) throws Exception {
		if (object == null) {
			return null;
		}
		return getMaxLengthGeneric(object.getClass(), property);
	}

	public static Integer getMaxLengthClassProperty(String className, String property) throws Exception {
		return getMaxLengthGeneric(Class.forName(className), property);
	}

	private static int getMaxLengthGeneric(Class<?> clazz, String property) throws Exception {
		return clazz.getDeclaredField(property).getAnnotation(Column.class).length();
	}

	public static Object getLastOrNull(List<?> liste) {
		return CollectionUtils.isEmpty(liste) ? null : liste.get(liste.size() - 1);
	}
	
	public static String getSimpleNameOfClass(@SuppressWarnings("rawtypes") Class clazz) {
		if (clazz == null) {
			return null;
		}
		String result = clazz.getSimpleName();
		String marqueur = "_$$"; // quelquefois le simple name contient _$$ suivi d'une chaîne générée, cette méthode permet de ne pas en tenir compte
		if (result.contains(marqueur)) {
			result = result.substring(0, result.indexOf(marqueur));
		}
		return result;
	}
	
	public static String getSimpleClassNameOfObject(Object object) {
		if (object == null) {
			return null;
		}
		return getSimpleNameOfClass(object.getClass());
	}

	public static String readUrlContent(String url) {
		InputStream in = null;
		try {
			in = new URL(url).openStream();
			return IOUtils.toString(in, "UTF-8");
		} catch (Exception e) {
			log.error("Erreur sur lecture du contenu de l'URL : " + url, e);
		} finally {
			if (in != null) {
				IOUtils.closeQuietly(in);
			}
		}
		return null;
	}

	/**
	 * Valide via une regexp un code couleur Html
	 * @param couleurHtml : le code couleur
	 * @return true si le code est dans le format #XXXXXX, false sinon
	 */
	public static boolean isCodeCouleurHtmlValide(String couleurHtml) {
		Pattern pattern = Pattern.compile(HEX_PATTERN);
		Matcher matcher = pattern.matcher(couleurHtml);
		return matcher.matches();
	}
	
	/**
	 * Permet de découper une chaine de caractère en bloc d'une certaine taille, reliés par un séparateur
	 * @param texte : le texte à découper
	 * @param number : la taille de chaque bloc
	 * @param separator : le séparateur permettant de relier chacun des blocx
	 * @return une chaine de caractère en bloc d'une certaine taille, reliés par un séparateur
	 */
	public static String splitByNumberAndSeparator(String texte, int number, String separator) {
		List<String> result = new ArrayList<String>();
		int index = 0;
		while (index < texte.length()) {
			result.add(texte.substring(index, Math.min(index + number,texte.length())));
		    index += number;
		}
		
		return StringUtils.join(result, separator); 
	}
	
	/**
	 * Permet de retrouver une entiteDto par son id
	 * @param entiteDto : l'arbre dans lequel on souhaite rechercher
	 * @param idEntite : l'id de l'entité recherché
	 * @param result : utile pour la récursivité, doit être appelé avec null
	 * @return l'entité correspondant à l'id passé en paramètre, null sinon
	 */
	public static EntiteDto findEntiteDtoDansArbreById(EntiteDto entiteDto, Integer idEntite, EntiteDto result) {
		
		if(entiteDto.getIdEntite().equals(idEntite)) {
			return entiteDto;
		}
		else {
			for(EntiteDto entiteDtoEnfant : entiteDto.getEnfants()) {
				result = findEntiteDtoDansArbreById(entiteDtoEnfant, idEntite, result);
			}
		}
		
		return result;
	}
}
