package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.I18nPropertiesRequest;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.customizableenum.CustomizableEnumFacade;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.backend.AbstractUnitTest;

class CustomizableEnumPatchMapperTest extends AbstractUnitTest {

	@Mock
	private CustomizableEnumFacade customizableEnumFacade;

	@InjectMocks
	private CustomizableEnumPatchMapper victim;

	@Test
	void getSupportedTypes_containsCustomizableEnumClass() {
		assertEquals(Set.of(CustomizableEnum.class), victim.getSupportedTypes());
	}

	@Test
	void map_nonCustomizableEnumTargetType_throwsIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> victim.map("HEALTHCARE_WORKER", String.class));
	}

	@Test
	void map_unregisteredCustomizableEnumType_throwsIllegalArgumentException() {
		assertThrows(
			IllegalArgumentException.class,
			() -> victim.map(new ValuePatchRequest<UnregisteredEnum>().setValue("VALUE").setTargetType(UnregisteredEnum.class)));
	}

	@Test
	void map_matchByValue_returnsEnumValue() {
		// PREPARE
		OccupationType expected = occupationType("HEALTHCARE_WORKER", "Healthcare Worker");
		when(customizableEnumFacade.getEnumValues(CustomizableEnumType.OCCUPATION_TYPE, null)).thenReturn(List.of(expected));

		// EXECUTE
		OccupationType actual = victim.map("HEALTHCARE_WORKER", OccupationType.class).getData();

		// CHECK
		assertEquals(expected, actual);
	}

	@Test
	void map_matchByCaption_returnsEnumValue() {
		// PREPARE
		OccupationType expected = occupationType("HEALTHCARE_WORKER", "Healthcare Worker");
		when(customizableEnumFacade.getEnumValues(CustomizableEnumType.OCCUPATION_TYPE, null)).thenReturn(List.of(expected));

		// EXECUTE
		OccupationType actual = victim.map("Healthcare Worker", OccupationType.class).getData();

		// CHECK
		assertEquals(expected, actual);
	}

	@Test
	void map_matchByValueCaseInsensitive_returnsEnumValue() {
		// PREPARE
		OccupationType expected = occupationType("HEALTHCARE_WORKER", "Healthcare Worker");
		when(customizableEnumFacade.getEnumValues(CustomizableEnumType.OCCUPATION_TYPE, null)).thenReturn(List.of(expected));

		// EXECUTE
		OccupationType actual = victim.map("healthcare_worker", OccupationType.class).getData();

		// CHECK
		assertEquals(expected, actual);
	}

	@Test
	void map_matchByCaptionWithAccents_returnsEnumValue() {
		// PREPARE - stored caption has no accent, input does
		OccupationType expected = occupationType("INFIRMIER", "Infirmier");
		when(customizableEnumFacade.getEnumValues(CustomizableEnumType.OCCUPATION_TYPE, null)).thenReturn(List.of(expected));

		// EXECUTE
		OccupationType actual = victim.map("Infïrmier", OccupationType.class).getData();

		// CHECK
		assertEquals(expected, actual);
	}

	@Test
	void map_notFoundInDefaultLanguage_foundByInputLanguage_returnsEnumValue() {
		// PREPARE
		OccupationType expected = occupationType("HEALTHCARE_WORKER", "Healthcare Worker");
		when(customizableEnumFacade.getEnumValues(CustomizableEnumType.OCCUPATION_TYPE, null)).thenReturn(List.of());
		when(customizableEnumFacade.getEnumValue(CustomizableEnumType.OCCUPATION_TYPE, null, "HEALTHCARE_WORKER")).thenReturn(expected);

		try (MockedStatic<I18nProperties> mockedI18n = mockStatic(I18nProperties.class)) {
			mockedI18n.when(() -> I18nProperties.buildKeyValueDictionary(any(I18nPropertiesRequest.class)))
				.thenReturn(Map.of("HEALTHCARE_WORKER", "Agent de sante"));

			ValuePatchRequest<OccupationType> request = new ValuePatchRequest<OccupationType>().setValue("Agent de sante")
				.setTargetType(OccupationType.class)
				.setInputLanguages(List.of(Language.FR));

			// EXECUTE
			OccupationType actual = victim.map(request).getData();

			// CHECK
			assertEquals(expected, actual);
		}
	}

	@Test
	void map_notFoundInDefaultLanguage_noInputLanguages_usesUserLanguage() {
		// PREPARE
		OccupationType expected = occupationType("HEALTHCARE_WORKER", "Healthcare Worker");
		when(customizableEnumFacade.getEnumValues(CustomizableEnumType.OCCUPATION_TYPE, null)).thenReturn(List.of());
		when(customizableEnumFacade.getEnumValue(CustomizableEnumType.OCCUPATION_TYPE, null, "HEALTHCARE_WORKER")).thenReturn(expected);

		try (MockedStatic<I18nProperties> mockedI18n = mockStatic(I18nProperties.class)) {
			mockedI18n.when(I18nProperties::getUserLanguage).thenReturn(Language.DE);
			mockedI18n.when(() -> I18nProperties.buildKeyValueDictionary(any(I18nPropertiesRequest.class)))
				.thenReturn(Map.of("HEALTHCARE_WORKER", "Gesundheitsarbeiter"));

			ValuePatchRequest<OccupationType> request =
				new ValuePatchRequest<OccupationType>().setValue("Gesundheitsarbeiter").setTargetType(OccupationType.class);

			// EXECUTE
			OccupationType actual = victim.map(request).getData();

			// CHECK
			assertEquals(expected, actual);
		}
	}

	@Test
	void map_noMatch_fallbackAllowed_otherPresent_returnsOtherEnumValue() {
		// PREPARE
		OccupationType otherEnum = occupationType(CustomizableEnumPatchMapper.FALLBACK_NAME, "Other");
		when(customizableEnumFacade.getEnumValues(CustomizableEnumType.OCCUPATION_TYPE, null)).thenReturn(List.of());
		when(customizableEnumFacade.getEnumValue(CustomizableEnumType.OCCUPATION_TYPE, null, CustomizableEnumPatchMapper.FALLBACK_NAME))
			.thenReturn(otherEnum);

		try (MockedStatic<I18nProperties> mockedI18n = mockStatic(I18nProperties.class)) {
			mockedI18n.when(I18nProperties::getUserLanguage).thenReturn(Language.EN);
			mockedI18n.when(() -> I18nProperties.buildKeyValueDictionary(any(I18nPropertiesRequest.class))).thenReturn(Map.of());

			ValuePatchRequest<OccupationType> request =
				new ValuePatchRequest<OccupationType>().setValue("UNKNOWN_VALUE").setTargetType(OccupationType.class).setAllowFallbackValues(true);

			// EXECUTE
			OccupationType actual = victim.map(request).getData();

			// CHECK
			assertEquals(otherEnum, actual);
		}
	}

	@Test
	void map_noMatch_fallbackAllowed_otherNotPresent_returnsFailureCause() {
		// PREPARE
		when(customizableEnumFacade.getEnumValues(CustomizableEnumType.OCCUPATION_TYPE, null)).thenReturn(List.of());
		when(customizableEnumFacade.getEnumValue(CustomizableEnumType.OCCUPATION_TYPE, null, CustomizableEnumPatchMapper.FALLBACK_NAME))
			.thenReturn(null);

		try (MockedStatic<I18nProperties> mockedI18n = mockStatic(I18nProperties.class)) {
			mockedI18n.when(I18nProperties::getUserLanguage).thenReturn(Language.EN);
			mockedI18n.when(() -> I18nProperties.buildKeyValueDictionary(any(I18nPropertiesRequest.class))).thenReturn(Map.of());

			ValuePatchRequest<OccupationType> request =
				new ValuePatchRequest<OccupationType>().setValue("UNKNOWN_VALUE").setTargetType(OccupationType.class).setAllowFallbackValues(true);

			// EXECUTE & CHECK
			assertEquals(DataPatchFailureCause.NOT_PRESENT_IN_REFERENCE_DATA_LIST, victim.map(request).getDataPatchFailureCause());
		}
	}

	@Test
	void map_noMatch_fallbackDisabled_returnsFailureCause() {
		// PREPARE
		when(customizableEnumFacade.getEnumValues(CustomizableEnumType.OCCUPATION_TYPE, null)).thenReturn(List.of());

		try (MockedStatic<I18nProperties> mockedI18n = mockStatic(I18nProperties.class)) {
			mockedI18n.when(I18nProperties::getUserLanguage).thenReturn(Language.EN);
			mockedI18n.when(() -> I18nProperties.buildKeyValueDictionary(any(I18nPropertiesRequest.class))).thenReturn(Map.of());

			ValuePatchRequest<OccupationType> request =
				new ValuePatchRequest<OccupationType>().setValue("UNKNOWN_VALUE").setTargetType(OccupationType.class).setAllowFallbackValues(false);

			// EXECUTE & CHECK
			assertEquals(DataPatchFailureCause.NOT_PRESENT_IN_REFERENCE_DATA_LIST, victim.map(request).getDataPatchFailureCause());
		}
	}

	private static OccupationType occupationType(String value, String caption) {
		OccupationType occupationType = new OccupationType();
		occupationType.setValue(value);
		occupationType.setCaption(caption);
		return occupationType;
	}

	private static class UnregisteredEnum extends CustomizableEnum {

		@Override
		public void setProperties(Map<String, Object> properties) {
		}

		@Override
		public boolean matchPropertyValue(String property, Object value) {
			return false;
		}

		@Override
		public Map<String, Class<?>> getAllProperties() {
			return Map.of();
		}
	}
}
