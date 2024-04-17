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

package de.symeda.sormas.backend.user;

import static java.util.Collections.singletonList;
import static org.keycloak.representations.IDToken.LOCALE;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Functions;
import com.jayway.jsonpath.JsonPath;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.user.event.PasswordResetEvent;
import de.symeda.sormas.backend.user.event.SyncUsersFromProviderEvent;
import de.symeda.sormas.backend.user.event.UserCreateEvent;
import de.symeda.sormas.backend.user.event.UserUpdateEvent;

/**
 * @author Alex Vidrean
 * @since 15-Aug-20
 */
@Stateless
@LocalBean
public class KeycloakService {

	private static final String CLIENT_ID_SORMAS_STATS = "sormas-stats";
	private static final String KEYCLOAK_ROLE_SORMAS_STATS_ACCESS = "sormas-stats-access";
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	private static final String OIDC_REALM = "realm";
	private static final String OIDC_SERVER_URL = "auth-server-url";
	private static final String OIDC_SECRET = "credentials.secret";

	private static final String REALM_NAME = "SORMAS";

	private static final String ACTION_UPDATE_PASSWORD = "UPDATE_PASSWORD";
	private static final String ACTION_VERIFY_EMAIL = "VERIFY_EMAIL";

	private Keycloak keycloakInstance = null;

	@PostConstruct
	public void init() {

		if (!AuthProvider.KEYCLOAK.equalsIgnoreCase(configFacade.getAuthenticationProvider())) {
			logger.info("Keycloak Auth Provider not active");
			return;
		}

		Optional<String> oidcJson = ConfigProvider.getConfig().getOptionalValue("sormas.backend.security.oidc.json", String.class);

		if (!oidcJson.isPresent()) {
			logger.warn(
				"Undefined KEYCLOAK configuration for sormas.backend.security.oidc.json. Configure the property or disable the KEYCLOAK authentication provider.");
			return;
		}

		String keycloakJsonConfig = oidcJson.get();

		keycloakInstance = KeycloakBuilder.builder()
			.realm(JsonPath.read(keycloakJsonConfig, OIDC_REALM))
			.serverUrl(JsonPath.read(keycloakJsonConfig, OIDC_SERVER_URL))
			.clientId("sormas-backend")
			.clientSecret(JsonPath.read(keycloakJsonConfig, OIDC_SECRET))
			.grantType(OAuth2Constants.CLIENT_CREDENTIALS)
			.build();

	}

	/**
	 * Handles a user create event.
	 * <p>
	 * There are 3 scenarios for a user created event:
	 * <ol>
	 * <li>The user is a mock user (like the default system users) in which case the password is prefilled with the one defined in the
	 * code</li>
	 * <li>The user is a real and has an email address, in which case an email is sent to the user to activate the account</li>
	 * <li>The user is a real and doesn't have an email address, in which case the password will be setup by the system and user will be
	 * active automatically</li>
	 * </ol>
	 * </p>
	 *
	 * @param userCreateEvent
	 *            a real / mock user created event
	 * @see de.symeda.sormas.backend.common.StartupShutdownService
	 */
	public void handleUserCreateEvent(@Observes UserCreateEvent userCreateEvent) {
		Optional<Keycloak> keycloak = getKeycloakInstance();
		if (!keycloak.isPresent()) {
			logger.warn("Cannot obtain keycloak instance. Will not create user in keycloak");
			return;
		}

		User user = userCreateEvent.getUser();
		String userId = createUser(keycloak.get(), user);
		if (StringUtils.isNotBlank(user.getUserEmail())) {
			sendActivationEmail(keycloak.get(), userId);
		}
	}

	/**
	 * Handles a user update event.
	 * <p>
	 * There are 2 possible scenarios handled here:
	 * <ol>
	 * <li>The user already exists in Keycloak, which will trigger the update</li>
	 * <li>The user doesn't exist in Keycloak yet (maybe due to activating Keycloak later) which will trigger the user to be created</li>
	 * </ol>
	 * </p>
	 *
	 * @param userUpdateEvent
	 *            contains the old user and the new user information
	 */
	public void handleUserUpdateEvent(@Observes UserUpdateEvent userUpdateEvent) {
		Optional<Keycloak> keycloak = getKeycloakInstance();
		if (!keycloak.isPresent()) {
			logger.warn("Cannot obtain keycloak instance. Will not update user in keycloak");
			return;
		}

		User oldUser = userUpdateEvent.getOldUser();
		User newUser = userUpdateEvent.getNewUser();

		String existingUsername = oldUser != null ? oldUser.getUserName() : newUser.getUserName();

		try {
			Optional<UserRepresentation> userRepresentation = updateUser(keycloak.get(), existingUsername, newUser);
			if (!userRepresentation.isPresent()) {
				logger.debug("Cannot find user in Keycloak. Will try to create it");
				createUser(keycloak.get(), newUser);
			}
		} catch (Exception e) {
			if (userUpdateEvent.getExceptionCallback() != null) {
				userUpdateEvent.getExceptionCallback().accept(e.getMessage());
			}
			logger.error(e.getMessage(), e);
		}
	}

	public void handleUserUpdateEventAsync(@ObservesAsync UserUpdateEvent userUpdateEvent) {
		logger.debug("Handling userUpdateEvent asynchronously for user {}", userUpdateEvent.getNewUser().getUuid());
		handleUserUpdateEvent(userUpdateEvent);
	}

	/**
	 * Handles a user password request event.
	 * <p>
	 * There are 2 ways this is handled:
	 * <ol>
	 * <li>In case the user has an email address, the password reset email will be sent. This means the password will not be updated
	 * until the user follows the instructions in the email.</li>
	 * <li>In case the user doesn't have an email address, the password will automatically be updated for the user and login won't be
	 * possible with the old password anymore.</li>
	 * </ol>
	 * </p>
	 *
	 * @param passwordResetEvent
	 *            user and the plain text password which was set
	 */
	public void handlePasswordResetEvent(@Observes PasswordResetEvent passwordResetEvent) {
		Optional<Keycloak> keycloak = getKeycloakInstance();
		if (!keycloak.isPresent()) {
			logger.warn("Cannot obtain keycloak instance. Will not reset user password in keycloak");
			return;
		}

		User user = passwordResetEvent.getUser();
		Optional<UserRepresentation> userRepresentation = getUserByUsername(keycloak.get(), user.getUserName());
		if (!userRepresentation.isPresent()) {
			logger.warn("Cannot find user or email for user with username {} to reset the password", user.getUserName());
			return;
		}
		if (StringUtils.isNotBlank(user.getUserEmail())) {
			userRepresentation.ifPresent(existing -> sendPasswordResetEmail(keycloak.get(), existing.getId()));
		} else {
			userRepresentation.ifPresent(existing -> {
				setCredentials(existing, user.getPassword(), user.getSeed());
				keycloak.get().realm(REALM_NAME).users().get(existing.getId()).update(existing);
			});
		}
	}

	public void handleSyncUsersFromProviderEvent(@Observes SyncUsersFromProviderEvent syncUsersFromProviderEvent) {
		Optional<Keycloak> keycloak = getKeycloakInstance();
		if (keycloak.isEmpty()) {
			logger.warn("Cannot obtain keycloak instance. Will not sync users from provider");
			return;
		}

		List<User> existingUsers = syncUsersFromProviderEvent.getExistingUsers();
		Map<String, User> existingUsersByUsername =
			existingUsers.stream().collect(Collectors.toMap(user1 -> user1.getUserName().toLowerCase(), Functions.identity()));
		List<UserRepresentation> providerUsers = keycloak.get().realm(REALM_NAME).users().list();
		List<User> syncedUsers = providerUsers.stream().map(user -> {
			User sormasUser = existingUsersByUsername.get(user.getUsername().toLowerCase());
			if (sormasUser == null) {
				sormasUser = new User();
			}
			updateUser(sormasUser, user);

			return sormasUser;
		}).collect(Collectors.toList());

		Set<String> providerUserNames = providerUsers.stream().map(UserRepresentation::getUsername).collect(Collectors.toSet());
		List<User> deletedUsers = existingUsers.stream().filter(user -> !providerUserNames.contains(user.getUserName())).collect(Collectors.toList());

		syncUsersFromProviderEvent.getCallback().accept(syncedUsers, deletedUsers);

	}

	/**
	 * Creates a {@link UserRepresentation} from the SORMAS user and send the request to create the user to Keycloak.
	 *
	 * @return keycloak user identifier, which is extracted from the location of the response
	 *         `https://keycloak-url/auth/admin/realms/realm-name/users/user-identifier
	 */
	private String createUser(Keycloak keycloak, User user) {

		UserRepresentation userRepresentation = createUserRepresentation(user, user.getPassword());
		String ret;
		try (Response response = keycloak.realm(REALM_NAME).users().create(userRepresentation)) {
			if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
				throw new WebApplicationException(response);
			}
			String[] pathSegments = response.getLocation().getPath().split("/");
			ret = pathSegments[pathSegments.length - 1];
		}

		assignKeycloakClientRolesToUser(keycloak, user, ret);

		return ret;

	}

	private UserRepresentation createUserRepresentation(User user, String hashedPassword) {

		UserRepresentation userRepresentation = new UserRepresentation();

		userRepresentation.setEnabled(user.isActive());
		userRepresentation.setUsername(user.getUserName());
		userRepresentation.setFirstName(user.getFirstName());
		userRepresentation.setLastName(user.getLastName());
		setLanguage(userRepresentation, user.getLanguage());
		if (StringUtils.isNotBlank(hashedPassword)) {
			setCredentials(userRepresentation, hashedPassword, user.getSeed());
		}

		if (StringUtils.isNotBlank(user.getUserEmail())) {
			userRepresentation.setEmail(user.getUserEmail());
			userRepresentation.setRequiredActions(Arrays.asList(ACTION_VERIFY_EMAIL, ACTION_UPDATE_PASSWORD));
		}

		return userRepresentation;
	}

	private void updateUser(User user, UserRepresentation userRepresentation) {
		user.setActive(userRepresentation.isEnabled());
		user.setUserName(userRepresentation.getUsername());
		user.setFirstName(userRepresentation.getFirstName());
		user.setLastName(userRepresentation.getLastName());
		user.setLanguage(getLanguage(userRepresentation));
		user.setUserEmail(userRepresentation.getEmail());
	}

	private Optional<UserRepresentation> updateUser(Keycloak keycloak, String existingUsername, User newUser) {

		Optional<UserRepresentation> userRepresentation = getUserByUsername(keycloak, existingUsername);

		if (!userRepresentation.isPresent()) {
			logger.warn("Cannot find user to update for username {}", newUser.getUserName());
			return Optional.empty();
		}

		UserRepresentation newUserRepresentation = userRepresentation.get();

		updateUserRepresentation(newUserRepresentation, newUser);

		final String userId = newUserRepresentation.getId();

		keycloak.realm(REALM_NAME).users().get(userId).update(newUserRepresentation);

		assignKeycloakClientRolesToUser(keycloak, newUser, userId);

		return Optional.of(newUserRepresentation);
	}

	private void updateUserRepresentation(UserRepresentation userRepresentation, User user) {

		userRepresentation.setEnabled(user.isActive());
		userRepresentation.setUsername(user.getUserName());
		userRepresentation.setFirstName(user.getFirstName());
		userRepresentation.setLastName(user.getLastName());
		userRepresentation.setEmail(user.getUserEmail());
		setLanguage(userRepresentation, user.getLanguage());
	}

	/**
	 * Assigns a keycloak user a client role.
	 * 
	 * @param keycloak
	 *            the keycloak instance
	 * @param sormasUser
	 *            the SORMAS user we inspect for a certain right
	 * @param userResourceId
	 *            the user resource id in keycloak
	 */
	private void assignKeycloakClientRolesToUser(Keycloak keycloak, User sormasUser, String userResourceId) {

		// Please note that this cannot be done via the client representation directly as Keycloak is very strict about
		// resource creation. Currently, we only use this function to assign the sormas-stats-access client role to a
		// sormasUser if the sormasUser has the STATISTICS_ACCESS right.

		UsersResource usersResource = keycloak.realm(REALM_NAME).users();
		UserResource userResource = usersResource.get(userResourceId);

		assignSormasStatsRole(keycloak, sormasUser, userResource);
	}

	private void assignSormasStatsRole(Keycloak keycloak, User sormasUser, UserResource userResource) {
		if (StringUtils.isBlank(configFacade.getSormasStatsUrl())) {
			return;
		}

		Optional<ClientRepresentation> clientRepresentation =
			keycloak.realm(REALM_NAME).clients().findAll().stream().filter(client -> client.getClientId().equals(CLIENT_ID_SORMAS_STATS)).findFirst();
		if (!clientRepresentation.isPresent()) {
			logger.error("Cannot find client with id {}", CLIENT_ID_SORMAS_STATS);
			return;
		}

		final String clientRepId = clientRepresentation.get().getId();

		ClientResource clientResource = keycloak.realm(REALM_NAME).clients().get(clientRepId);
		Optional<RoleRepresentation> roleRepresentation =
			clientResource.roles().list().stream().filter(element -> element.getName().equals(KEYCLOAK_ROLE_SORMAS_STATS_ACCESS)).findFirst();

		if (!roleRepresentation.isPresent()) {
			logger.error("Cannot find role with name {}", KEYCLOAK_ROLE_SORMAS_STATS_ACCESS);
			return;
		}
		// we cannot add the role directly to the user in an update of the representation, we have to do it separately
		if (sormasUser.isActive() && sormasUser.hasUserRight(UserRight.STATISTICS_ACCESS)) {
			userResource.roles().clientLevel(clientRepId).add(Collections.singletonList(roleRepresentation.get()));
		} else {
			// we cannot remove the role directly from the user in an update operation, we have to do it explicitly
			userResource.roles().clientLevel(clientRepId).remove(Collections.singletonList(roleRepresentation.get()));
		}
	}

	private Optional<UserRepresentation> getUserByUsername(Keycloak keycloak, String username) {
		return keycloak.realm(REALM_NAME).users().search(username, true).stream().findFirst();
	}

	private void sendActivationEmail(Keycloak keycloak, String userId) {
		keycloak.realm(REALM_NAME).users().get(userId).sendVerifyEmail();
	}

	private void sendPasswordResetEmail(Keycloak keycloak, String userId) {
		keycloak.realm(REALM_NAME).users().get(userId).executeActionsEmail(Collections.singletonList(ACTION_UPDATE_PASSWORD));
	}

	private Language getLanguage(UserRepresentation userRepresentation) {
		Map<String, List<String>> attributes = userRepresentation.getAttributes();
		if (attributes != null) {
			List<String> locale = attributes.get(LOCALE);
			if (locale != null && !locale.isEmpty()) {
				return Language.fromLocaleString(locale.get(0));
			}
		}
		return null;
	}

	private void setLanguage(UserRepresentation userRepresentation, Language language) {
		Map<String, List<String>> attributes = userRepresentation.getAttributes();
		if (attributes == null) {
			attributes = new HashMap<>();
		}

		if (language != null) {
			attributes.put(LOCALE, Collections.singletonList(language.getLocale().getLanguage()));
		}
	}

	private void setCredentials(UserRepresentation userRepresentation, String password, String salt) {
		JsonObjectBuilder secretData = Json.createObjectBuilder();
		secretData.add("value", password);
		secretData.add("salt", Base64.getEncoder().encodeToString(salt.getBytes()));

		JsonObjectBuilder credentialData = Json.createObjectBuilder();
		credentialData.add("hashIterations", 1);
		credentialData.add("algorithm", "sormas-sha256");

		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		// force user to reset password to avoid the usage of SHA for password hashing for any long than necessary
		credential.setTemporary(true);
		credential.setSecretData(secretData.build().toString());
		credential.setCredentialData(credentialData.build().toString());
		userRepresentation.setCredentials(singletonList(credential));
	}

	private Optional<Keycloak> getKeycloakInstance() {
		return Optional.ofNullable(keycloakInstance);
	}

}
