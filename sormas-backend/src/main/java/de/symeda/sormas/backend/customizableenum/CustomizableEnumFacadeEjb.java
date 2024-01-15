/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.customizableenum;

import static de.symeda.sormas.backend.util.QueryHelper.getResultList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.customizableenum.CustomEnumNotFoundException;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumCriteria;
import de.symeda.sormas.api.customizableenum.CustomizableEnumFacade;
import de.symeda.sormas.api.customizableenum.CustomizableEnumHelper;
import de.symeda.sormas.api.customizableenum.CustomizableEnumTranslation;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.customizableenum.CustomizableEnumValueDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumValueIndexDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumValueReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.InvalidCustomizationException;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.RightsAllowed;

@Singleton(name = "CustomizableEnumFacade")
public class CustomizableEnumFacadeEjb
	extends
	AbstractBaseEjb<CustomizableEnumValue, CustomizableEnumValueDto, CustomizableEnumValueIndexDto, CustomizableEnumValueReferenceDto, CustomizableEnumValueService, CustomizableEnumCriteria>
	implements CustomizableEnumFacade {

	/**
	 * Maps a customizable enum type to all enum value entities of that type in the database.
	 */
	private final EnumMap<CustomizableEnumType, List<CustomizableEnumValue>> enumValueEntities = new EnumMap<>(CustomizableEnumType.class);
	/**
	 * Maps a customizable enum type to all enum value strings of that type in the database.
	 */
	private final EnumMap<CustomizableEnumType, List<String>> enumValues = new EnumMap<>(CustomizableEnumType.class);
	/**
	 * Maps a customizable enum type to a map with all enum values of this type as its keys and the properties defined for these
	 * enum values as its values.
	 */
	private final EnumMap<CustomizableEnumType, Map<String, Map<String, Object>>> enumProperties = new EnumMap<>(CustomizableEnumType.class);
	/**
	 * Maps a customizable enum type to a map with all enum values of this type as its keys and their active status as its values.
	 */
	private final EnumMap<CustomizableEnumType, Map<String, Boolean>> enumActiveStatuses = new EnumMap<>(CustomizableEnumType.class);
	/**
	 * Maps a customizable enum type (defined by its class) to a map which in turn maps all languages for which translations exist to
	 * the possible enum values of this type, which then finally map to their translated captions.
	 */
	private final Map<Class<? extends CustomizableEnum>, Map<Language, Map<String, String>>> enumValuesByLanguage = new HashMap<>();
	/**
	 * Maps a customizable enum type (defined by its class) to a map which in turn maps all diseases that are relevant for this enum type
	 * to all enum values that are used for the disease.
	 */
	private final Map<Class<? extends CustomizableEnum>, Map<Optional<Disease>, List<String>>> enumValuesByDisease = new HashMap<>();

	@EJB
	private CustomizableEnumValueService service;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	public CustomizableEnumFacadeEjb() {

	}

	@Inject
	public CustomizableEnumFacadeEjb(CustomizableEnumValueService service) {
		super(CustomizableEnumValue.class, CustomizableEnumValueDto.class, service);
	}

	@Lock(LockType.READ)
	@Override
	public List<CustomizableEnumValueDto> getAllAfter(Date date) {
		return service.getAllAfter(date).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	protected CustomizableEnumValue fillOrBuildEntity(CustomizableEnumValueDto source, CustomizableEnumValue target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, CustomizableEnumValue::new, checkChangeDate);

		target.setDataType(source.getDataType());
		target.setValue(source.getValue());
		target.setCaption(source.getCaption());
		target.setTranslations(source.getTranslations());
		target.setDiseases(source.getDiseases());
		target.setDescription(source.getDescription());
		target.setDescriptionTranslations(source.getDescriptionTranslations());
		target.setProperties(source.getProperties());
		target.setDefaultValue(source.isDefaultValue());
		target.setActive(source.isActive());

		return target;
	}

	@Override
	public void validate(CustomizableEnumValueDto dto) throws ValidationRuntimeException {

		if (!CustomizableEnumHelper.isValidEnumValue(dto.getValue())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.customizableEnumValueAllowedCharacters));
		}
	}

	@Lock(LockType.READ)
	@RightsAllowed(UserRight._CUSTOMIZABLE_ENUM_MANAGEMENT)
	@Override
	public CustomizableEnumValueDto save(CustomizableEnumValueDto dto) {

		if (dto == null) {
			return null;
		}

		validate(dto);

		CustomizableEnumValue existingEntity = service.getByUuid(dto.getUuid());

		List<String> dataTypeValues = enumValues.get(dto.getDataType());
		if (existingEntity == null && dataTypeValues != null && dataTypeValues.contains(dto.getValue())) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(Validations.customizableEnumValueDuplicateValue, dto.getDataType().toString(), dto.getValue()));
		}

		CustomizableEnumValue enumValue = fillOrBuildEntity(dto, existingEntity, true);
		service.ensurePersisted(enumValue);

		// Reset cache since values have been changed
		loadData();

		return toDto(enumValue);
	}

	@Lock(LockType.READ)
	@Override
	public long count(CustomizableEnumCriteria criteria) {
		return service.count((cb, root) -> service.buildCriteriaFilter(criteria, cb, root));
	}

	@Lock(LockType.READ)
	@Override
	public List<CustomizableEnumValueDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Lock(LockType.READ)
	@Override
	public List<String> getAllUuids() {
		return service.getAllUuids();
	}

	@Lock(LockType.READ)
	@RightsAllowed(UserRight._CUSTOMIZABLE_ENUM_MANAGEMENT)
	@Override
	public List<CustomizableEnumValueIndexDto> getIndexList(
		CustomizableEnumCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CustomizableEnumValue> cq = cb.createQuery(CustomizableEnumValue.class);
		Root<CustomizableEnumValue> root = cq.from(CustomizableEnumValue.class);

		Predicate filter = null;
		if (criteria != null) {
			filter = service.buildCriteriaFilter(criteria, cb, root);
		}
		if (filter != null) {
			cq.where(filter);
		}

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case CustomizableEnumValue.DATA_TYPE:
				case CustomizableEnumValue.VALUE:
					expression = root.get(sortProperty.propertyName);
					break;
				case CustomizableEnumValue.CAPTION:
					expression = cb.lower(root.get(sortProperty.propertyName));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(root.get(CustomizableEnumValue.DATA_TYPE)), cb.asc(root.get(CustomizableEnumValue.VALUE)));
		}

		cq.select(root);

		return getResultList(em, cq, first, max, this::toIndexDto);
	}

	private CustomizableEnumValueIndexDto toIndexDto(CustomizableEnumValue entity) {

		if (entity == null) {
			return null;
		}

		CustomizableEnumValueIndexDto dto = new CustomizableEnumValueIndexDto();
		DtoHelper.fillDto(dto, entity);

		dto.setDataType(entity.getDataType());
		dto.setValue(entity.getValue());
		dto.setCaption(entity.getCaption());
		dto.setDiseases(entity.getDiseases());
		dto.setProperties(entity.getProperties());

		return dto;
	}

	/**
	 * Using LockType.READ to provide concurrent locks to multiple clients.
	 */
	@Lock(LockType.READ)
	@Override
	@SuppressWarnings("unchecked")
	public <T extends CustomizableEnum> T getEnumValue(CustomizableEnumType type, String value) {
		if (!enumValues.get(type).contains(value)) {
			throw new IllegalArgumentException(String.format("Invalid enum value %s for customizable enum type %s", value, type.toString()));
		}

		Language language = I18nProperties.getUserLanguage();
		Class<T> enumClass = (Class<T>) type.getEnumClass();

		if (!enumValuesByLanguage.get(enumClass).containsKey(language)) {
			fillLanguageCache(type, enumClass, language);
		}

		return buildCustomizableEnum(type, value, language, enumClass);
	}

	@Lock(LockType.READ)
	@Override
	@SuppressWarnings("unchecked")
	public <T extends CustomizableEnum> T getEnumValue(CustomizableEnumType type, String value, Disease disease) throws CustomEnumNotFoundException {
		return (T) getEnumValues(type, disease).stream()
			.filter((e) -> e.getValue().equals(value))
			.findFirst()
			.orElseThrow(
				() -> new CustomEnumNotFoundException(
					"Invalid enum value " + value + " for customizable enum type " + type + " and disease " + disease));
	}

	@Lock(LockType.READ)
	@Override
	public boolean existsEnumValue(CustomizableEnumType type, String value, Disease disease) {
		return getEnumValues(type, disease).stream().anyMatch(e -> e.getValue().equals(value));
	}

	/**
	 * @return Entries are currently not returned in any specific order
	 */
	@Lock(LockType.READ)
	@Override
	public <T extends CustomizableEnum> List<T> getEnumValues(CustomizableEnumType type, Disease disease) {
		return getEnumValues(type, null, disease);
	}

	/**
	 * @return Entries are currently not returned in any specific order
	 */
	@Lock(LockType.READ)
	@Override
	@SuppressWarnings("unchecked")
	public <T extends CustomizableEnum> List<T> getEnumValues(CustomizableEnumType type, String selectedValue, Disease disease) {
		Language language = I18nProperties.getUserLanguage();
		Class<T> enumClass = (Class<T>) type.getEnumClass();
		Optional<Disease> innerDisease = Optional.ofNullable(disease);

		if (!enumValuesByLanguage.get(enumClass).containsKey(language)) {
			fillLanguageCache(type, enumClass, language);
		}

		if (!enumValuesByDisease.get(enumClass).containsKey(innerDisease)) {
			fillDiseaseCache(type, enumClass, innerDisease);
		}

		Stream<String> diseaseValuesStream;
		if (innerDisease.isPresent()) {
			// combine specific and unspecific values
			diseaseValuesStream = Stream.concat(
				enumValuesByDisease.get(enumClass).get(innerDisease).stream(),
				enumValuesByDisease.get(enumClass).get(Optional.empty()).stream());
		} else {
			diseaseValuesStream = enumValuesByDisease.get(enumClass).get(Optional.empty()).stream();
		}

		return diseaseValuesStream.filter(value -> enumActiveStatuses.get(type).get(value) || (selectedValue != null && selectedValue.equals(value)))
			.map(value -> buildCustomizableEnum(type, value, language, enumClass))
			.sorted(Comparator.comparing(CustomizableEnum::getCaption))
			.collect(Collectors.toList());
	}

	@Lock(LockType.READ)
	@Override
	public boolean hasEnumValues(CustomizableEnumType type, Disease disease) {
		return !getEnumValues(type, disease).isEmpty();
	}

	private <T extends CustomizableEnum> T buildCustomizableEnum(CustomizableEnumType type, String value, Language language, Class<T> enumClass) {
		try {
			T enumValue = enumClass.getDeclaredConstructor().newInstance();
			enumValue.setValue(value);
			enumValue.setCaption(enumValuesByLanguage.get(enumClass).get(language).get(value));
			enumValue.setProperties(enumProperties.get(type).get(value));
			return enumValue;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private synchronized <T extends CustomizableEnum> void fillLanguageCache(CustomizableEnumType type, Class<T> enumClass, Language language) {

		// may have already been put by another thread while this one was waiting (synchronized)
		if (enumValuesByLanguage.get(enumClass).containsKey(language)) {
			return;
		}

		boolean isCountryLanguage = StringUtils.equals(configFacade.getCountryLocale(), language.getLocale().toString());

		Map<String, String> languageEnumValues = new HashMap<>();
		for (CustomizableEnumValue customizableEnumValue : enumValueEntities.get(type)) {
			// define caption
			String caption;
			if (customizableEnumValue.isDefaultValue()) {
				// Default values use translations provided in the properties files
				caption = I18nProperties.getEnumCaption(language, customizableEnumValue.getDataType().toString(), customizableEnumValue.getValue());

				if (StringUtils.isBlank(caption)) {
					caption = customizableEnumValue.getCaption();
				}
			} else if (isCountryLanguage || CollectionUtils.isEmpty(customizableEnumValue.getTranslations())) {
				// If the enum value does not have any translations or the user uses the server language,
				// add the server language to the cache and use the default caption of the enum value
				caption = customizableEnumValue.getCaption();
			} else {
				// Check whether the list of translations contains the user language; if yes, add that language
				// to the cache and use its translation; if not, fall back to the default caption of the enum value
				Optional<CustomizableEnumTranslation> translation = customizableEnumValue.getTranslations()
					.stream()
					.filter(t -> t.getLanguageCode().equals(language.getLocale().toString()))
					.findFirst();
				if (translation.isPresent()) {
					caption = translation.get().getValue();
				} else {
					caption = customizableEnumValue.getCaption();
				}
			}
			languageEnumValues.put(customizableEnumValue.getValue(), caption);
		}
		enumValuesByLanguage.get(enumClass).put(language, languageEnumValues); // has to be put into the actual hash map last
	}

	private synchronized <T extends CustomizableEnum> void fillDiseaseCache(
		CustomizableEnumType type,
		Class<T> enumClass,
		Optional<Disease> disease) {

		// may have already been put by another thread while this one was waiting (synchronized)
		if (enumValuesByDisease.get(enumClass).containsKey(disease)) {
			return;
		}

		List<String> diseaseEnumValues = enumValueEntities.get(type)
			.stream()
			.filter(
				e -> disease.map(value -> e.getDiseases() != null && e.getDiseases().contains(value))
					.orElseGet(() -> CollectionUtils.isEmpty(e.getDiseases())))
			.map(CustomizableEnumValue::getValue)
			.collect(Collectors.toList());
		enumValuesByDisease.get(enumClass).put(disease, diseaseEnumValues);
	}

	@PostConstruct
	@Override
	public void loadData() {
		enumValueEntities.clear();
		enumValues.clear();
		enumActiveStatuses.clear();
		enumValuesByLanguage.clear();
		enumValuesByDisease.clear();
		enumProperties.clear();

		for (CustomizableEnumType enumType : CustomizableEnumType.values()) {
			enumValueEntities.putIfAbsent(enumType, new ArrayList<>());
			enumValues.putIfAbsent(enumType, new ArrayList<>());
		}

		// Build list of customizable enums mapped by their enum type; other caches are built on-demand
		for (CustomizableEnumValue customizableEnumValue : service.getAll()) {
			CustomizableEnumType enumType = customizableEnumValue.getDataType();
			enumValueEntities.get(enumType).add(customizableEnumValue);
			String value = customizableEnumValue.getValue();
			if (enumValues.get(enumType).contains(value)) {
				throw new InvalidCustomizationException(
					"Enum value " + value + " for customizable enum type " + enumType.toString() + " is not unique");
			}
			enumValues.get(enumType).add(value);
			enumProperties.putIfAbsent(enumType, new HashMap<>());
			enumProperties.get(enumType).putIfAbsent(value, customizableEnumValue.getProperties());
			enumActiveStatuses.putIfAbsent(enumType, new HashMap<>());
			enumActiveStatuses.get(enumType).putIfAbsent(value, customizableEnumValue.isActive());
		}

		for (CustomizableEnumType enumType : CustomizableEnumType.values()) {

			Class<? extends CustomizableEnum> enumClass = enumType.getEnumClass();
			enumValuesByLanguage.putIfAbsent(enumClass, new ConcurrentHashMap<>()); // access to contains has to be thread-safe
			enumValuesByDisease.putIfAbsent(enumClass, new ConcurrentHashMap<>());

			// Always add values for no disease because they are relevant in all cases
			fillDiseaseCache(enumType, enumClass, Optional.empty());
		}
	}

	public CustomizableEnumValueDto toDto(CustomizableEnumValue source) {

		if (source == null) {
			return null;
		}

		CustomizableEnumValueDto target = new CustomizableEnumValueDto();
		DtoHelper.fillDto(target, source);

		target.setDataType(source.getDataType());
		target.setValue(source.getValue());
		target.setCaption(source.getCaption());
		target.setTranslations(source.getTranslations());
		target.setDiseases(source.getDiseases());
		target.setDescription(source.getDescription());
		target.setDescriptionTranslations(source.getDescriptionTranslations());
		target.setProperties(source.getProperties());
		target.setDefaultValue(source.isDefaultValue());
		target.setActive(source.isActive());

		return target;
	}

	@Override
	protected CustomizableEnumValueReferenceDto toRefDto(CustomizableEnumValue customizableEnumValue) {

		if (customizableEnumValue == null) {
			return null;
		}

		return new CustomizableEnumValueReferenceDto(customizableEnumValue.getUuid());
	}

	@Override
	protected void pseudonymizeDto(CustomizableEnumValue source, CustomizableEnumValueDto dto, Pseudonymizer pseudonymizer, boolean inJurisdiction) {
		// Customizable enum values don't need to be pseudonymized
	}

	@Override
	protected void restorePseudonymizedDto(
		CustomizableEnumValueDto dto,
		CustomizableEnumValueDto existingDto,
		CustomizableEnumValue entity,
		Pseudonymizer pseudonymizer) {
		// Customizable enum values don't need to be pseudonymized
	}

	@LocalBean
	@Stateless
	public static class CustomizableEnumFacadeEjbLocal extends CustomizableEnumFacadeEjb {

		public CustomizableEnumFacadeEjbLocal() {
		}

		@Inject
		public CustomizableEnumFacadeEjbLocal(CustomizableEnumValueService service) {
			super(service);
		}
	}
}
