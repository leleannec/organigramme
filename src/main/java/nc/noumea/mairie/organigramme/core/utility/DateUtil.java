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

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtil {

	private static final DateTimeFormatter YEAR_FORMATTER = dateTimeFormatterNCForPattern("yyyy");
	private static final DateTimeFormatter TIME_FORMATTER = dateTimeFormatterNCForPattern("HH:mm");
	private static final DateTimeFormatter TIME_FORMATTER_H = dateTimeFormatterNCForPattern("HH'h'mm");
	private static final DateTimeFormatter DATE_FORMATTER_FOR_FILE = dateTimeFormatterNCForPattern("yyyy-MM-dd");
	private static final DateTimeFormatter DATE_FORMATTER = dateTimeFormatterNCForPattern("dd/MM/yyyy");
	private static final DateTimeFormatter DATETIME_FORMATTER = dateTimeFormatterNCForPattern("dd/MM/yyyy HH:mm");
	private static final DateTimeFormatter DATETIME_FORMATTER_FRIENDLY = dateTimeFormatterNCForPattern("dd/MM/yyyy 'à' HH'h'mm");
	private static final String TIMEZONE_NOUMEA = "Pacific/Noumea";

	private static DateTimeFormatter dateTimeFormatterNCForPattern(String pattern) {
		return DateTimeFormat.forPattern(pattern).withZone(DateTimeZone.forID(TIMEZONE_NOUMEA));
	}

	/**
	 * Retourne la date représentée au format dd/MM/yyyy, ex : 31/12/2013
	 * 
	 * @param date
	 *            date
	 * @return une représentation de la date en paramètre, ou "" si date est
	 *         null
	 */
	public static String formatDate(Date date) {
		return date == null ? "" : DATE_FORMATTER.print(new DateTime(date));
	}

	/**
	 * Retourne la partie heure d'une date au format HH:mm, ex : "23:59"
	 * 
	 * @param date
	 *            date
	 * @return une représentation de la partie heure de la date en paramètre, ou
	 *         "" si date est null
	 */
	public static String formatHeureMinute(Date date) {
		return date == null ? "" : TIME_FORMATTER.print(new DateTime(date));
	}

	/**
	 * Retourne la partie heure d'une date au format HHhmm, ex : "23h59"
	 * 
	 * @param date
	 *            date
	 * @return une représentation de la partie heure de la date en paramètre, ou
	 *         "" si date est null
	 */
	public static String formatHeureMinuteH(Date date) {
		return date == null ? "" : TIME_FORMATTER_H.print(new DateTime(date));
	}

	/**
	 * Retourne la date représentée au format yyyy, ex : 2014
	 * 
	 * @param date
	 *            date
	 * @return une représentation de la date en paramètre, ou "" si date est
	 *         null
	 */
	public static String formatYear(Date date) {
		if (date == null) {
			return "";
		}
		return YEAR_FORMATTER.print(new DateTime(date));
	}

	/**
	 * Retourne la date/heure représentée au format dd/MM/yyyy HH:mm, ex :
	 * 31/12/2013 23:59
	 * 
	 * @param date
	 *            date
	 * @return une représentation de la date en paramètre, ou "" si date est
	 *         null
	 */
	public static String formatDateTime(Date date) {
		if (date == null) {
			return "";
		}
		return DATETIME_FORMATTER.print(new DateTime(date));
	}

	/**
	 * Retourne la date/heure représentée au format dd/MM/yyyy à HH'h'mm, ex :
	 * 31/12/2013 à 23h59
	 * 
	 * @param date
	 *            date
	 * @return une représentation de la date en paramètre, ou "" si date est
	 *         null
	 */
	public static String formatDateTimeFriendly(Date date) {
		if (date == null) {
			return "";
		}
		return DATETIME_FORMATTER_FRIENDLY.print(new DateTime(date));
	}

	/**
	 * Retourne la date/heure représentée au format yyyy-MM-dd
	 * 
	 * @param date
	 *            date
	 * @return une représentation de la date en paramètre, ou "" si date est
	 *         null
	 */
	public static String formatDateForFile(Date date) {
		if (date == null) {
			return "";
		}
		return DATE_FORMATTER_FOR_FILE.print(new DateTime(date));
	}

	/**
	 * Retoure une représentation de la date
	 * 
	 * @param date
	 *            date concernée
	 * @return exemple : "7 janvier 2014", "" si la date en entrée est null
	 */
	public static String formatDateAvecMoisEnTexte(Date date) {
		if (date == null) {
			return "";
		}
		DateTime dateTime = new DateTime(date);
		return dateTime.getDayOfMonth() + " " + libelleMois(dateTime.getMonthOfYear()) + " " + dateTime.getYear();
	}

	/**
	 * Retourne une chaîne d'horodatage de la date courante, pratique pour
	 * suffixer le nom des fichiers générés notamment.
	 * 
	 * @return horodatage
	 */
	public static String getHorodatage() {
		return DateTime.now().toString("YYYYMMddHHmmss"); // #5784
	}

	public static DateTime debutJournee(DateTime d) {
		return d.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
	}

	public static Date debutJournee(Date d) {
		return debutJournee(new DateTime(d)).toDate();
	}

	public static DateTime aujourdhui00h00DateTime() {
		return debutJournee(new DateTime());
	}

	public static Date aujourdhui00h00() {
		return aujourdhui00h00DateTime().toDate();
	}

	public static Date parseDateTime(String str) {
		if (str == null) {
			return null;
		}
		return DATETIME_FORMATTER.parseDateTime(str).toDate();
	}

	public static Date parseDate(String str) {
		if (str == null) {
			return null;
		}
		return DATE_FORMATTER.parseDateTime(str).toDate();
	}

	/**
	 * Indique si date 1 est supérieur à date 2 (et les 2 non null).
	 * 
	 * @param date1
	 *            date qu'on souhaite comparer avec date2
	 * @param date2
	 *            date comparée
	 * @return true si date1 et date2 non null, et date1 supérieur date2
	 */
	public static boolean isNotNullAndAfter(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return false;
		}
		return date1.after(date2);
	}

	public static boolean isNotNullAndAfterOrEquals(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return false;
		}
		return date1.after(date2) || date1.equals(date2);
	}

	/**
	 * Indique si date 1 est supérieur à date 2 + nombreMois mois (et les 2
	 * dates non null).
	 * 
	 * @param date1
	 *            date qu'on souhaite comparer avec date2
	 * @param date2
	 *            date comparée
	 * @param nombreMois
	 *            nombreMois
	 * @return true si date1 et date2 non null, et date1 supérieur à date2 +
	 *         nombreMois mois
	 */
	public static boolean isNotNullAndAfterNMonth(Date date1, Date date2, int nombreMois) {
		if (date1 == null || date2 == null) {
			return false;
		}
		DateTime dateTime1 = new DateTime(date1);
		DateTime dateTime2 = new DateTime(date2);
		return dateTime1.isAfter(dateTime2.plusMonths(nombreMois));
	}

	private static final String[] LISTE_MOIS = new String[] { "janvier", "février", "mars", "avril", "mai", "juin",
			"juillet", "août", "septembre", "octobre", "novembre", "décembre" };

	/**
	 * @param monthOfYear
	 *            entre 1 et 12
	 * @return le libellé du mois correspondant à la date
	 */
	public static String libelleMois(int monthOfYear) {
		if (monthOfYear < 1 || monthOfYear > 12) {
			throw new IllegalArgumentException("mois en dehors de l'intervalle [1;12] : " + monthOfYear);
		}
		return LISTE_MOIS[monthOfYear - 1];
	}

	public static int compare(Date date1, Date date2) {
		if (date1 == null && date2 == null) {
			return 0; // null==null
		}

		if (date1 != null && date2 != null) {
			return date1.compareTo(date2);
		}

		if (date1 == null) {
			return -1; // null < date2
		}
		return 1; // date1 < null
	}
}
