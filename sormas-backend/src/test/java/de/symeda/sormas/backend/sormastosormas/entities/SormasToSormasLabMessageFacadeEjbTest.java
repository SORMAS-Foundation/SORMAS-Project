/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.entities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response;

import de.symeda.sormas.api.sormastosormas.labmessage.SormasToSormasLabMessageDto;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasTest;

public class SormasToSormasLabMessageFacadeEjbTest extends SormasToSormasTest {

	@Test
	public void testSendLabMessage() throws SormasToSormasException {
		Date dateNow = new Date();

		LabMessageDto labMessage = creator.createLabMessage((lm) -> setLabMessageFields(lm, dateNow));

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/labmessages"));

				List<SormasToSormasLabMessageDto> postBody = invocation.getArgument(2, List.class);
				assertThat(postBody.size(), is(1));
				LabMessageDto sharedLabMessage = postBody.get(0).getEntity();

				assertThat(sharedLabMessage.getUuid(), is(labMessage.getUuid()));
				assertLabMessageFields(sharedLabMessage, dateNow);

				return Response.noContent().build();
			});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));

		getSormasToSormasLabMessageFacade().sendLabMessages(Collections.singletonList(labMessage.getUuid()), options);

		Mockito.verify(MockProducer.getSormasToSormasClient(), Mockito.times(1))
			.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any());
		assertThat(getLabMessageFacade().getByUuid(labMessage.getUuid()).getStatus(), is(LabMessageStatus.FORWARDED));
	}

	@Test
	public void testSaveLabMessages() throws SormasToSormasException, SormasToSormasValidationException {
		LabMessageDto labMessage = LabMessageDto.build();
		Date dateNow = new Date();
		setLabMessageFields(labMessage, dateNow);

		SormasToSormasEncryptedDataDto encryptedData = encryptShareDataAsArray(new SormasToSormasLabMessageDto(labMessage));
		getSormasToSormasLabMessageFacade().saveLabMessages(encryptedData);

		LabMessageDto savedLabMessage = getLabMessageFacade().getByUuid(labMessage.getUuid());
		assertThat(savedLabMessage, is(notNullValue()));
		assertLabMessageFields(savedLabMessage, dateNow);
	}

	private void setLabMessageFields(LabMessageDto labMessage, Date dateValue) {
		labMessage.setMessageDateTime(dateValue);
		labMessage.setSampleDateTime(dateValue);
		labMessage.setSampleMaterial(SampleMaterial.RECTAL_SWAB);
		labMessage.setLabSampleId("Test lab sample ID");
		labMessage.setPersonFirstName("James");
		labMessage.setPersonLastName("Smith");
		labMessage.setPersonPostalCode("test postal code");
	}

	private void assertLabMessageFields(LabMessageDto labMessage, Date dateValue) {
		long dateTime = dateValue.getTime();

		assertThat(labMessage.getMessageDateTime().getTime(), is(dateTime));
		assertThat(labMessage.getSampleDateTime().getTime(), is(dateTime));
		assertThat(labMessage.getSampleMaterial(), is(SampleMaterial.RECTAL_SWAB));
		assertThat(labMessage.getLabSampleId(), is("Test lab sample ID"));
		assertThat(labMessage.getPersonFirstName(), is("James"));
		assertThat(labMessage.getPersonLastName(), is("Smith"));
		assertThat(labMessage.getPersonPostalCode(), is("test postal code"));
	}
}
