/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariantConverter;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;

public class CustomizableEnumConverterTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf;
	private UserReferenceDto reportingUser;
	private CustomizableEnum bf_1_2;
	private CustomizableEnum generic;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCF();
		reportingUser = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_OFFICER).toReference();

		List<CustomizableEnumValue> enumValues = getCustomizableEnumValueService().getAll();
		if (enumValues.isEmpty()) {
			CustomizableEnumValue bf_1_2_value = new CustomizableEnumValue();
			bf_1_2_value.setDataType(CustomizableEnumType.DISEASE_VARIANT);
			bf_1_2_value.setValue("BF.1.2");
			Set<Disease> diseases = new HashSet<>();
			diseases.add(Disease.CORONAVIRUS);
			bf_1_2_value.setDiseases(diseases);
			bf_1_2_value.setCaption("BF.1.2 variant");
			bf_1_2_value.setActive(true);
			getCustomizableEnumValueService().ensurePersisted(bf_1_2_value);

			CustomizableEnumValue generic_value = new CustomizableEnumValue();
			generic_value.setDataType(CustomizableEnumType.DISEASE_VARIANT);
			generic_value.setValue("GENERIC");
			generic_value.setCaption("Variant 2");
			generic_value.setActive(true);
			getCustomizableEnumValueService().ensurePersisted(generic_value);

			getCustomizableEnumFacade().loadData();

			bf_1_2 = getCustomizableEnumFacade().getEnumValue(CustomizableEnumType.DISEASE_VARIANT, Disease.CORONAVIRUS, bf_1_2_value.getValue());
			generic = getCustomizableEnumFacade().getEnumValue(CustomizableEnumType.DISEASE_VARIANT, null, generic_value.getValue());

			Mockito
				.when(
					MockProducer.getCustomizableEnumFacadeForConverter()
						.getEnumValue(CustomizableEnumType.DISEASE_VARIANT, Disease.CORONAVIRUS, bf_1_2_value.getValue()))
				.thenReturn(bf_1_2);
			Mockito
				.when(
					MockProducer.getCustomizableEnumFacadeForConverter().getEnumValue(CustomizableEnumType.DISEASE_VARIANT, null, generic_value.getValue()))
				.thenReturn(generic);
		}
	}

	@Test
	public void testConvertCovidDiseaseVariant() {
		CaseDataDto caze = creator.createCase(reportingUser, creator.createPerson().toReference(), rdcf, c -> {
			c.setDisease(Disease.CORONAVIRUS);
			c.setDiseaseVariant(new DiseaseVariantConverter().convertToEntityAttribute(Disease.CORONAVIRUS, bf_1_2.getValue()));
		});

		assertThat(caze.getDiseaseVariant().getValue(), is(bf_1_2.getValue()));

		CaseDataDto reloadedCase = getCaseFacade().getByUuid(caze.getUuid());
		assertThat(reloadedCase.getDiseaseVariant().getValue(), is(bf_1_2.getValue()));
	}

	@Test
	public void testConvertGenericDiseaseVariant() {
		CaseDataDto caze = creator.createCase(reportingUser, creator.createPerson().toReference(), rdcf, c -> {
			c.setDisease(Disease.CORONAVIRUS);
			c.setDiseaseVariant(new DiseaseVariantConverter().convertToEntityAttribute(Disease.CORONAVIRUS, generic.getValue()));
		});

		assertThat(caze.getDiseaseVariant().getValue(), is(generic.getValue()));

		CaseDataDto reloadedCase = getCaseFacade().getByUuid(caze.getUuid());
		assertThat(reloadedCase.getDiseaseVariant().getValue(), is(generic.getValue()));
	}
}
