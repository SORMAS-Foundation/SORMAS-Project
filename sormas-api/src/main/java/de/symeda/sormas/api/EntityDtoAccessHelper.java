package de.symeda.sormas.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.symeda.sormas.api.utils.DataHelper;

public class EntityDtoAccessHelper {

	public static Object getPropertyValue(HasUuid entity, String propertyKey) throws InvocationTargetException, IllegalAccessException {
		if (entity == null) {
			return null;
		}
		Class<? extends HasUuid> entityClass = entity.getClass();
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
			entityClass = HasUuid.class.isAssignableFrom(superclass) ? (Class<? extends HasUuid>) superclass : null;
		}
		throw new IllegalArgumentException("No property " + propertyKey + " in class " + entity.getClass().getSimpleName());
	}

	public static Object getPropertyPathValue(HasUuid entity, String propertyPath) {
		return getPropertyPathValue(entity, propertyPath, null);
	}

	public static Object getPropertyPathValue(HasUuid entity, String propertyPath, IReferenceDtoResolver referenceDtoResolver) {
		String[] propertyKeys = propertyPath.split("[.]");
		Object currentEntity = entity;
		for (int i = 0; i < propertyKeys.length; i++) {
			if (currentEntity == null) {
				return null;
			}
			boolean isResolvable = referenceDtoResolver != null && ReferenceDto.class.isAssignableFrom(currentEntity.getClass());

			if (!HasUuid.class.isAssignableFrom(currentEntity.getClass())) {
				String errorPropertyPath = entity.getClass().getSimpleName() + "." + String.join(".", Arrays.copyOfRange(propertyKeys, 0, i));
				throw new IllegalArgumentException(errorPropertyPath + " is not an EntityDto or ReferenceDto");
			}
			Object propertyValue = null;
			try {
				propertyValue = getPropertyValue((HasUuid) currentEntity, propertyKeys[i]);
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			} catch (IllegalArgumentException e) {
				if (!isResolvable) {
					throw e;
				}
			}
			if (propertyValue != null) {
				currentEntity = propertyValue;
			} else {
				if (isResolvable) {
					try {
						currentEntity = getPropertyValue(referenceDtoResolver.resolve((ReferenceDto) currentEntity), propertyKeys[i]);
					} catch (InvocationTargetException | IllegalAccessException e) {
						throw new IllegalArgumentException(e);
					}
				} else {
					currentEntity = null;
				}
			}
		}
		return currentEntity;
	}

	public static String getPropertyPathValueString(HasUuid entity, String propertyPath, IReferenceDtoResolver referenceDtoResolver) {
		return DataHelper.valueToString(getPropertyPathValue(entity, propertyPath, referenceDtoResolver));
	}

	public interface IReferenceDtoResolver {

		EntityDto resolve(ReferenceDto referenceDto);
	}

	public static class CachedReferenceDtoResolver implements IReferenceDtoResolver {

		private Map<String, EntityDto> referenceCache = new HashMap<>();
		private IReferenceDtoResolver referenceDtoResolver;

		public CachedReferenceDtoResolver(IReferenceDtoResolver referenceDtoResolver) {
			this.referenceDtoResolver = referenceDtoResolver;
		}

		public EntityDto resolve(ReferenceDto referenceDto) {
			if (referenceDto != null) {
				EntityDto entityDto = referenceCache.get(referenceDto.getUuid());
				if (entityDto != null) {
					return entityDto;
				}
			}
			if (referenceDtoResolver != null) {
				EntityDto resolvedEntity = referenceDtoResolver.resolve(referenceDto);
				addReference(referenceDto, resolvedEntity);
				return resolvedEntity;
			}
			return null;
		}

		public void addReference(ReferenceDto referenceDto, EntityDto entityDto) {
			if (referenceDto != null && entityDto != null) {
				referenceCache.put(referenceDto.getUuid(), entityDto);
			}
		}
	}
}
