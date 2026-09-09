/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.backend.common.messaging;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.user.User;

public class MessagingServiceTest extends AbstractBeanTest {

	@Mock
	private EmailService emailService;

	@Inject
	private MessagingService messagingService;

	@Test
	public void testSendEmailAttemptsAllRecipientsAfterDeliveryFailure() throws Exception {

		TestDataCreator.RDCF rdcf = creator.createRDCF();

		UserDto firstUserDto = creator.createUser(
			rdcf,
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR),
			user -> user.setUserEmail("first-recipient@sormas-test.de"));
		UserDto secondUserDto = creator.createUser(
			rdcf,
			creator.getUserRoleReference(DefaultUserRole.CASE_SUPERVISOR),
			user -> user.setUserEmail("second-recipient@sormas-test.de"));

		User firstUser = getUserService().getByReferenceDto(firstUserDto.toReference());
		User secondUser = getUserService().getByReferenceDto(secondUserDto.toReference());

		Mockito.doThrow(new MessagingException("Simulated delivery failure"))
			.when(emailService)
			.sendEmailAsync(eq(firstUserDto.getUserEmail()), any(), any());

		Map<User, String> userMessages = new LinkedHashMap<>();
		userMessages.put(firstUser, "First message");
		userMessages.put(secondUser, "Second message");

		assertThrows(
			NotificationDeliveryFailedException.class,
			() -> messagingService.sendEmail(userMessages, MessageSubject.CASE_CLASSIFICATION_CHANGED, new Object[0]));

		Mockito.verify(emailService).sendEmailAsync(eq(firstUserDto.getUserEmail()), any(), any());
		Mockito.verify(emailService).sendEmailAsync(eq(secondUserDto.getUserEmail()), any(), any());
	}

	@Test
	public void testSendEmailSkipsInactiveRecipientWithoutAbortingBatch() throws Exception {

		TestDataCreator.RDCF rdcf = creator.createRDCF();

		UserDto firstUserDto = creator.createUser(
			rdcf,
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR),
			user -> user.setUserEmail("inactive-recipient@sormas-test.de"));
		UserDto secondUserDto = creator.createUser(
			rdcf,
			creator.getUserRoleReference(DefaultUserRole.CASE_SUPERVISOR),
			user -> user.setUserEmail("active-recipient@sormas-test.de"));

		firstUserDto.setActive(false);
		getUserFacade().saveUser(firstUserDto, false);

		User firstUser = getUserService().getByReferenceDto(firstUserDto.toReference());
		User secondUser = getUserService().getByReferenceDto(secondUserDto.toReference());

		Map<User, String> userMessages = new LinkedHashMap<>();
		userMessages.put(firstUser, "First message");
		userMessages.put(secondUser, "Second message");

		assertDoesNotThrow(
			() -> messagingService.sendEmail(userMessages, MessageSubject.CASE_CLASSIFICATION_CHANGED, new Object[0]));

		Mockito.verify(emailService, Mockito.never()).sendEmailAsync(eq(firstUserDto.getUserEmail()), any(), any());
		Mockito.verify(emailService).sendEmailAsync(eq(secondUserDto.getUserEmail()), any(), any());
	}
}
