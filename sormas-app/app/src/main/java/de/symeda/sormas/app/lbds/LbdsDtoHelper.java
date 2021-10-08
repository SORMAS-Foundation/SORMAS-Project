package de.symeda.sormas.app.lbds;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;

public class LbdsDtoHelper {

	private static final List<String> LBDS_PROPERTIES_ENTITY_DTO = Arrays.asList(EntityDto.UUID, EntityDto.CHANGE_DATE, EntityDto.CREATION_DATE);

	private static final List<String> LBDS_PROPERTIES_PERSON_DTO = Arrays.asList(
		PersonDto.FIRST_NAME,
		PersonDto.LAST_NAME,
		PersonDto.SEX,
		// Do not set phone to null
		PersonDto.PHONE);

	private static final List<String> LBDS_PROPERTIES_CASE_DATA_DTO = Arrays.asList(
		CaseDataDto.PERSON,
		CaseDataDto.DISEASE,
		CaseDataDto.REPORT_DATE,
		CaseDataDto.REPORTING_USER,
		CaseDataDto.CASE_CLASSIFICATION,
		CaseDataDto.INVESTIGATION_STATUS,
		CaseDataDto.RESPONSIBLE_REGION,
		CaseDataDto.RESPONSIBLE_DISTRICT,
		CaseDataDto.FACILITY_TYPE,
		CaseDataDto.HEALTH_FACILITY);

	private static Map<String, List<String>> LBDS_DTO_PROPERTIES = new HashMap<>();

	static {
		LBDS_DTO_PROPERTIES.put(PersonDto.class.getSimpleName(), LBDS_PROPERTIES_PERSON_DTO);
		LBDS_DTO_PROPERTIES.put(CaseDataDto.class.getSimpleName(), LBDS_PROPERTIES_CASE_DATA_DTO);
	}

	public static void stripLbdsDto(EntityDto entityDto) throws IntrospectionException, InvocationTargetException, IllegalAccessException {

		String entityDtoClass = entityDto.getClass().getSimpleName();
		List<String> lbdsProperties = LBDS_DTO_PROPERTIES.get(entityDtoClass);
		if (lbdsProperties != null) {
			for (PropertyDescriptor property : Introspector.getBeanInfo(entityDto.getClass()).getPropertyDescriptors()) {
				String propertyName = property.getName();
				boolean isLbdsProperty = property.getPropertyType().isPrimitive()
					|| LBDS_PROPERTIES_ENTITY_DTO.contains(propertyName)
					|| lbdsProperties.contains(propertyName);
				if (!isLbdsProperty) {
					Method writeMethod = property.getWriteMethod();
					if (writeMethod != null && writeMethod.getGenericParameterTypes().length == 1) {
						Object value = List.class.isAssignableFrom(property.getPropertyType()) ? Collections.emptyList() : null;
						writeMethod.invoke(
							entityDto,
							new Object[] {
								value });
					}
				}
			}
		} else {
			throw new IllegalArgumentException(entityDtoClass + " is not intended for LBDS sending");
		}
	}
}
