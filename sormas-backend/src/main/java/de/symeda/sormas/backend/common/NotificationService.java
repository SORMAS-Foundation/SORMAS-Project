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

import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.symeda.sormas.api.user.NotificationProtocol;
import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.backend.common.messaging.MessageSubject;
import de.symeda.sormas.backend.common.messaging.MessagingService;
import de.symeda.sormas.backend.common.messaging.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserRole;
import de.symeda.sormas.backend.user.UserRoleService;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "NotificationService")
@LocalBean
public class NotificationService {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private MessagingService messagingService;

	@EJB
	private UserService userService;

	@EJB
	private UserRoleService userRoleService;

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
			() -> buildUserMessages(regions, additionalUsers, message, NotificationProtocol.EMAIL, allowedNotificationTypes),
			() -> buildUserMessages(regions, additionalUsers, message, NotificationProtocol.SMS, allowedNotificationTypes));
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
				filterUserMessagesByRoles(
					emailUserMessagesSupplier.get(),
					userRoleService.getActiveByNotificationTypes(NotificationProtocol.EMAIL, allowedNotificationTypes)),
				subject,
				subjectParams);
			messagingService.sendSms(
				filterUserMessagesByRoles(
					smsUserMessagesSupplier.get(),
					userRoleService.getActiveByNotificationTypes(NotificationProtocol.SMS, allowedNotificationTypes)),
				subject,
				subjectParams);
		}
	}

	private Map<User, String> buildUserMessages(
		List<Region> regions,
		List<User> additionalUsers,
		String message,
		NotificationProtocol notificationProtocol,
		Collection<NotificationType> notificationTypes) {
		List<User> recipients = new ArrayList<>();
		if (regions != null) {
			// fetch notification types, because the filterUserMessagesByRoles logic will need it anyway
			recipients.addAll(userService.getAllByRegionsAndNotificationTypes(regions, notificationProtocol, notificationTypes, true));
		}

		if (additionalUsers != null) {
			additionalUsers.stream().forEach(user -> {
				if (user.getUserRoles()
					.stream()
					.flatMap(
						userRole -> NotificationProtocol.EMAIL.equals(notificationProtocol)
							? userRole.getEmailNotifications().stream()
							: userRole.getSmsNotifications().stream())
					.anyMatch(type -> notificationTypes.contains(type))) {
					recipients.add(user);
				}
			});
		}

		return recipients.stream().collect(Collectors.toMap(Function.identity(), (u) -> message));
	}

	private Map<User, String> filterUserMessagesByRoles(Map<User, String> userStringMap, Collection<UserRole> userRoles) {
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
