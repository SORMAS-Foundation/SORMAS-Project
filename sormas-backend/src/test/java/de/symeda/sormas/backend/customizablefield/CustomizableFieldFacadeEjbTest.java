/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.backend.customizablefield;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataFacade;
import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueFacade;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

class CustomizableFieldFacadeEjbTest extends AbstractBeanTest {

    private RDCF rdcf;
    private UserDto surveillanceSupervisor;

    @Override
    public void init() {
        super.init();
        rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
        surveillanceSupervisor = creator.createSurveillanceSupervisor(rdcf);
    }

    @ParameterizedTest
    @EnumSource(CustomizableFieldContext.class)
    void testRenderCustomizableFields(CustomizableFieldContext context) {
        CustomizableFieldMetadataFacade metadataFacade = getBean(CustomizableFieldMetadataFacadeEjb.CustomizableFieldMetadataFacadeEjbLocal.class);
        CustomizableFieldValueFacade valueFacade = getBean(CustomizableFieldValueFacadeEjb.CustomizableFieldValueFacadeEjbLocal.class);

        CustomizableFieldMetadataDto metadata = new CustomizableFieldMetadataDto();
        metadata.setName("customField_" + context.name().toLowerCase());
        metadata.setFieldType(CustomizableFieldType.TEXT);
        metadata.setContextClass(context);
        metadata.setUiGroup(context.name().toLowerCase());
        metadata.setUiLinePosition(1);

        CustomizableFieldMetadataDto savedMetadata = metadataFacade.save(metadata);
        assertThat(savedMetadata.getUuid(), is(notNullValue()));

        PersonDto person = creator.createPerson("Case", "Person", Sex.MALE, 1980, 1, 1);
        CaseDataDto caze = creator.createCase(
            surveillanceSupervisor.toReference(),
            person.toReference(),
            Disease.EVD,
            CaseClassification.PROBABLE,
            InvestigationStatus.PENDING,
            new java.util.Date(),
            rdcf);

        String entityUuid = getEntityUuidForContext(context, caze);
        CustomizableFieldValueDto valueDto = new CustomizableFieldValueDto();
        valueDto.setValue("custom-value");
        Map<String, CustomizableFieldValueDto> fieldValues = Map.of(savedMetadata.getUuid(), valueDto);
        valueFacade.saveEntityCustomFields(entityUuid, context, fieldValues);

        List<CustomizableFieldMetadataDto> activeFields = metadataFacade.getActiveFieldsForContext(context);
        Map<String, CustomizableFieldValueDto> values = valueFacade.getValuesForEntity(entityUuid, context);

        assertThat(activeFields, hasSize(1));
        assertThat(activeFields.get(0).getUuid(), is(savedMetadata.getUuid()));
        assertThat(activeFields.get(0).getName(), is(equalTo("customField_" + context.name().toLowerCase())));

        assertThat(values, hasKey(savedMetadata.getUuid()));
        assertThat(values.get(savedMetadata.getUuid()).getValue(), is(equalTo("custom-value")));
    }

    private String getEntityUuidForContext(CustomizableFieldContext context, CaseDataDto caze) {
        switch (context) {
        case CASE:
            return caze.getUuid();
        case EPIDATA:
            return caze.getEpiData().getUuid();
        default:
            throw new IllegalArgumentException("Unhandled context: " + context);
        }
    }
}
