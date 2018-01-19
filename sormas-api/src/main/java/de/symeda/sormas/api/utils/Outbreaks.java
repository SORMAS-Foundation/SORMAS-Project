package de.symeda.sormas.api.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Used to annotate which fields are visible for outbreak situations.
 * 
 * @author Maté Strysewske
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Outbreaks {
	
	public static class OutbreaksConfiguration {
		
		private static final HashMap<Class<?>, List<String>> classConfigCache = 
				new HashMap<Class<?>, List<String>>();
		
		public static boolean isDefined(Class<?> clazz, String propertyName) {
			if (!classConfigCache.containsKey(clazz)) {
				readClassConfig(clazz);
			}
			
			List<String> classConfig = classConfigCache.get(clazz);
			return classConfig.contains(propertyName);
		}
		
		private static synchronized void readClassConfig(Class<?> clazz) {
			
			List<String> classConfig = new ArrayList<String>();
			
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(Outbreaks.class)) {
					classConfig.add(field.getName());
				}
			}
			
			// Entity class - needed because of the UUID
			for (Field field : clazz.getSuperclass().getDeclaredFields()) {
				if (field.isAnnotationPresent(Outbreaks.class)) {
					classConfig.add(field.getName());
				}
			}
			
			classConfigCache.put(clazz, classConfig);
			
		}
		
	}
	
}
