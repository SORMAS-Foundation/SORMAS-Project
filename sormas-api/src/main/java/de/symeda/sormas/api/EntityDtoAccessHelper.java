package de.symeda.sormas.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class EntityDtoAccessHelper {

	public static <T extends EntityDto> Object getPropertyValue(T entity, String propertyKey)
		throws InvocationTargetException, IllegalAccessException {
		Class<? extends EntityDto> entityClass = entity.getClass();
		while (entityClass != null) {
			Method[] declaredMethods = entityClass.getDeclaredMethods();
			for (Method method : declaredMethods) {
				String methodName = method.getName();
				if (methodName.startsWith("get") || methodName.startsWith("is")) {
					String propertyName = methodName.replaceAll("(^(is|get))|((Reference)?Dto$)", "").toUpperCase();
					if (propertyName.equals(propertyKey.toUpperCase())) {
						return method.invoke(entity);
					}
				}
			}
			Class<?> superclass = entityClass.getSuperclass();
			entityClass = EntityDto.class.isAssignableFrom(superclass) ? (Class<? extends EntityDto>) superclass : null;
		}
		throw new IllegalArgumentException("No property " + propertyKey + " in class " + entity.getClass().getSimpleName());
	}

	public static <T extends EntityDto> Object getPropertyPathValue(T entity, String propertyPath) {
		String[] propertykeys = propertyPath.split("[.]");
		Object currentEntity = entity;
		for (int i = 0; i < propertykeys.length; i++) {
			if (currentEntity == null) {
				return null;
			}
			if (!EntityDto.class.isAssignableFrom(currentEntity.getClass())) {
				String errorPropertyPath = entity.getClass().getSimpleName() + "." + String.join(".", Arrays.copyOfRange(propertykeys, 0, i));
				throw new IllegalArgumentException(errorPropertyPath + " is not an EntityDto");
			}
			try {
				currentEntity = getPropertyValue((EntityDto) currentEntity, propertykeys[i]);
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return currentEntity;
	}
}
