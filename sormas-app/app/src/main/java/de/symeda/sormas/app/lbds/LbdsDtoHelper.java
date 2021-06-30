package de.symeda.sormas.app.lbds;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

import android.util.Log;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;

public class LbdsDtoHelper {

	private static final List<String> PROPERTIES_TO_SKIP = Arrays.asList("class", "enrolledInExternalJournal", PersonDto.PHONE);

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

	public static boolean isModifiedLbds(Person person, PersonDto personDto, boolean checkNonLbdsProperties)
		throws IntrospectionException, InvocationTargetException, IllegalAccessException {
		return isModifiedLbds(person, personDto, new PersonDtoHelper(), checkNonLbdsProperties);
	}

	private static <ADO extends AbstractDomainObject, DTO extends EntityDto> boolean isModifiedLbds(
		ADO ado,
		DTO dto,
		AdoDtoHelper<ADO, DTO> adoDtoHelper,
		boolean checkNonLbdsProperties)
		throws IntrospectionException, IllegalAccessException, InvocationTargetException {

		if (ado == null || dto == null) {
			return ado != null || dto != null;
		}

		DTO localPersonDto = adoDtoHelper.adoToDto(ado);
		Class<?> entityDtoClass = dto.getClass();
		List<String> lbdsProperties = LBDS_DTO_PROPERTIES.get(entityDtoClass.getSimpleName());
		if (lbdsProperties != null) {
			for (PropertyDescriptor property : Introspector.getBeanInfo(entityDtoClass).getPropertyDescriptors()) {
				String propertyName = property.getName();
				Method readMethod = property.getReadMethod();
				Object localPropertyValue = readMethod.invoke(localPersonDto);
				if (readMethod != null && readMethod.getGenericParameterTypes().length == 0 && !PROPERTIES_TO_SKIP.contains(propertyName)) {
					boolean isLbdsProperty = LBDS_PROPERTIES_ENTITY_DTO.contains(propertyName) || lbdsProperties.contains(propertyName);
					if (isLbdsProperty) {
						Object propertyValue = readMethod.invoke(dto);

						Log.i("SORMAS_LBDS", "inspecting " + propertyName + ": local " + localPropertyValue + " / " + propertyValue);

						if ((propertyValue == null || localPropertyValue == null)) {
							if (!(propertyValue == null && localPropertyValue == null)) {
								Log.i("SORMAS_LBDS", "1. modified " + propertyName + ": local " + localPropertyValue + " / " + propertyValue);
								return true;
							}
						} else if (ReferenceDto.class.isAssignableFrom(propertyValue.getClass())) {
							String uuid = ((ReferenceDto) propertyValue).getUuid();
							String localUuid = ((ReferenceDto) localPropertyValue).getUuid();
							if (!uuid.equals(localUuid)) {
								Log.i("SORMAS_LBDS", "2. modified " + propertyName + ": local " + localPropertyValue + " / " + propertyValue);
								return true;
							}
						} else if (!propertyValue.equals(localPropertyValue)) {
							Log.i("SORMAS_LBDS", "3. modified " + propertyName + ": local " + localPropertyValue + " / " + propertyValue);
							return true;
						}
					} else {
						Log.i("SORMAS_LBDS", "inspecting non-LBDS:" + propertyName + ": local " + localPropertyValue);
						if (checkNonLbdsProperties && !isNullOrEmptyLbds(localPropertyValue)) {
							return true;
						}
					}
				}
			}
		} else {
			throw new IllegalArgumentException(entityDtoClass + " is not intended for LBDS use");
		}

		return false;
	}

	private static boolean isNullOrEmptyLbds(Object property) {
		if (property == null) {
			return true;
		} else if (Collection.class.isAssignableFrom(property.getClass())) {
			return ((Collection<?>) property).isEmpty();
		} else if (property.toString().isEmpty()) {
			return true;
		}
		return false;
	}
}
