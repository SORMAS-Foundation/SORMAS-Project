package de.symeda.sormas.backend.common;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.HasUuid;

public class PatchHelper {

	/**
	 * This method updates the existingObject with the values received in the jsonObject
	 * 
	 * @param jsonObject
	 *            - the json payload containing the fields that are to be replaced. The structure of the json matches the structure of the
	 *            Dto given by the existingObject
	 * @param existingObject
	 *            - the existing object whose fields are to be updated
	 * @param <T>
	 *            - the type of the object
	 */
	public static <T> void postUpdate(@NotNull JsonNode jsonObject, @NotNull T existingObject) {

		try {
			Iterator<Map.Entry<String, JsonNode>> jsonObjectFieldMap = jsonObject.fields();
			while (jsonObjectFieldMap.hasNext()) {
				try {
					Map.Entry<String, JsonNode> jsonObjectFieldMapEntry = jsonObjectFieldMap.next();
					JsonNode jsonObjectFieldNode = jsonObjectFieldMapEntry.getValue();
					Field existingObjectField = getFieldByName(jsonObjectFieldMapEntry.getKey(), existingObject.getClass());
					if (existingObjectField == null) {
						throw new RuntimeException("No such field exception! " + jsonObjectFieldMapEntry.getKey());
					}
					existingObjectField.setAccessible(true);

					if (jsonObjectFieldNode.isObject()) {
						updateExistingObject(existingObject, jsonObjectFieldNode, existingObjectField);

					} else if (jsonObjectFieldNode.isArray()) {
						updateObjectList(existingObject, jsonObjectFieldNode, existingObjectField);

					} else {
						setFieldValue(existingObject, existingObjectField, jsonObjectFieldNode);
					}

				} catch (IllegalAccessException e) {
					e.printStackTrace();
					Logger logger = LoggerFactory.getLogger(existingObject.getClass());
					logger.error(String.format("Failed to update object" + existingObject.getClass().getSimpleName() + ". Error: " + e.getMessage()));
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method updates the fields of type collections (lists or array).
	 * 
	 * @param existingObject
	 * @param jsonObjectFieldNode
	 * @param existingObjectField
	 * @param <T>
	 * @throws JsonProcessingException
	 * @throws IllegalAccessException
	 */
	private static <T> void updateObjectList(T existingObject, JsonNode jsonObjectFieldNode, Field existingObjectField)
		throws JsonProcessingException, IllegalAccessException {
		Class listElementClass = getParameterizedType(existingObjectField);
		ArrayList tempNewElementList = new ArrayList();

		for (JsonNode listElement : jsonObjectFieldNode) {
			addOrReplaceListElement(existingObject, existingObjectField, listElementClass, tempNewElementList, listElement);
		}

		if (existingObjectField.getType().isAssignableFrom(List.class)) {
			Object existingObjectFieldInstance = existingObjectField.get(existingObject);
			List existingElementList = tempNewElementList;
			if (existingObjectFieldInstance != null) {
				existingElementList = (List) existingObjectFieldInstance;
				existingElementList.clear();
				existingElementList.addAll(tempNewElementList);
			} else {
				existingObjectField.set(existingObject, existingElementList);
			}
		} else if (existingObjectField.getType().isArray()) {
			existingObjectField.set(existingObject, tempNewElementList.toArray());
		}
	}

	/**
	 * This method update an existing object field. If the existingObject field id NULL a new instance is created.
	 * If the existing object field is not NULL the postUpdate method is called on the object field
	 * 
	 * @param existingObject
	 * @param jsonObjectFieldNode
	 * @param existingObjectField
	 * @param <T>
	 * @throws IllegalAccessException
	 * @throws JsonProcessingException
	 */
	private static <T> void updateExistingObject(T existingObject, JsonNode jsonObjectFieldNode, Field existingObjectField)
		throws IllegalAccessException, JsonProcessingException {
		Object existingFieldValue = existingObjectField.get(existingObject);
		if (existingFieldValue == null) {
			setFieldValue(existingObject, existingObjectField, jsonObjectFieldNode);
		} else {
			postUpdate(jsonObjectFieldNode, existingFieldValue);
		}
	}

	/**
	 * Add or replace the element in the list. The search is based on the UUID of the object. If an object with the given UUID is find in
	 * the list it is patched as all the other objects.
	 * If there is no element with that UUID in the list the new element is added. This method is specific to the project, For the object
	 * which re not instances of HasUuid (dont have an uuid),
	 * all the elements are replaced with the values form json
	 * 
	 * @param existingObject
	 * @param existingObjectField
	 * @param listElementClass
	 * @param tempNewElementList
	 * @param listElement
	 * @param <T>
	 * @throws JsonProcessingException
	 */
	private static <T> void addOrReplaceListElement(
		T existingObject,
		Field existingObjectField,
		Class listElementClass,
		ArrayList tempNewElementList,
		JsonNode listElement)
		throws JsonProcessingException {

		if (HasUuid.class.isAssignableFrom(listElementClass) && listElement.hasNonNull(EntityDto.UUID)) {
			Object existingListElement = getListElementByUuid(existingObject, existingObjectField, listElement.get(EntityDto.UUID).textValue());
			if (existingListElement != null) {
				postUpdate(listElement, existingListElement);
				tempNewElementList.add(existingListElement);
			} else {
				addObjectToList(tempNewElementList, listElement, listElementClass);
			}

		} else {
			addObjectToList(tempNewElementList, listElement, listElementClass);
		}
	}

	/**
	 * Replace the field value of the existing with the value from the json
	 * 
	 * @param existingObject
	 * @param existingField
	 * @param fieldJson
	 * @param <T>
	 * @throws IllegalAccessException
	 * @throws JsonProcessingException
	 */
	private static <T> void setFieldValue(@NotNull T existingObject, @NotNull Field existingField, JsonNode fieldJson)
		throws IllegalAccessException, JsonProcessingException {
		Object fieldValue = getObjectFromJsonNode(fieldJson, existingField.getType());
		existingField.set(existingObject, fieldValue);

	}

	/**
	 * Deserialized the json to the java object. The method treats the special case of enumeration and also the case when the json is null
	 * 
	 * @param fieldJson
	 * @param objectClass
	 * @return
	 * @throws JsonProcessingException
	 */
	private static Object getObjectFromJsonNode(JsonNode fieldJson, @NotNull Class objectClass) throws JsonProcessingException {
		Object object = null;
		if (!fieldJson.isNull()) {

			if (objectClass.isEnum()) {
				object = Enum.valueOf((Class<Enum>) objectClass, fieldJson.textValue());
			} else {
				ObjectMapper mapper = new ObjectMapper();
				object = mapper.treeToValue(fieldJson, objectClass);
			}
		}
		return object;
	}

	/**
	 * 
	 * @param list
	 * @param objectJson
	 * @param objectClass
	 * @throws JsonProcessingException
	 */
	private static void addObjectToList(@NotNull List list, JsonNode objectJson, @NotNull Class objectClass) throws JsonProcessingException {
		Object object = getObjectFromJsonNode(objectJson, objectClass);
		if (object != null) {
			list.add(object);
		}
	}

	/**
	 * This method return the Field of a class or its superclasses that has the name specified by the fieldName parameter
	 * 
	 * @param fieldName
	 *            - the name of the field
	 * @param objectClass
	 *            - the class where the field is searched
	 * @return
	 */
	private static Field getFieldByName(@NotNull String fieldName, @NotNull Class objectClass) {
		Field field = Arrays.stream(objectClass.getDeclaredFields()).filter(fld -> fld.getName().equals(fieldName)).findFirst().orElse(null);
		while (field == null && objectClass.getSuperclass() != null) {
			field = getFieldByName(fieldName, objectClass.getSuperclass());
		}
		return field;
	}

	/**
	 * Returns the parameterized type of the field
	 * 
	 * @param field
	 * @return
	 */
	private static Class getParameterizedType(@NotNull Field field) {
		Type genericFieldType = field.getGenericType();
		Class listElementClass = Object.class;
		if (genericFieldType instanceof ParameterizedType) {
			ParameterizedType aType = (ParameterizedType) genericFieldType;
			Type[] fieldArgTypes = aType.getActualTypeArguments();
			listElementClass = (Class) fieldArgTypes[0];
		}

		return listElementClass;
	}

	/**
	 * Returns the element from the arrayField that have the given uuid
	 * 
	 * @param obj
	 *            - the object that contains the arrayField
	 * @param arrayField
	 *            - the arrayField
	 * @param uuid
	 *            - the given uuid
	 * @return
	 */
	private static Object getListElementByUuid(@NotNull Object obj, @NotNull Field arrayField, @NotNull String uuid) {
		try {
			return ((List) arrayField.get(obj)).stream().filter(listElement -> {
				Field uuidField = getFieldByName(EntityDto.UUID, listElement.getClass());
				if (uuidField == null) {
					throw new RuntimeException("No such field exception!" + EntityDto.UUID);
				}
				uuidField.setAccessible(true);
				try {
					return uuid.equals(uuidField.get(listElement));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}).findFirst().orElse(null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

}
