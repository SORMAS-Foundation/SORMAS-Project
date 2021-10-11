/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.sormastosormas.share;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * @author Alex Vidrean
 * @since 11-Oct-21
 */

@RunWith(MockitoJUnitRunner.class)
public class ShareDataBuilderHelperTest extends AbstractBeanTest {

    @Test
    public void testClearIgnoredPropertiesForCase() {

        TestDataCreator.RDCF rdcf = creator.createRDCF();

        PersonDto personDto = creator.createPerson();
        UserReferenceDto officer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
        CaseDataDto caseDataDto = creator.createCase(officer, rdcf, dto -> {
            dto.setPerson(personDto.toReference());
            dto.setSurveillanceOfficer(officer);
            dto.setClassificationUser(officer);
            dto.setAdditionalDetails("additionalDetails");
            dto.setExternalID("externalId");
            dto.setExternalToken("externalToken");
            dto.setInternalToken("internalToken");
        });

        getShareDataBuilderHelper().clearIgnoredProperties(caseDataDto);

        assertThat(caseDataDto.getAdditionalDetails(), is(nullValue()));
        assertThat(caseDataDto.getExternalID(), is(nullValue()));
        assertThat(caseDataDto.getExternalToken(), is(nullValue()));
        assertThat(caseDataDto.getInternalToken(), is(nullValue()));
        assertThat(caseDataDto.getPerson(), not(nullValue()));
    }

    @Test
    public void testDoNotClearIgnoredPropertiesForCase() {

        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS, Boolean.FALSE.toString());
        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID, Boolean.FALSE.toString());
        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN, Boolean.FALSE.toString());
        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN, Boolean.FALSE.toString());

        TestDataCreator.RDCF rdcf = creator.createRDCF();

        PersonDto personDto = creator.createPerson();
        UserReferenceDto officerReferenceDto = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
        CaseDataDto caseDataDto = creator.createCase(officerReferenceDto, rdcf, dto -> {
            dto.setPerson(personDto.toReference());
            dto.setSurveillanceOfficer(officerReferenceDto);
            dto.setClassificationUser(officerReferenceDto);
            dto.setAdditionalDetails("additionalDetails");
            dto.setExternalID("externalId");
            dto.setExternalToken("externalToken");
            dto.setInternalToken("internalToken");
        });

        getShareDataBuilderHelper().clearIgnoredProperties(caseDataDto);

        assertThat(caseDataDto.getPerson(), not(nullValue()));
        assertThat(caseDataDto.getAdditionalDetails(), is("additionalDetails"));
        assertThat(caseDataDto.getExternalID(), is("externalId"));
        assertThat(caseDataDto.getExternalToken(), is("externalToken"));
        assertThat(caseDataDto.getInternalToken(), is("internalToken"));
    }

    @Test
    public void testClearIgnoredPropertiesForContact() {

        TestDataCreator.RDCF rdcf = creator.createRDCF();

        PersonReferenceDto personReferenceDto = creator.createPerson().toReference();
        UserReferenceDto officerReferenceDto = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
        ContactDto contactDto = creator.createContact(officerReferenceDto, personReferenceDto);
        contactDto.setAdditionalDetails("additionalDetails");
        contactDto.setExternalID("externalId");
        contactDto.setExternalToken("externalToken");
        contactDto.setInternalToken("internalToken");

        getShareDataBuilderHelper().clearIgnoredProperties(contactDto);

        assertThat(contactDto.getPerson(), not(nullValue()));
        assertThat(contactDto.getAdditionalDetails(), is(nullValue()));
        assertThat(contactDto.getExternalID(), is(nullValue()));
        assertThat(contactDto.getExternalToken(), is(nullValue()));
        assertThat(contactDto.getInternalToken(), is(nullValue()));
    }

    @Test
    public void testDoNotClearIgnoredPropertiesForContact() {

        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS, Boolean.FALSE.toString());
        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID, Boolean.FALSE.toString());
        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN, Boolean.FALSE.toString());
        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN, Boolean.FALSE.toString());

        TestDataCreator.RDCF rdcf = creator.createRDCF();

        PersonReferenceDto personReferenceDto = creator.createPerson().toReference();
        UserReferenceDto officerReferenceDto = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
        ContactDto contactDto = creator.createContact(officerReferenceDto, personReferenceDto);
        contactDto.setAdditionalDetails("additionalDetails");
        contactDto.setExternalID("externalId");
        contactDto.setExternalToken("externalToken");
        contactDto.setInternalToken("internalToken");

        getShareDataBuilderHelper().clearIgnoredProperties(contactDto);

        assertThat(contactDto.getPerson(), not(nullValue()));
        assertThat(contactDto.getAdditionalDetails(), is("additionalDetails"));
        assertThat(contactDto.getExternalID(), is("externalId"));
        assertThat(contactDto.getExternalToken(), is("externalToken"));
        assertThat(contactDto.getInternalToken(), is("internalToken"));
    }

    @Test
    public void testClearIgnoredPropertiesForEvent() {

        TestDataCreator.RDCF rdcf = creator.createRDCF();

        UserReferenceDto officerReferenceDto = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
        EventDto eventDto = creator.createEvent(officerReferenceDto);
        eventDto.setDisease(Disease.CORONAVIRUS);
        eventDto.setExternalId("externalId");
        eventDto.setExternalToken("externalToken");
        eventDto.setInternalToken("internalToken");

        getShareDataBuilderHelper().clearIgnoredProperties(eventDto);

        assertThat(eventDto.getDisease(), not(nullValue()));
        assertThat(eventDto.getExternalId(), is(nullValue()));
        assertThat(eventDto.getExternalToken(), is(nullValue()));
        assertThat(eventDto.getInternalToken(), is(nullValue()));
    }

    @Test
    public void testDoNotClearIgnoredPropertiesForEvent() {

        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS, Boolean.FALSE.toString());
        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID, Boolean.FALSE.toString());
        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN, Boolean.FALSE.toString());
        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN, Boolean.FALSE.toString());

        TestDataCreator.RDCF rdcf = creator.createRDCF();

        UserReferenceDto officerReferenceDto = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
        EventDto eventDto = creator.createEvent(officerReferenceDto);
        eventDto.setDisease(Disease.CORONAVIRUS);
        eventDto.setExternalId("externalId");
        eventDto.setExternalToken("externalToken");
        eventDto.setInternalToken("internalToken");

        getShareDataBuilderHelper().clearIgnoredProperties(eventDto);

        assertThat(eventDto.getDisease(), not(nullValue()));
        assertThat(eventDto.getExternalId(), is("externalId"));
        assertThat(eventDto.getExternalToken(), is("externalToken"));
        assertThat(eventDto.getInternalToken(), is("internalToken"));
    }

    @Test
    public void testClearIgnoredPropertiesForPerson() {

        PersonDto personDto = creator.createPerson();
        personDto.setAdditionalDetails("additionalDetails");
        personDto.setExternalId("externalId");
        personDto.setExternalToken("externalToken");
        personDto.setInternalToken("internalToken");

        getShareDataBuilderHelper().clearIgnoredProperties(personDto);

        assertThat(personDto.getFirstName(), not(nullValue()));
        assertThat(personDto.getExternalId(), is(nullValue()));
        assertThat(personDto.getExternalToken(), is(nullValue()));
        assertThat(personDto.getInternalToken(), is(nullValue()));
    }

    @Test
    public void testDoNotClearIgnoredPropertiesForPerson() {

        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS, Boolean.FALSE.toString());
        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID, Boolean.FALSE.toString());
        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN, Boolean.FALSE.toString());
        MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN, Boolean.FALSE.toString());

        PersonDto personDto = creator.createPerson();
        personDto.setAdditionalDetails("additionalDetails");
        personDto.setExternalId("externalId");
        personDto.setExternalToken("externalToken");
        personDto.setInternalToken("internalToken");

        getShareDataBuilderHelper().clearIgnoredProperties(personDto);

        assertThat(personDto.getFirstName(), not(nullValue()));
        assertThat(personDto.getAdditionalDetails(), is("additionalDetails"));
        assertThat(personDto.getExternalId(), is("externalId"));
        assertThat(personDto.getExternalToken(), is("externalToken"));
        assertThat(personDto.getInternalToken(), is("internalToken"));
    }

}
