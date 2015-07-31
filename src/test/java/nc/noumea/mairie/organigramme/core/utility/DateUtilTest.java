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

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DateUtilTest {

	@Test
	public void debutJournee() {
		Date date = DateUtil.parseDateTime("31/12/2013 23:15");
		Assert.assertEquals(DateUtil.formatDateTime(DateUtil.debutJournee(date)), "31/12/2013 00:00");

		// avec des ms cette fois
		Assert.assertTrue(DateUtil.formatDateTime(DateUtil.debutJournee(new Date())).endsWith(" 00:00"));

		// variante avec DateTime
		Assert.assertTrue(DateUtil.formatDateTime(DateUtil.debutJournee(new DateTime()).toDate()).endsWith(" 00:00"));
	}

	@Test
	public void aujourdhui00h00() {
		Assert.assertTrue(DateUtil.formatDateTime(DateUtil.aujourdhui00h00()).endsWith(" 00:00"));
	}

	@Test
	public void formatDate() {
		Assert.assertEquals(DateUtil.formatDate(null), "");

		Date date = DateUtil.parseDateTime("31/12/2013 23:59");
		Assert.assertEquals(DateUtil.formatDate(date), "31/12/2013"); // heures/minutes
																		// non
																		// affichées
	}

	@Test
	public void formatDateTime() {
		Assert.assertEquals(DateUtil.formatDateTime(null), "");

		Date date = DateUtil.parseDateTime("31/12/2013 23:59");
		Assert.assertEquals(DateUtil.formatDateTime(date), "31/12/2013 23:59");
	}

	@Test
	public void formatTime() {
		Assert.assertEquals(DateUtil.formatHeureMinute(null), "");
		Date date = DateUtil.parseDateTime("31/12/2013 23:59");
		Assert.assertEquals(DateUtil.formatHeureMinute(date), "23:59");
	}

	@Test
	public void parseDate() {
		Assert.assertNull(DateUtil.parseDate(null));

		Date date = DateUtil.parseDate("31/12/2013");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Assert.assertEquals(calendar.get(Calendar.YEAR), 2013);
		Assert.assertEquals(calendar.get(Calendar.MONTH), 11); // 11 = décembre
		Assert.assertEquals(calendar.get(Calendar.DAY_OF_MONTH), 31);
		Assert.assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 0);
		Assert.assertEquals(calendar.get(Calendar.MINUTE), 0);
		Assert.assertEquals(calendar.get(Calendar.SECOND), 0);
		Assert.assertEquals(calendar.get(Calendar.MILLISECOND), 0);
	}

	@Test
	public void parseDateTime() {
		Assert.assertNull(DateUtil.parseDateTime(null));

		Date date = DateUtil.parseDateTime("31/12/2013 23:59");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Assert.assertEquals(calendar.get(Calendar.YEAR), 2013);
		Assert.assertEquals(calendar.get(Calendar.MONTH), 11); // 11 = décembre
		Assert.assertEquals(calendar.get(Calendar.DAY_OF_MONTH), 31);
		Assert.assertEquals(calendar.get(Calendar.HOUR_OF_DAY), 23);
		Assert.assertEquals(calendar.get(Calendar.MINUTE), 59);
		Assert.assertEquals(calendar.get(Calendar.SECOND), 0);
		Assert.assertEquals(calendar.get(Calendar.MILLISECOND), 0);
	}

	@Test
	public void isNotNullAndAfter() {
		Assert.assertTrue(DateUtil.isNotNullAndAfter(DateUtil.parseDate("15/01/2014"), DateUtil.parseDate("14/01/2014")));

		Assert.assertFalse(DateUtil.isNotNullAndAfter(DateUtil.parseDate("14/01/2014"),
				DateUtil.parseDate("15/01/2014")));
		Assert.assertFalse(DateUtil.isNotNullAndAfter(DateUtil.parseDate("15/01/2014"),
				DateUtil.parseDate("15/01/2014")));
		Assert.assertFalse(DateUtil.isNotNullAndAfter(null, null));
		Assert.assertFalse(DateUtil.isNotNullAndAfter(null, new Date()));
		Assert.assertFalse(DateUtil.isNotNullAndAfter(new Date(), null));
	}

	@Test
	public void isNotNullAndAfterAndEquals() {
		Assert.assertTrue(DateUtil.isNotNullAndAfterOrEquals(DateUtil.parseDate("15/01/2014"),
				DateUtil.parseDate("14/01/2014")));
		Assert.assertTrue(DateUtil.isNotNullAndAfterOrEquals(DateUtil.parseDate("15/01/2014"),
				DateUtil.parseDate("15/01/2014")));

		Assert.assertFalse(DateUtil.isNotNullAndAfter(DateUtil.parseDate("14/01/2014"),
				DateUtil.parseDate("15/01/2014")));
		Assert.assertFalse(DateUtil.isNotNullAndAfter(null, null));
		Assert.assertFalse(DateUtil.isNotNullAndAfter(null, new Date()));
		Assert.assertFalse(DateUtil.isNotNullAndAfter(new Date(), null));
	}

	@Test
	public void isNotNullAndAfterNMonth() {
		Assert.assertTrue(DateUtil.isNotNullAndAfterNMonth(DateUtil.parseDate("15/01/2014"),
				DateUtil.parseDate("14/01/2014"), 0));
		Assert.assertFalse(DateUtil.isNotNullAndAfterNMonth(DateUtil.parseDate("15/01/2014"),
				DateUtil.parseDate("14/01/2014"), 1));

		Assert.assertTrue(DateUtil.isNotNullAndAfterNMonth(DateUtil.parseDate("15/02/2014"),
				DateUtil.parseDate("14/01/2014"), 1));
		Assert.assertFalse(DateUtil.isNotNullAndAfterNMonth(DateUtil.parseDate("15/02/2014"),
				DateUtil.parseDate("14/01/2014"), 2));

		Assert.assertTrue(DateUtil.isNotNullAndAfterNMonth(DateUtil.parseDate("15/01/2016"),
				DateUtil.parseDate("14/01/2014"), 24));
		Assert.assertFalse(DateUtil.isNotNullAndAfterNMonth(DateUtil.parseDate("14/01/2016"),
				DateUtil.parseDate("14/01/2014"), 24));
	}

	@Test
	public void libelleMois() {
		Assert.assertEquals(DateUtil.libelleMois(1), "janvier");
		Assert.assertEquals(DateUtil.libelleMois(12), "décembre");
		Assert.assertEquals(DateUtil.libelleMois(9), "septembre");
	}

	@Test
	public void formatDateAvecMoisEnTexte() {
		Assert.assertEquals(DateUtil.formatDateAvecMoisEnTexte(null), "");
		Assert.assertEquals(DateUtil.formatDateAvecMoisEnTexte(DateUtil.parseDate("28/02/1965")), "28 février 1965");
		Assert.assertEquals(DateUtil.formatDateAvecMoisEnTexte(DateUtil.parseDate("01/01/2014")), "1 janvier 2014");
		Assert.assertEquals(DateUtil.formatDateAvecMoisEnTexte(DateUtil.parseDate("31/12/2015")), "31 décembre 2015");
	}

	@Test
	public void formatDateTimeFriendly() {
		Assert.assertEquals(DateUtil.formatDateTimeFriendly(null), "");
		Assert.assertEquals(DateUtil.formatDateTimeFriendly(DateUtil.parseDateTime("28/02/1965 12:25")),
				"28/02/1965 à 12h25");
		Assert.assertEquals(DateUtil.formatDateTimeFriendly(DateUtil.parseDateTime("01/01/2014 23:59")),
				"01/01/2014 à 23h59");
		Assert.assertEquals(DateUtil.formatDateTimeFriendly(DateUtil.parseDateTime("31/12/2015 00:00")),
				"31/12/2015 à 00h00");
	}

	@Test
	public void testCompare() {
		Date date1 = DateUtil.parseDate("01/03/2015");
		Date date2 = DateUtil.parseDate("02/03/2015");
		Assert.assertEquals(0, DateUtil.compare(date1, date1));
		Assert.assertEquals(0, DateUtil.compare(null, null));
		Assert.assertEquals(1, DateUtil.compare(date2, date1));
		Assert.assertEquals(-1, DateUtil.compare(date1, date2));
		Assert.assertEquals(-1, DateUtil.compare(null, date1));
		Assert.assertEquals(1, DateUtil.compare(date1, null));
	}
}
