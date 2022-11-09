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

package de.symeda.sormas.backend.sormastosormas.crypto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeTest;
import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.TestDataCreator;

public class SormasToSormasEncryptionServiceTest extends SormasToSormasFacadeTest {

	@Test
	public void testEncryptDecrypt() throws SormasToSormasException {
		// create test data
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		useSurveillanceOfficerLogin(rdcf);
		PersonDto person = creator.createPerson("FirstName", "LastName");
		UserReferenceDto officer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
		});

		List<SormasToSormasCaseDto> bodyToEncrypt = new ArrayList<>();
		SormasToSormasOriginInfoDto s2sOriginInfo = new SormasToSormasOriginInfoDto();
		s2sOriginInfo.setComment("Test comment");
		bodyToEncrypt.add(new SormasToSormasCaseDto(person, caze, s2sOriginInfo));

		mockS2Snetwork();

		// encrypt
		mockDefaultServerAccess();
		SormasToSormasEncryptedDataDto encryptedBody = getSormasToSormasEncryptionFacade().signAndEncrypt(bodyToEncrypt, SECOND_SERVER_ID);
		mockSecondServerAccess();

		assertThat(encryptedBody.getSenderId(), is(DEFAULT_SERVER_ID));
		assertThat(encryptedBody.getData(), instanceOf(byte[].class));

		// decrypt
		mockSecondServerAccess();
		SormasToSormasCaseDto[] decryptedBody = getSormasToSormasEncryptionFacade().decryptAndVerify(encryptedBody, SormasToSormasCaseDto[].class);
		mockDefaultServerAccess();

		assertThat(decryptedBody.length, is(1));
		SormasToSormasCaseDto decryptedCase = decryptedBody[0];
		assertThat(decryptedCase.getPerson().getFirstName(), is("FirstName"));
		assertThat(decryptedCase.getPerson().getLastName(), is("LastName"));
		assertThat(decryptedCase.getOriginInfo().getComment(), is("Test comment"));
		assertThat(decryptedCase.getSamples(), is(nullValue()));
		assertThat(decryptedCase.getAssociatedContacts(), is(nullValue()));

	}
}
