/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.messaging.MessageSubject;
import de.symeda.sormas.backend.common.messaging.MessagingService;
import de.symeda.sormas.backend.common.messaging.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;

public class NotificationServiceTest extends AbstractBeanTest {

	@Mock
	private FeatureConfigurationFacadeEjb configurationFacade;

	@Mock
	private MessagingService messagingService;

	@Inject
	private NotificationService notificationService;

	@SuppressWarnings("unchecked")
	public void init() {
		super.init();

		Mockito.when(configurationFacade.isFeatureEnabled(any())).thenReturn(true);
		Mockito.when(configurationFacade.isFeatureEnabled(FeatureType.KEYCLOAK_TO_SORMAS_USER_SYNC)).thenReturn(false);
	}

	@Test
	public void testSendNotificationsDirectInvokation() throws NotificationDeliveryFailedException {

		notificationService
			.sendNotifications(NotificationType.CASE_LAB_RESULT_ARRIVED, null, null, MessageSubject.CASE_CLASSIFICATION_CHANGED, "Test message");

		Mockito.verify(messagingService, Mockito.times(1)).sendEmail(any(), any(), any());
		Mockito.verify(messagingService, Mockito.times(1)).sendSms(any(), any(), any());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSendNotificationsLoadUsersByRoles() throws NotificationDeliveryFailedException {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		Region region = getRegionService().getByReferenceDto(rdcf.region);

		UserDto survSup = creator.createUser(rdcf, "Surv", "Sup", creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		UserDto caseSup = creator.createUser(rdcf, "Case", "Sup", creator.getUserRoleReference(DefaultUserRole.CASE_SUPERVISOR));

		Mockito.doAnswer(invocation -> {
			Map<User, String> userMessages = invocation.getArgument(0);

			assertThat(userMessages.size(), is(2));
			assertThat(userMessages.get(getUserService().getByReferenceDto(survSup.toReference())), is("Test message"));
			assertThat(userMessages.get(getUserService().getByReferenceDto(caseSup.toReference())), is("Test message"));

			return null;
		}).when(messagingService).sendEmail(any(), any(), any());

		notificationService.sendNotifications(
			NotificationType.CASE_LAB_RESULT_ARRIVED,
			Collections.singletonList(region),
			null,
			MessageSubject.LAB_RESULT_ARRIVED,
			"Test message");

		Mockito.verify(messagingService, Mockito.times(1)).sendEmail(any(), any(), any());
		Mockito.verify(messagingService, Mockito.times(1)).sendSms(any(), any(), any());
	}

	@Test
	public void testSendNotificationsAdditionalUsers() throws NotificationDeliveryFailedException {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		Region region = getRegionService().getByReferenceDto(rdcf.region);

		@SuppressWarnings("unused")
		UserDto survSup = creator.createUser(rdcf, "Surv", "Sup", creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		UserDto survOff = creator.createUser(rdcf, "Case", "Sup", creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		User survOffUser = getEagerUser(survOff.getUuid());

//		Mockito.when(userService.getAllByRegionsAndNotificationTypes(any(),any(), any())).then(invocation -> {
//			// load only for SURVEILLANCE_SUPERVISOR, so the additional CASE_SUPERVISOR user will be added in the notification service
//			return getUserService().getAllByRegionsAndNotificationTypes(
//				(List<Region>) invocation.getArgument(0),
//				(NotificationProtocol) invocation.getArgument(1),
//				Collections.singletonList(NotificationType.REGION));
//		});
//		Mockito.doAnswer(invocation -> {
//			Map<User, String> userMessages = (Map<User, String>) invocation.getArgument(0);
//
//			assertThat(userMessages.size(), is(2));
//			assertThat(userMessages.get(getUserService().getByReferenceDto(survSup.toReference())), is("Test message"));
//			assertThat(userMessages.get(getUserService().getByReferenceDto(survOff.toReference())), is("Test message"));
//
//			return null;
//		}).when(messagingService).sendEmail(any(), any(), any());

		// VISIT_COMPLETED would normally only be sent to the SURVEILLANCE_SUPERVISOR
		notificationService.sendNotifications(
			NotificationType.CONTACT_VISIT_COMPLETED,
			Collections.singletonList(region),
			Collections.singletonList(survOffUser),
			MessageSubject.VISIT_COMPLETED,
			"Test message");

		Mockito.verify(messagingService, Mockito.times(1)).sendEmail(any(), any(), any());
		Mockito.verify(messagingService, Mockito.times(1)).sendSms(any(), any(), any());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSendNotificationsFilterUserMessagesByRoles() throws NotificationDeliveryFailedException {

		TestDataCreator.RDCF rdcf = creator.createRDCF();

		UserDto survSup = creator.createUser(rdcf, "Surv", "Sup", creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		UserDto caseSup = creator.createUser(rdcf, "Case", "Sup", creator.getUserRoleReference(DefaultUserRole.CASE_SUPERVISOR));
		UserDto contSup = creator.createUser(rdcf, "Cont", "Sup", creator.getUserRoleReference(DefaultUserRole.CONTACT_SUPERVISOR));

		User survSupUser = getEagerUser(survSup.getUuid());
		User survOffUser = getEagerUser(caseSup.getUuid());
		User contSupUser = getEagerUser(contSup.getUuid());

		Mockito.doAnswer(invocation -> {
			Map<User, String> userMessages = (Map<User, String>) invocation.getArgument(0);

			assertThat(userMessages.size(), is(2));
			assertThat(userMessages.get(survSupUser), is("Test message SS"));
			assertThat(userMessages.get(survOffUser), is("Test message SO"));

			return null;
		}).when(messagingService).sendEmail(any(), any(), any());

		notificationService.sendNotifications(NotificationType.CASE_LAB_RESULT_ARRIVED, MessageSubject.LAB_RESULT_ARRIVED, () -> {
			Map<User, String> userMessages = new HashMap<>();
			userMessages.put(survOffUser, "Test message SO");
			userMessages.put(survSupUser, "Test message SS");
			userMessages.put(contSupUser, "Test message CS");

			return userMessages;
		});

		Mockito.verify(messagingService, Mockito.times(1)).sendEmail(any(), any(), any());
		Mockito.verify(messagingService, Mockito.times(1)).sendSms(any(), any(), any());
	}

	@Test
	public void testSendNotificationsFeatureDisabled() throws NotificationDeliveryFailedException {

		TestDataCreator.RDCF rdcf = creator.createRDCF();

		UserDto survSup = creator.createSurveillanceSupervisor(rdcf);
		User survSupUser = getEagerUser(survSup.getUuid());

		Mockito.when(configurationFacade.isFeatureEnabled(FeatureType.TASK_NOTIFICATIONS)).thenReturn(false);

		notificationService.sendNotifications(NotificationType.TASK_START, MessageSubject.TASK_START, () -> {
			Map<User, String> userMessages = new HashMap<>();
			userMessages.put(survSupUser, "Test message");
			return userMessages;
		});

		Mockito.verify(messagingService, Mockito.times(0)).sendEmail(any(), any(), any());
		Mockito.verify(messagingService, Mockito.times(0)).sendSms(any(), any(), any());
	}
}
