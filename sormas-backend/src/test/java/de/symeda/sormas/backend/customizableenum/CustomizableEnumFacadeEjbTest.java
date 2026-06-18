package de.symeda.sormas.backend.customizableenum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.customizableenum.CustomEnumNotFoundException;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumHelper;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.backend.AbstractBeanTest;

public class CustomizableEnumFacadeEjbTest extends AbstractBeanTest {

	@BeforeEach
	public void createCustomEnums() {
		List<CustomizableEnumValue> enumValues = getCustomizableEnumValueService().getAll();
		if (enumValues.isEmpty()) {
			CustomizableEnumValue entry = new CustomizableEnumValue();
			entry.setDataType(CustomizableEnumType.DISEASE_VARIANT);
			entry.setValue("BF.1.2");
			Set<Disease> diseases = new HashSet<>();
			diseases.add(Disease.CORONAVIRUS);
			entry.setDiseases(diseases);
			entry.setCaption("BF.1.2 variant");
			entry.setActive(true);
			getCustomizableEnumValueService().ensurePersisted(entry);

			entry = new CustomizableEnumValue();
			entry.setDataType(CustomizableEnumType.DISEASE_VARIANT);
			entry.setValue("GENERIC");
			entry.setCaption("Variant 2");
			entry.setActive(true);
			getCustomizableEnumValueService().ensurePersisted(entry);

			getCustomizableEnumFacade().loadData();
		}

	}

	@Test
	public void getEnumValues() {

		List<CustomizableEnum> enumValues = getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, Disease.CORONAVIRUS);
		assertEquals(2, enumValues.size());

		enumValues = getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, null);
		assertEquals(1, enumValues.size());

	}

	@Test
	public void getEnumValuesDoesNotReturnDuplicatesWhenValueIsBothDiseaseSpecificAndUnspecific() {

		// Same value defined for a specific disease and for no disease (unspecific)
		String sharedValue = "TYPE_A_B";

		CustomizableEnumValue specific = new CustomizableEnumValue();
		specific.setDataType(CustomizableEnumType.DISEASE_VARIANT);
		specific.setValue(sharedValue);
		Set<Disease> diseases = new HashSet<>();
		diseases.add(Disease.NEW_INFLUENZA);
		specific.setDiseases(diseases);
		specific.setCaption("Type A+B");
		specific.setActive(true);
		getCustomizableEnumValueService().ensurePersisted(specific);

		CustomizableEnumValue unspecific = new CustomizableEnumValue();
		unspecific.setDataType(CustomizableEnumType.DISEASE_VARIANT);
		unspecific.setValue(sharedValue);
		unspecific.setCaption("Type A+B");
		unspecific.setActive(true);
		getCustomizableEnumValueService().ensurePersisted(unspecific);

		getCustomizableEnumFacade().loadData();

		List<CustomizableEnum> enumValues = getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, Disease.NEW_INFLUENZA);
		long occurrences = enumValues.stream().filter(e -> sharedValue.equals(e.getValue())).count();
		assertEquals(1, occurrences);
	}

	@Test
	public void tetGetUnknownDiseaseVariantWithNullDisease() throws CustomEnumNotFoundException {
		assertThrows(
			CustomEnumNotFoundException.class,
			() -> getCustomizableEnumFacade().getEnumValue(CustomizableEnumType.DISEASE_VARIANT, "any", null));
	}

	@Test
	public void testEnumValueValidation() {
		assertTrue(CustomizableEnumHelper.isValidEnumValue("VALIDSIMPLEVALUE"));
		assertTrue(CustomizableEnumHelper.isValidEnumValue("VALID_SIMPLE_VALUE"));
		assertTrue(CustomizableEnumHelper.isValidEnumValue("VALID.SIMPLE.VALUE"));
		assertTrue(CustomizableEnumHelper.isValidEnumValue("VALID+SIMPLE+VALUE"));
		assertTrue(CustomizableEnumHelper.isValidEnumValue("VALID_SIM+PLE.VALUE"));
		assertTrue(CustomizableEnumHelper.isValidEnumValue("VALID_SIMPLE.VALUE+1"));

		assertFalse(CustomizableEnumHelper.isValidEnumValue("invalidvalue"));
		assertFalse(CustomizableEnumHelper.isValidEnumValue("INVALID-VALUE"));
		assertFalse(CustomizableEnumHelper.isValidEnumValue("INVALID$VALUE"));

	}
}
