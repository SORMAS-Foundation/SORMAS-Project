/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.utils.criteria;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.IgnoreForUrl;

@SuppressWarnings("serial")
public abstract class BaseCriteria implements Serializable {

	public String toUrlParams() {

		StringBuilder urlFilter = new StringBuilder();
		String encoding = "UTF-8";
		try {
			for (Method getter : getClass().getDeclaredMethods()) {
				if (Modifier.isStatic(getter.getModifiers())
					|| Modifier.isPrivate(getter.getModifiers())
					|| !(getter.getName().startsWith("get") || getter.getName().startsWith("is"))
					|| getter.isAnnotationPresent(IgnoreForUrl.class))
					continue;

				String propertyName = getter.getName();
				if (propertyName.startsWith("get")) {
					propertyName = propertyName.substring(3, 4).toLowerCase() + propertyName.substring(4);
				} else if (propertyName.startsWith("is")) {
					propertyName = propertyName.substring(2, 3).toLowerCase() + propertyName.substring(3);
				}

				Object value = getter.invoke(this);
				Class<?> type = getter.getReturnType();

				if (value != null) {
					if (urlFilter.length() > 0)
						urlFilter.append('&');
					urlFilter.append(URLEncoder.encode(propertyName, encoding));
					urlFilter.append('=');
					String stringValue;
					if (ReferenceDto.class.isAssignableFrom(type)) {
						stringValue = ((ReferenceDto) value).getUuid();
					} else if (Date.class.isAssignableFrom(type)) {
						stringValue = String.valueOf(((Date) value).getTime());
					} else if (Boolean.class.isAssignableFrom(type)) {
						stringValue = String.valueOf(((Boolean) value).booleanValue());
					} else if (Enum.class.isAssignableFrom(type)) {
						stringValue = ((Enum<?>) value).name();
					} else if (String.class.isAssignableFrom(type)) {
						stringValue = (String) value;
					} else if (Integer.class.isAssignableFrom(type)) {
						stringValue = String.valueOf(value);
					} else if (EpiWeek.class.isAssignableFrom(type)) {
						stringValue = ((EpiWeek) value).toUrlString();
					} else if (CriteriaDateType.class.isAssignableFrom(type)) {
						stringValue = CriteriaDateTypeHelper.toUrlString((CriteriaDateType) value);
					} else if (CustomizableEnum.class.isAssignableFrom(type)) {
						stringValue = ((CustomizableEnum) value).getValue();
					} else {
						throw new NotImplementedException(type.toString());
					}
					urlFilter.append(URLEncoder.encode(stringValue, encoding));
				}
			}
		} catch (IllegalArgumentException | UnsupportedEncodingException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return urlFilter.toString();
	}

	public boolean hasAnyFilterActive() {

		try {
			for (Method getter : getClass().getDeclaredMethods()) {
				if (Modifier.isStatic(getter.getModifiers())
					|| Modifier.isPrivate(getter.getModifiers())
					|| !(getter.getName().startsWith("get") || getter.getName().startsWith("is")))
					continue;

				Object value = getter.invoke(this);
				Class<?> type = getter.getReturnType();

				if (String.class.isAssignableFrom(type)) {
					if (!DataHelper.isNullOrEmpty((String) value)) {
						return true;
					}
				} else if (Boolean.class.isAssignableFrom(type)) {
					if (value == Boolean.TRUE) {
						return true;
					}
				} else {
					if (value != null) {
						return true;
					}
				}
			}

			return false;
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void fromUrlParams(String urlParams) {
		Map<String, List<String>> params = splitQuery(urlParams);
		try {
			for (Method getter : getClass().getDeclaredMethods()) {
				if (Modifier.isStatic(getter.getModifiers())
					|| Modifier.isPrivate(getter.getModifiers())
					|| !(getter.getName().startsWith("get") || getter.getName().startsWith("is")))
					continue;

				String propertyName = getter.getName();
				if (propertyName.startsWith("get")) {
					propertyName = propertyName.substring(3, 4).toLowerCase() + propertyName.substring(4);
				} else if (propertyName.startsWith("is")) {
					propertyName = propertyName.substring(2, 3).toLowerCase() + propertyName.substring(3);
				}

				Class<?> type = getter.getReturnType();

				Method setter;
				try {
					setter = getClass().getMethod(propertyName, type);
				} catch (NoSuchMethodException e) {
					setter = getClass().getMethod("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1), type);
				}

				if (params.containsKey(propertyName)) {
					List<String> fieldParams = params.get(propertyName);

					Object value = parseUrlParam(type, fieldParams);

					setter.invoke(this, value);
				}
			}

		} catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	protected Object parseUrlParam(Class<?> type, List<String> fieldParams) throws InstantiationException, IllegalAccessException {
		Object value = null;
		if (ReferenceDto.class.isAssignableFrom(type)) {
			value = type.newInstance();
			((ReferenceDto) value).setUuid(fieldParams.get(0));
		} else if (Date.class.isAssignableFrom(type)) {
			try {
				value = new Date(Long.parseLong(fieldParams.get(0)));
			} catch (NumberFormatException e) {
				// ignore
			}
		} else if (Boolean.class.isAssignableFrom(type)) {
			value = Boolean.valueOf(fieldParams.get(0));
		} else if (Enum.class.isAssignableFrom(type)) {
			try {
				//noinspection unchecked
				value = Enum.valueOf((Class<Enum>) type, fieldParams.get(0));
			} catch (IllegalArgumentException e) {
				// ignore
			}
		} else if (String.class.isAssignableFrom(type)) {
			value = fieldParams.get(0);
		} else if (Integer.class.isAssignableFrom(type)) {
			value = Integer.valueOf(fieldParams.get(0));
		} else if (EpiWeek.class.isAssignableFrom(type)) {
			value = EpiWeek.fromUrlString(fieldParams.get(0));
		} else if (CustomizableEnum.class.isAssignableFrom(type)) {
			value = type.newInstance();
			((CustomizableEnum) value).setValue(fieldParams.get(0));
		} else {
			throw new NotImplementedException(type.toString());
		}
		return value;
	}

	public static Map<String, List<String>> splitQuery(String urlParams) {

		if (DataHelper.isNullOrEmpty(urlParams)) {
			return Collections.emptyMap();
		}
		String encoding = "UTF-8";

		final Map<String, List<String>> queryPairs = new LinkedHashMap<>();
		try {
			final String[] pairs = urlParams.split("&");
			for (String pair : pairs) {
				final int idx = pair.indexOf("=");
				String key;
				key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), encoding) : pair;
				if (!queryPairs.containsKey(key)) {
					queryPairs.put(key, new LinkedList<String>());
				}
				final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), encoding) : null;
				queryPairs.get(key).add(value);
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return queryPairs;
	}
}
