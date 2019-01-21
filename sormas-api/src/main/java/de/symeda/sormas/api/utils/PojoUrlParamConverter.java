package de.symeda.sormas.api.utils;

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

public class PojoUrlParamConverter {
	
	public static String toUrlParams(Object source) {
	
		StringBuilder urlFilter = new StringBuilder();
		String encoding = "UTF-8";
		try {
			for (Method getter : source.getClass().getDeclaredMethods()) {
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
				
				Object value = getter.invoke(source);
				Class<?> type = getter.getReturnType();
				
				if (value != null) {
					if (urlFilter.length() > 0)
						urlFilter.append('&');
					urlFilter.append(URLEncoder.encode(propertyName, encoding));
					urlFilter.append('=');
					String stringValue;
					if (ReferenceDto.class.isAssignableFrom(type)) {
						stringValue = ((ReferenceDto)value).getUuid();
					} else if (Date.class.isAssignableFrom(type)) {
						stringValue = String.valueOf(((Date)value).getTime());
					} else if (Boolean.class.isAssignableFrom(type)) {
						stringValue = String.valueOf(((Boolean)value).booleanValue());
					} else if (Enum.class.isAssignableFrom(type)) {
						stringValue = ((Enum<?>)value).name();
					} else if (String.class.isAssignableFrom(type)) {
						stringValue = (String)value;
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T fromUrlParams(T target, String urlParams) {
		
		Map<String, List<String>> params = splitQuery(urlParams);
		
		try {
			for (Method getter : target.getClass().getDeclaredMethods()) {
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
				Method setter = target.getClass().getMethod(propertyName, type);
				
				if (params.containsKey(propertyName)) {
					List<String> fieldParams = params.get(propertyName);
					
					Object value;
					if (ReferenceDto.class.isAssignableFrom(type)) {
						value = type.newInstance();
						((ReferenceDto)value).setUuid(fieldParams.get(0));
					} else if (Date.class.isAssignableFrom(type)) {
						value = new Date(Long.valueOf(fieldParams.get(0)).longValue());
					} else if (Boolean.class.isAssignableFrom(type)) {
						value = Boolean.valueOf(fieldParams.get(0));
					} else if (Enum.class.isAssignableFrom(type)) {
						value = Enum.valueOf((Class<? extends Enum>)type, fieldParams.get(0));
					} else if (String.class.isAssignableFrom(type)) {
						value = fieldParams.get(0);
					} else {
						throw new NotImplementedException(type.toString());
					}
					
					setter.invoke(target, value);
				}
			}

		} catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		}
		
		return target;
	}
	
	public static Map<String, List<String>> splitQuery(String urlParams) {
	    if (DataHelper.isNullOrEmpty(urlParams)) {
	        return Collections.emptyMap();
	    }
		String encoding = "UTF-8";

		final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
		try {
		    final String[] pairs = urlParams.split("&");
		    for (String pair : pairs) {
		      final int idx = pair.indexOf("=");
		      String key;
				key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), encoding) : pair;
		      if (!query_pairs.containsKey(key)) {
		        query_pairs.put(key, new LinkedList<String>());
		      }
		      final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), encoding) : null;
		      query_pairs.get(key).add(value);
		    }
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	    return query_pairs;
	}
}