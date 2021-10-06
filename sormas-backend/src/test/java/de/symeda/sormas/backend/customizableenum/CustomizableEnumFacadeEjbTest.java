package de.symeda.sormas.backend.customizableenum;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.backend.AbstractBeanTest;

public class CustomizableEnumFacadeEjbTest extends AbstractBeanTest {

	@Before
	public void createCustomEnums() {
		List<CustomizableEnumValue> enumValues = getCustomizableEnumValueService().getAll();
		if (enumValues.isEmpty()) {
			CustomizableEnumValue entry = new CustomizableEnumValue();
			entry.setDataType(CustomizableEnumType.DISEASE_VARIANT);
			entry.setValue("BF.1.2");
			entry.setDiseases(Arrays.asList(Disease.CORONAVIRUS));
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
}
