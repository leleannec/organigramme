package nc.noumea.mairie.organigramme.core.transformer;

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

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import flexjson.JSONException;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import flexjson.transformer.AbstractTransformer;

public class MSDateTransformer extends AbstractTransformer implements ObjectFactory {

	private static final String		msDateFormat		= "/[Dd][Aa][Tt][Ee]\\((\\-?[0-9]+)([\\+\\-]{1}[0-9]{4})*\\)/";
	private static final Pattern	msDateFormatPattern	= Pattern.compile(msDateFormat);

	@Override
	public void transform(Object arg0) {

		if (arg0 == null) {
			getContext().write(null);
			return;
		}

		DateTime dt = new DateTime(arg0);

		DateTimeFormatter formater = new DateTimeFormatterBuilder().appendLiteral("/Date(").appendLiteral(String.format("%s", dt.getMillis()))
				.appendPattern("Z").appendLiteral(")/").toFormatter();

		getContext().writeQuoted(formater.print(dt));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {

		Matcher matcher = msDateFormatPattern.matcher(value.toString());

		try {
			matcher.find();

			String timestamp = matcher.group(1);
			String timeZone = matcher.group(2);

			DateTime dt;

			if (timeZone != null)
				dt = new DateTime(Long.parseLong(timestamp), DateTimeZone.forID(timeZone));
			else
				dt = new DateTime(Long.parseLong(timestamp), DateTimeZone.UTC);

			return dt.toDate();
		} catch (Exception ex) {
			throw new JSONException(String.format("Unable to parse '%s' as a valid date time. Expected format is '%s'", value.toString(), msDateFormat), ex);
		}
	}

}
