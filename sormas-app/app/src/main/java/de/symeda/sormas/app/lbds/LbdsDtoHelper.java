package de.symeda.sormas.app.lbds;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

import android.util.Log;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

class LbdsDtoHelper {

	private static final List<String> LBDS_PROPERTIES_ENTITY_DTO =
		Arrays.asList(EntityDto.UUID, EntityDto.CHANGE_DATE, EntityDto.CREATION_DATE, PseudonymizableDto.PSEUDONYMIZED);

	private static final List<String> LBDS_PROPERTIES_PERSON_DTO = Arrays
		.asList(PersonDto.FIRST_NAME, PersonDto.LAST_NAME, PersonDto.SEX, PersonDto.COVID_CODE_DELIVERED, PersonDto.HAS_COVID_APP, PersonDto.PHONE);

	private static final List<String> LBDS_PROPERTIES_CASE_DATA_DTO = Arrays.asList(
		CaseDataDto.PERSON,
		CaseDataDto.DISEASE,
		CaseDataDto.REPORT_DATE,
		CaseDataDto.REPORTING_USER,
		CaseDataDto.CASE_CLASSIFICATION,
		CaseDataDto.INVESTIGATION_STATUS,
		CaseDataDto.REGION,
		CaseDataDto.DISTRICT,
		CaseDataDto.FACILITY_TYPE,
		CaseDataDto.HEALTH_FACILITY);

	private static Map<String, List<String>> LBDS_DTO_PROPERTIES = new HashMap<>();

	static {
		LBDS_DTO_PROPERTIES.put(PersonDto.class.getSimpleName(), LBDS_PROPERTIES_PERSON_DTO);
		LBDS_DTO_PROPERTIES.put(CaseDataDto.class.getSimpleName(), LBDS_PROPERTIES_CASE_DATA_DTO);
	}

	public static void stripLbdsDto(EntityDto entityDto) throws IntrospectionException, InvocationTargetException, IllegalAccessException {

		Log.i("SORMAS_LBDS", "==========================");
		String entityDtoClass = entityDto.getClass().getSimpleName();
		List<String> lbdsProperties = LBDS_DTO_PROPERTIES.get(entityDtoClass);
		if (lbdsProperties != null) {
			Log.i("SORMAS_LBDS", "Properties:");
			for (PropertyDescriptor property : Introspector.getBeanInfo(entityDto.getClass()).getPropertyDescriptors()) {
				String propertyName = property.getName();
				boolean isLbdsProperty = LBDS_PROPERTIES_ENTITY_DTO.contains(propertyName) || lbdsProperties.contains(propertyName);
				String marker = isLbdsProperty ? "* " : "- ";
				Log.i("SORMAS_LBDS", marker + propertyName + ": " + property.getPropertyType().getName());
				if (!isLbdsProperty) {
					Method writeMethod = property.getWriteMethod();
					if (writeMethod != null && writeMethod.getGenericParameterTypes().length == 1)
						writeMethod.invoke(
							entityDto,
							new Object[] {
								null });
				}
			}
		} else {
			Log.i("SORMAS_LBDS", entityDtoClass + " is not intended for LBDS sending");
		}
		Log.i("SORMAS_LBDS", "==========================");
	}
}
