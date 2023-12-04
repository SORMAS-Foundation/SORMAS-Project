package de.symeda.sormas.backend.customizableenum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.customizableenum.CustomEnumNotFoundException;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
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
			getCustomizableEnumValueService().ensurePersisted(entry);

			entry = new CustomizableEnumValue();
			entry.setDataType(CustomizableEnumType.DISEASE_VARIANT);
			entry.setValue("GENERIC");
			entry.setCaption("Variant 2");
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
	public void tetGetUnknownDiseaseVariantWithNullDisease() throws CustomEnumNotFoundException {
		assertThrows(
			CustomEnumNotFoundException.class,
			() -> getCustomizableEnumFacade().getEnumValue(CustomizableEnumType.DISEASE_VARIANT, "any", null));
	}
}
