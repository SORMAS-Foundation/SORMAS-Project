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
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldGroup;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataFacade;
import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueFacade;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
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
    @EnumSource(value = CustomizableFieldContext.class,
        names = {
            "CASE",
            "EPIDATA",
            "EXPOSURE" })
    void testCaseRelatedCustomizableFields(CustomizableFieldContext context) {
        CustomizableFieldMetadataFacade metadataFacade = getBean(CustomizableFieldMetadataFacadeEjb.CustomizableFieldMetadataFacadeEjbLocal.class);
        CustomizableFieldValueFacade valueFacade = getBean(CustomizableFieldValueFacadeEjb.CustomizableFieldValueFacadeEjbLocal.class);

        CustomizableFieldMetadataDto metadata = buildMetadata(context, "customField_" + context.name().toLowerCase());

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

        caze.getEpiData().getExposures().add(ExposureDto.build(ExposureType.TRAVEL));
        caze = getCaseFacade().save(caze);

        String entityUuid = getEntityUuidForContext(context, caze);

        // in case other contexts are added and no corresponding case data is created this should fail
        // in case of failure update the creation of the case and add the aditional necessary data
        assertThat("Unexpected entity UUID null for context " + context, entityUuid, notNullValue());

        CustomizableFieldValueDto valueDto = new CustomizableFieldValueDto();
        valueDto.setValue("custom-value");
        Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> fieldValues = Map.of(savedMetadata, valueDto);
        valueFacade.saveEntityCustomFields(entityUuid, context, fieldValues);

        List<CustomizableFieldMetadataDto> activeFields = metadataFacade.getActiveFieldsForContext(context);
        Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values = valueFacade.getValuesForEntity(entityUuid, context);

        assertThat(activeFields, hasSize(1));
        assertThat(activeFields.get(0).getUuid(), is(savedMetadata.getUuid()));
        assertThat(activeFields.get(0).getName(), is(equalTo("customField_" + context.name().toLowerCase())));

        assertThat(values, hasKey(savedMetadata));
        assertThat(values.get(savedMetadata).getValue(), is(equalTo("custom-value")));
    }

    @ParameterizedTest
    @EnumSource(value = CustomizableFieldContext.class,
        names = {
            "CASE",
            "EPIDATA",
            "EXPOSURE" })
    void testCaseRelatedDefaultValuesArePersistedOnCreate(CustomizableFieldContext context) {
        CustomizableFieldMetadataFacade metadataFacade = getBean(CustomizableFieldMetadataFacadeEjb.CustomizableFieldMetadataFacadeEjbLocal.class);
        CustomizableFieldValueFacade valueFacade = getBean(CustomizableFieldValueFacadeEjb.CustomizableFieldValueFacadeEjbLocal.class);

        CustomizableFieldMetadataDto metadata = buildMetadata(context, "defaultField_" + context.name().toLowerCase());
        metadata.setDefaultValue("default-" + context.name().toLowerCase());
        CustomizableFieldMetadataDto savedMetadata = metadataFacade.save(metadata);

        PersonDto person = creator.createPerson("Default", "Case", Sex.MALE, 1981, 2, 2);
        CaseDataDto caze = creator.createCase(
            surveillanceSupervisor.toReference(),
            person.toReference(),
            Disease.EVD,
            CaseClassification.PROBABLE,
            InvestigationStatus.PENDING,
            new java.util.Date(),
            rdcf);

        if (context == CustomizableFieldContext.EXPOSURE) {
            caze.getEpiData().getExposures().add(ExposureDto.build(ExposureType.TRAVEL));
            caze = getCaseFacade().save(caze);
        }

        Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values =
            valueFacade.getValuesForEntity(getEntityUuidForContext(context, caze), context);

        assertThat(values, hasKey(savedMetadata));
        assertThat(values.get(savedMetadata).getValue(), is(equalTo(savedMetadata.getDefaultValue())));
    }

    @ParameterizedTest
    @EnumSource(value = CustomizableFieldContext.class,
        names = {
            "EPIDATA",
            "EXPOSURE" })
    void testContactRelatedDefaultValuesArePersistedOnCreate(CustomizableFieldContext context) {
        CustomizableFieldMetadataFacade metadataFacade = getBean(CustomizableFieldMetadataFacadeEjb.CustomizableFieldMetadataFacadeEjbLocal.class);
        CustomizableFieldValueFacade valueFacade = getBean(CustomizableFieldValueFacadeEjb.CustomizableFieldValueFacadeEjbLocal.class);

        CustomizableFieldMetadataDto metadata = buildMetadata(context, "contactDefaultField_" + context.name().toLowerCase());
        metadata.setDefaultValue("contact-default-" + context.name().toLowerCase());
        CustomizableFieldMetadataDto savedMetadata = metadataFacade.save(metadata);

        PersonDto person = creator.createPerson("Default", "Contact", Sex.FEMALE, 1982, 3, 3);
        ContactDto contact = creator.createContact(surveillanceSupervisor.toReference(), person.toReference(), Disease.EVD, dto -> {
            if (context == CustomizableFieldContext.EXPOSURE) {
                dto.getEpiData().getExposures().add(ExposureDto.build(ExposureType.TRAVEL));
            }
        });

        Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> values =
            valueFacade.getValuesForEntity(getEntityUuidForContext(context, contact), context);

        assertThat(values, hasKey(savedMetadata));
        assertThat(values.get(savedMetadata).getValue(), is(equalTo(savedMetadata.getDefaultValue())));
    }

    private CustomizableFieldMetadataDto buildMetadata(CustomizableFieldContext context, String name) {
        CustomizableFieldMetadataDto metadata = new CustomizableFieldMetadataDto();
        metadata.setName(name);
        metadata.setFieldType(CustomizableFieldType.TEXT);
        metadata.setContextClass(context);
        metadata.setUiGroup(CustomizableFieldGroup.getGroupsForContext(context).stream().findFirst().orElse(null));
        metadata.setUiLinePosition(1);
        return metadata;
    }

    private String getEntityUuidForContext(CustomizableFieldContext context, CaseDataDto caze) {
        switch (context) {
        case CASE:
            return caze.getUuid();
        case EPIDATA:
            return caze.getEpiData().getUuid();
        case EXPOSURE:
            return caze.getEpiData().getExposures() != null && !caze.getEpiData().getExposures().isEmpty()
                ? caze.getEpiData().getExposures().get(0).getUuid()
                : null;
        default:
            throw new IllegalArgumentException("Unhandled context: " + context);
        }
    }

    private String getEntityUuidForContext(CustomizableFieldContext context, ContactDto contact) {
        switch (context) {
        case EPIDATA:
            return contact.getEpiData().getUuid();
        case EXPOSURE:
            return contact.getEpiData().getExposures() != null && !contact.getEpiData().getExposures().isEmpty()
                ? contact.getEpiData().getExposures().get(0).getUuid()
                : null;
        default:
            throw new IllegalArgumentException("Unhandled context: " + context);
        }
    }
}
