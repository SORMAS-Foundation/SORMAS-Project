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

package de.symeda.sormas.backend.sormastosormas;

import static de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjbTest.DEFAULT_SERVER_ACCESS_CN;
import static de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjbTest.DEFAULT_SERVER_ACCESS_DATA_CSV;
import static de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjbTest.SECOND_SERVER_ACCESS_CN;
import static de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjbTest.SECOND_SERVER_ACCESS_DATA_CSV;
import static de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjbTest.SECOND_SERVER_REST_PASSWORD;
import static de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjbTest.SECOND_SERVER_REST_URL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

import javax.ws.rs.core.Response;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.StartupShutdownService;

public class SormasToSormasLabMessageFacadeEjbTest extends AbstractBeanTest {

	private ObjectMapper objectMapper;

	@Before
	public void setUp() {
		objectMapper = new ObjectMapper();

		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

		mockDefaultServerAccess();
	}

	@Test
	public void testSendLabMessage() throws JsonProcessingException, SormasToSormasException {
		Date dateNow = new Date();

		LabMessageDto labMessage = creator.createLabMessage((lm) -> setLabMessageFields(lm, dateNow));

		Mockito.when(MockProducer.getSormasToSormasClient().post(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.any()))
			.thenAnswer(invocation -> {
				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_REST_URL));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/labmessages"));

				String authToken = invocation.getArgument(2, String.class);
				assertThat(authToken, startsWith("Basic "));
				String credentials = new String(Base64.getDecoder().decode(authToken.replace("Basic ", "")), StandardCharsets.UTF_8);
				// uses password from server-list.csv from `serveraccessdefault` package
				assertThat(credentials, is(StartupShutdownService.SORMAS_TO_SORMAS_USER_NAME + ":" + SECOND_SERVER_REST_PASSWORD));

				SormasToSormasEncryptedDataDto encryptedData = invocation.getArgument(3, SormasToSormasEncryptedDataDto.class);
				assertThat(encryptedData.getOrganizationId(), is(DEFAULT_SERVER_ACCESS_CN));

				LabMessageDto[] sharedMessages = decryptSharesData(encryptedData.getData(), LabMessageDto[].class);
				LabMessageDto sharedLabMessage = sharedMessages[0];

				assertThat(labMessage.getUuid(), is(labMessage.getUuid()));
				assertLabMessageFields(sharedLabMessage, dateNow);

				return Response.noContent().build();
			});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new ServerAccessDataReferenceDto(SECOND_SERVER_ACCESS_CN));

		getSormasToSormasLabMessageFacade().sendLabMessages(Collections.singletonList(labMessage.getUuid()), options);

		Mockito.verify(MockProducer.getSormasToSormasClient(), Mockito.times(1))
			.post(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.any());
		assertThat(getLabMessageFacade().getByUuid(labMessage.getUuid()), is(nullValue()));
	}

	@Test
	public void testSaveLabMessages() throws JsonProcessingException, SormasToSormasException, SormasToSormasValidationException {
		LabMessageDto labMessage = LabMessageDto.build();
		Date dateNow = new Date();
		setLabMessageFields(labMessage, dateNow);

		byte[] encryptedData = encryptShareData(labMessage);
		getSormasToSormasLabMessageFacade().saveLabMessages(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		LabMessageDto savedLabMessage = getLabMessageFacade().getByUuid(labMessage.getUuid());
		assertThat(savedLabMessage, is(notNullValue()));
		assertLabMessageFields(savedLabMessage, dateNow);
	}

	@NotNull
	private void setLabMessageFields(LabMessageDto labMessage, Date dateValue) {
		labMessage.setMessageDateTime(dateValue);
		labMessage.setSampleDateTime(dateValue);
		labMessage.setSampleMaterial(SampleMaterial.RECTAL_SWAB);
		labMessage.setLabSampleId("Test lab sample ID");
		labMessage.setTestType(PathogenTestType.CULTURE);
		labMessage.setTestDateTime(dateValue);
		labMessage.setTestResult(PathogenTestResultType.PENDING);
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
		assertThat(labMessage.getTestType(), is(PathogenTestType.CULTURE));
		assertThat(labMessage.getTestDateTime().getTime(), is(dateTime));
		assertThat(labMessage.getTestResult(), is(PathogenTestResultType.PENDING));
		assertThat(labMessage.getPersonFirstName(), is("James"));
		assertThat(labMessage.getPersonLastName(), is("Smith"));
		assertThat(labMessage.getPersonPostalCode(), is("test postal code"));
	}

	private byte[] encryptShareData(Object shareData) throws JsonProcessingException, SormasToSormasException {
		mockDefaultServerAccess();

		byte[] data = objectMapper.writeValueAsBytes(Collections.singletonList(shareData));
		byte[] encryptedData = getSormasToSormasEncryptionService().encrypt(data, SECOND_SERVER_ACCESS_CN);

		mockSecondServerAccess();

		return encryptedData;
	}

	private <T> T[] decryptSharesData(byte[] data, Class<T[]> dataType) throws SormasToSormasException, IOException {
		mockSecondServerAccess();

		byte[] decryptData = getSormasToSormasEncryptionService().decrypt(data, DEFAULT_SERVER_ACCESS_CN);
		T[] parsedData = objectMapper.readValue(decryptData, dataType);

		mockDefaultServerAccess();

		return parsedData;
	}

	private void mockSecondServerAccess() {
		File file = new File("src/test/java/de/symeda/sormas/backend/sormastosormas/serveraccesssecond");

		Mockito.when(SormasToSormasFacadeEjbTest.MockSormasToSormasConfigProducer.sormasToSormasConfig.getPath()).thenReturn(file.getAbsolutePath());
		Mockito.when(SormasToSormasFacadeEjbTest.MockSormasToSormasConfigProducer.sormasToSormasConfig.getServerAccessDataFileName())
			.thenReturn(SECOND_SERVER_ACCESS_DATA_CSV);
		Mockito.when(SormasToSormasFacadeEjbTest.MockSormasToSormasConfigProducer.sormasToSormasConfig.getKeystoreName())
			.thenReturn("second.sormas2sormas.keystore.p12");
		Mockito.when(SormasToSormasFacadeEjbTest.MockSormasToSormasConfigProducer.sormasToSormasConfig.getKeystorePass()).thenReturn("certiPass");
		Mockito.when(SormasToSormasFacadeEjbTest.MockSormasToSormasConfigProducer.sormasToSormasConfig.getTruststoreName())
			.thenReturn("sormas2sormas.truststore.p12");
		Mockito.when(SormasToSormasFacadeEjbTest.MockSormasToSormasConfigProducer.sormasToSormasConfig.getTruststorePass()).thenReturn("trusteR");
	}

	private void mockDefaultServerAccess() {

		File file = new File("src/test/java/de/symeda/sormas/backend/sormastosormas/serveraccessdefault");

		Mockito.when(SormasToSormasFacadeEjbTest.MockSormasToSormasConfigProducer.sormasToSormasConfig.getPath()).thenReturn(file.getAbsolutePath());
		Mockito.when(SormasToSormasFacadeEjbTest.MockSormasToSormasConfigProducer.sormasToSormasConfig.getServerAccessDataFileName())
			.thenReturn(DEFAULT_SERVER_ACCESS_DATA_CSV);
		Mockito.when(SormasToSormasFacadeEjbTest.MockSormasToSormasConfigProducer.sormasToSormasConfig.getKeystoreName())
			.thenReturn("default.sormas2sormas.keystore.p12");
		Mockito.when(SormasToSormasFacadeEjbTest.MockSormasToSormasConfigProducer.sormasToSormasConfig.getKeystorePass()).thenReturn("certPass");
		Mockito.when(SormasToSormasFacadeEjbTest.MockSormasToSormasConfigProducer.sormasToSormasConfig.getTruststoreName())
			.thenReturn("sormas2sormas.truststore.p12");
		Mockito.when(SormasToSormasFacadeEjbTest.MockSormasToSormasConfigProducer.sormasToSormasConfig.getTruststorePass()).thenReturn("truster");
	}
}
