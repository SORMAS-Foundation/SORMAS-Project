package de.symeda.sormas.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.BurialInfoDto;
import de.symeda.sormas.api.utils.DataHelper;

public class EntityDtoAccessHelper {

	public static Object getPropertyValue(Object entity, String propertyKey) throws InvocationTargetException, IllegalAccessException {
		if (entity == null) {
			return null;
		}
		Class<?> entityClass = entity.getClass();
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
			entityClass = superclass.isAssignableFrom(Object.class) ? null : superclass;
		}
		throw new IllegalArgumentException(
			"No property " + propertyKey + " in class " + (entity.getClass() != null ? entity.getClass().getSimpleName() : "<null>"));
	}

	public static Object getPropertyPathValue(Object entity, String propertyPath) {
		return getPropertyPathValue(entity, propertyPath, null);
	}

	public static Object getPropertyPathValue(Object entity, String propertyPath, IReferenceDtoResolver referenceDtoResolver) {
		String[] propertyKeys = propertyPath.split("[.]");
		Object currentEntity = entity;
		for (int i = 0; i < propertyKeys.length; i++) {
			if (currentEntity == null) {
				return null;
			}
			boolean isResolvable = referenceDtoResolver != null && ReferenceDto.class.isAssignableFrom(currentEntity.getClass());

			Object propertyValue = null;
			try {
				propertyValue = getPropertyValue(currentEntity, propertyKeys[i]);
			} catch (InvocationTargetException | IllegalAccessException e) {
				throwIllegalArgumentException(e, entity, propertyKeys, i);
			} catch (IllegalArgumentException e) {
				if (!isResolvable) {
					throwIllegalArgumentException(e, entity, propertyKeys, i);
				}
			}
			if (propertyValue != null) {
				currentEntity = propertyValue;
			} else {
				if (isResolvable) {
					try {
						currentEntity = getPropertyValue(referenceDtoResolver.resolve((ReferenceDto) currentEntity), propertyKeys[i]);
					} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
						throwIllegalArgumentException(e, entity, propertyKeys, i);
					}
				} else {
					currentEntity = null;
				}
			}
		}
		return currentEntity;
	}

	private static void throwIllegalArgumentException(Exception e, Object entity, String[] propertyKeys, int i) {
		String errorPropertyPath = cleanDictionaryClassNames(entity.getClass().getSimpleName())
			+ (i > 0 ? "." : "")
			+ StringUtils.join(Arrays.copyOfRange(propertyKeys, 0, i), ".");
		throw new IllegalArgumentException("In " + errorPropertyPath + ": " + cleanDictionaryClassNames(e.getMessage()), e);
	}

	public static Object getPropertyPathValueString(Object entity, String propertyPath, IReferenceDtoResolver referenceDtoResolver) {
		Object value = getPropertyPathValue(entity, propertyPath, referenceDtoResolver);
		return formatObject(value);
	}

	public static Object formatObject(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof Date
			|| value instanceof BurialInfoDto
			|| value instanceof BirthDateDto
			|| value.getClass().equals(Boolean.class)) {
			return DataHelper.valueToString(value);
		} else {
			return value;
		}
	}

	public static String cleanDictionaryClassNames(String className) {
		return className.replaceAll("(Reference)?Dto", "");
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
