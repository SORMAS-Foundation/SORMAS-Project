/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.messaging.MessageSubject;
import de.symeda.sormas.backend.common.messaging.MessagingService;
import de.symeda.sormas.backend.common.messaging.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

@Stateless(name = "NotificationService")
@LocalBean
public class NotificationService {

	@EJB
	private MessagingService messagingService;

	@EJB
	private UserService userService;

	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	public void sendNotifications(
		NotificationType notificationType,
		List<Region> regions,
		List<User> additionalUsers,
		MessageSubject subject,
		String message)
		throws NotificationDeliveryFailedException {
		sendNotifications(Collections.singleton(notificationType), regions, additionalUsers, subject, message);
	}

	public void sendNotifications(
		Set<NotificationType> notificationTypes,
		List<Region> regions,
		List<User> additionalUsers,
		MessageSubject subject,
		String message)
		throws NotificationDeliveryFailedException {
		Set<NotificationType> allowedNotificationTypes = getAllowedNotificationTypes(notificationTypes);

		sendNotifications(
			allowedNotificationTypes,
			subject,
			new Object[] {},
			() -> buildUserMessages(regions, additionalUsers, message, UserRole.getWithEmailNotificationTypes(allowedNotificationTypes)),
			() -> buildUserMessages(regions, additionalUsers, message, UserRole.getWithSmsNotificationTypes(allowedNotificationTypes)));
	}

	public void sendNotifications(NotificationType notificationType, MessageSubject subject, Supplier<Map<User, String>> userMessagesSupplier)
		throws NotificationDeliveryFailedException {
		sendNotifications(notificationType, subject, new Object[] {}, userMessagesSupplier);
	}

	public void sendNotifications(
		NotificationType notificationType,
		MessageSubject subject,
		Object[] subjectParams,
		Supplier<Map<User, String>> userMessagesSupplier)
		throws NotificationDeliveryFailedException {

		Map<User, String> cachedUserMessages = new HashMap<>();
		Supplier<Map<User, String>> cachedUserMessagesSupplier = () -> {
			if (cachedUserMessages.isEmpty()) {
				cachedUserMessages.putAll(userMessagesSupplier.get());
			}

			return cachedUserMessages;
		};

		sendNotifications(Collections.singleton(notificationType), subject, subjectParams, cachedUserMessagesSupplier, cachedUserMessagesSupplier);
	}

	private void sendNotifications(
		Set<NotificationType> notificationTypes,
		MessageSubject subject,
		Object[] subjectParams,
		Supplier<Map<User, String>> emailUserMessagesSupplier,
		Supplier<Map<User, String>> smsUserMessagesSupplier)
		throws NotificationDeliveryFailedException {

		Set<NotificationType> allowedNotificationTypes = getAllowedNotificationTypes(notificationTypes);

		if (!allowedNotificationTypes.isEmpty()) {
			messagingService.sendEmail(
				filterUserMessagesByRoles(emailUserMessagesSupplier.get(), UserRole.getWithEmailNotificationTypes(allowedNotificationTypes)),
				subject,
				subjectParams);
			messagingService.sendSms(
				filterUserMessagesByRoles(smsUserMessagesSupplier.get(), UserRole.getWithSmsNotificationTypes(allowedNotificationTypes)),
				subject,
				subjectParams);
		}
	}

	private Map<User, String> buildUserMessages(List<Region> regions, List<User> additionalUsers, String message, UserRole[] userRoles) {
		List<User> recipients = new ArrayList<>();
		if (regions != null) {
			recipients.addAll(userService.getAllByRegionsAndUserRoles(regions, userRoles));
		}

		if (additionalUsers != null) {
			recipients
				.addAll(additionalUsers.stream().filter(u -> !recipients.contains(u) && u.hasAnyUserRole(userRoles)).collect(Collectors.toList()));
		}

		return recipients.stream().collect(Collectors.toMap(Function.identity(), (u) -> message));
	}

	private Map<User, String> filterUserMessagesByRoles(Map<User, String> userStringMap, UserRole[] userRoles) {
		return userStringMap.entrySet()
			.stream()
			.filter(e -> e.getKey().hasAnyUserRole(userRoles))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private Set<NotificationType> getAllowedNotificationTypes(Set<NotificationType> notificationTypes) {
		return notificationTypes.stream()
			.filter(nt -> featureConfigurationFacade.isFeatureEnabled(nt.getRelatedFeatureType()))
			.collect(Collectors.toSet());
	}

}
