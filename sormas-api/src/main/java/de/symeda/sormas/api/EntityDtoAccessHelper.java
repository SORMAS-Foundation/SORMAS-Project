/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

	public static Object getPropertyValue(HasUuid entity, String propertyKey) throws InvocationTargetException, IllegalAccessException {
		if (entity == null) {
			return null;
		}
		Class<?> entityClass = entity.getClass();
		while (HasUuid.class.isAssignableFrom(entityClass)) {
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
			entityClass = entityClass.getSuperclass();
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

			Object propertyValue = null;
			try {
				propertyValue = getPropertyValue((HasUuid) currentEntity, propertyKeys[i]);
			} catch (InvocationTargetException | IllegalAccessException e) {
				throwIllegalArgumentException(e, entity, propertyKeys, i);
			} catch (ClassCastException e) {
				String entityClass = currentEntity.getClass().getSimpleName();
				String message = entityClass + "." + propertyKeys[i] + " cannot be resolved.";
				throwIllegalArgumentException(new IllegalArgumentException(message, e), entity, propertyKeys, i);
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

	private static void throwIllegalArgumentException(Exception e, HasUuid entity, String[] propertyKeys, int i) {
		String errorPropertyPath = cleanDictionaryClassNames(entity.getClass().getSimpleName())
			+ (i > 0 ? "." : "")
			+ StringUtils.join(Arrays.copyOfRange(propertyKeys, 0, i), ".");
		throw new IllegalArgumentException("In " + errorPropertyPath + ": " + cleanDictionaryClassNames(e.getMessage()), e);
	}

	public static Object getPropertyPathValueString(HasUuid entity, String propertyPath, IReferenceDtoResolver referenceDtoResolver) {
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
