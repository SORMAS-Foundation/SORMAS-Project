package de.symeda.sormas.app.lbds;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;

public class LbdsDtoHelper {

	private static final List<String> PROPERTIES_TO_SKIP = Arrays.asList(
		"class",
		"enrolledInExternalJournal",
		PersonDto.PHONE,
		"creationVersion",
		// TODO: check if embedded Dtos are empty:
		CaseDataDto.HOSPITALIZATION,
		CaseDataDto.EPI_DATA,
		CaseDataDto.SYMPTOMS,
		CaseDataDto.THERAPY,
		CaseDataDto.CLINICAL_COURSE,
		CaseDataDto.MATERNAL_HISTORY,
		CaseDataDto.PORT_HEALTH_INFO);

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

	private static final Map<String, Object> CASE_DEFAULT_VALUES = new HashMap<>();

	private static Map<String, List<String>> LBDS_DTO_PROPERTIES = new HashMap<>();

	static {
		LBDS_DTO_PROPERTIES.put(PersonDto.class.getSimpleName(), LBDS_PROPERTIES_PERSON_DTO);
		LBDS_DTO_PROPERTIES.put(CaseDataDto.class.getSimpleName(), LBDS_PROPERTIES_CASE_DATA_DTO);

		CASE_DEFAULT_VALUES.put(CaseDataDto.INVESTIGATION_STATUS, InvestigationStatus.PENDING);
		CASE_DEFAULT_VALUES.put(CaseDataDto.CASE_CLASSIFICATION, CaseClassification.NOT_CLASSIFIED);
		CASE_DEFAULT_VALUES.put(CaseDataDto.CASE_CLASSIFICATION, InvestigationStatus.PENDING);
		CASE_DEFAULT_VALUES.put(CaseDataDto.OUTCOME, CaseOutcome.NO_OUTCOME);
		CASE_DEFAULT_VALUES.put(CaseDataDto.CASE_ORIGIN, CaseOrigin.IN_COUNTRY);
		CASE_DEFAULT_VALUES.put(CaseDataDto.FOLLOW_UP_STATUS, FollowUpStatus.NO_FOLLOW_UP);
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

	public static boolean isModifiedLbds(Person person, PersonDto personDto, boolean checkNonLbdsProperties)
		throws IntrospectionException, InvocationTargetException, IllegalAccessException {
		return isModifiedLbds(person, personDto, new PersonDtoHelper(), checkNonLbdsProperties);
	}

	public static boolean isModifiedLbds(Case caze, CaseDataDto caseDataDto, boolean checkNonLbdsProperties)
		throws IntrospectionException, InvocationTargetException, IllegalAccessException {
		return isModifiedLbds(caze, caseDataDto, new CaseDtoHelper(), checkNonLbdsProperties);
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

						if ((propertyValue == null || localPropertyValue == null)) {
							if (!(propertyValue == null && localPropertyValue == null)) {
								return true;
							}
						} else if (ReferenceDto.class.isAssignableFrom(propertyValue.getClass())) {
							String uuid = ((ReferenceDto) propertyValue).getUuid();
							String localUuid = ((ReferenceDto) localPropertyValue).getUuid();
							if (!uuid.equals(localUuid)) {
								return true;
							}
						} else if (!propertyValue.equals(localPropertyValue)) {
							return true;
						}
					} else {
						if (checkNonLbdsProperties
							&& !isNullOrEmptyLbds(localPropertyValue)
							&& !(CaseDataDto.class.isAssignableFrom(entityDtoClass) && isCaseDefaultValue(propertyName, localPropertyValue))) {
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

	private static boolean isNullOrEmptyLbds(Object propertyValue) {
		if (propertyValue == null || propertyValue.toString() == null) {
			return true;
		} else if (Collection.class.isAssignableFrom(propertyValue.getClass())) {
			return ((Collection<?>) propertyValue).isEmpty();
		} else if (propertyValue.toString().isEmpty()) {
			return true;
		}
		return false;
	}

	private static boolean isCaseDefaultValue(String propertyName, Object propertyValue) {
		Object defaultValue = CASE_DEFAULT_VALUES.get(propertyName);
		return defaultValue.equals(propertyValue);
	}
}
