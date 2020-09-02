/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.user;

import com.nimbusds.jose.util.JSONObjectUtils;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.user.event.PasswordResetEvent;
import de.symeda.sormas.backend.user.event.UserCreateEvent;
import de.symeda.sormas.backend.user.event.UserUpdateEvent;
import net.minidev.json.JSONObject;
import org.eclipse.microprofile.config.ConfigProvider;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.keycloak.representations.IDToken.LOCALE;

/**
 * @author Alex Vidrean
 * @since 15-Aug-20
 */
@Stateless
@LocalBean
public class KeycloakService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String OIDC_REALM = "realm";
	private static final String OIDC_SERVER_URL = "auth-server-url";
	private static final String OIDC_CREDENTIALS = "credentials";
	private static final String OIDC_SECRET = "secret";

	private static final String REALM_NAME = "SORMAS";

	private Keycloak keycloak = null;

	@PostConstruct
	public void init() {
		Optional<String> oidcJson = ConfigProvider.getConfig().getOptionalValue("sormas.backend.security.oidc.json", String.class);

		if (!oidcJson.isPresent()) {
			logger.warn("Undefined KEYCLOAK configuration for sormas.backend.security.oidc.json. Configure the property or disable the KEYCLOAK authentication provider.");
			return;
		}

		try {
			JSONObject json = JSONObjectUtils.parse(oidcJson.get());

			keycloak = KeycloakBuilder.builder()
				.realm(json.getAsString(OIDC_REALM))
				.serverUrl(json.getAsString(OIDC_SERVER_URL))
				.clientId("sormas-backend")
				.clientSecret(JSONObjectUtils.getJSONObject(json, OIDC_CREDENTIALS).getAsString(OIDC_SECRET))
				.grantType(OAuth2Constants.CLIENT_CREDENTIALS)
				.build();

		} catch (ParseException e) {
			throw new IllegalArgumentException("Invalid JSON for backend keycloak oidc");
		}
	}

	public void handleUserCreateEvent(@Observes UserCreateEvent userCreateEvent) {
		Optional<Keycloak> keycloak = getKeycloak();
		if(!keycloak.isPresent()) {
			logger.warn("Cannot obtain keycloak instance. Will not create user in keycloak");
			return;
		}

		User user = userCreateEvent.getUser();
		String userId = createUser(keycloak.get(), user);
		sendActivationEmail(keycloak.get(), userId);
	}

	public void handleUserUpdateEvent(@Observes UserUpdateEvent userUpdateEvent) {
		Optional<Keycloak> keycloak = getKeycloak();
		if(!keycloak.isPresent()) {
			logger.warn("Cannot obtain keycloak instance. Will not update user in keycloak");
			return;
		}

		User newUser = userUpdateEvent.getNewUser();
		User oldUser = userUpdateEvent.getOldUser();

		Optional<UserRepresentation> userRepresentation = updateUser(keycloak.get(), oldUser, newUser);
		if(!userRepresentation.isPresent()) {
			logger.debug("Cannot find user in Keycloak. Will try to create it");
			String userId = createUser(keycloak.get(), newUser);
			sendActivationEmail(keycloak.get(), userId);
		}
	}

	public void handlePasswordResetEvent(@Observes PasswordResetEvent passwordResetEvent) {
		Optional<Keycloak> keycloak = getKeycloak();
		if(!keycloak.isPresent()) {
			logger.warn("Cannot obtain keycloak instance. Will not reset user password in keycloak");
			return;
		}

		User user = passwordResetEvent.getUser();
		Optional<UserRepresentation> userRepresentation = getUserByUsername(keycloak.get(), user.getUserName());
		if (!userRepresentation.isPresent()) {
			logger.warn("Cannot find user to update for username {}", user.getUserName());
			return;
		}
		userRepresentation.ifPresent(existing -> sendPasswordResetEmail(keycloak.get(), existing.getId()));
	}

	private UserRepresentation createUserRepresentation(User user) {
		UserRepresentation userRepresentation = new UserRepresentation();

		userRepresentation.setEnabled(user.isActive());
		userRepresentation.setUsername(user.getUserName());
		userRepresentation.setFirstName(user.getFirstName());
		userRepresentation.setLastName(user.getLastName());
		userRepresentation.setEmail(user.getUserEmail());
		userRepresentation.setRequiredActions(Arrays.asList("VERIFY_EMAIL", "UPDATE_PASSWORD"));
		setLanguage(userRepresentation, user.getLanguage());

		return userRepresentation;
	}

	private void updateUserRepresentation(UserRepresentation userRepresentation, User user) {
		userRepresentation.setEnabled(user.isActive());
		userRepresentation.setUsername(user.getUserName());
		userRepresentation.setFirstName(user.getFirstName());
		userRepresentation.setLastName(user.getLastName());
		userRepresentation.setEmail(user.getUserEmail());
		setLanguage(userRepresentation, user.getLanguage());
	}

	private String createUser(Keycloak keycloak, User user) {

		UserRepresentation userRepresentation = createUserRepresentation(user);
		Response response = keycloak.realm(REALM_NAME).users().create(userRepresentation);
		if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
			throw new WebApplicationException(response);
		}

		String[] pathSegments = response.getLocation().getPath().split("/");
		String userId = pathSegments[pathSegments.length - 1];

		boolean isRestUser = user.getUserRoles().stream().anyMatch(userRole -> UserRole.REST_USER == userRole);

		if (isRestUser) {
			addRealmRole(keycloak, userId, UserRole.REST_USER.name());
		}

		return userId;
	}

	private Optional<UserRepresentation> updateUser(Keycloak keycloak, User oldUser, User newUser) {
		Optional<UserRepresentation> userRepresentation = getUserByUsername(keycloak, newUser.getUserName());

		if (!userRepresentation.isPresent()) {
			logger.warn("Cannot find user to update for username {}", newUser.getUserName());
			return Optional.empty();
		}

		UserRepresentation newUserRepresentation = userRepresentation.get();
		updateUserRepresentation(newUserRepresentation, newUser);

		keycloak.realm(REALM_NAME).users().get(newUserRepresentation.getId()).update(newUserRepresentation);

		boolean newUserIsRestUser = newUser.getUserRoles().stream().anyMatch(userRole -> UserRole.REST_USER == userRole);
		boolean oldUserIsRestUser = oldUser.getUserRoles().stream().anyMatch(userRole -> UserRole.REST_USER == userRole);

		if (!oldUserIsRestUser && newUserIsRestUser) {
			addRealmRole(keycloak, newUserRepresentation.getId(), UserRole.REST_USER.name());
		} else if (oldUserIsRestUser && !newUserIsRestUser) {
			removeRealmRole(keycloak, newUserRepresentation.getId(), UserRole.REST_USER.name());
		}

		return Optional.of(newUserRepresentation);
	}

	private void addRealmRole(Keycloak keycloak, String userId, String role) {
		RoleRepresentation roleRepresentation = keycloak.realm(REALM_NAME).roles().get(role).toRepresentation();
		if (roleRepresentation != null) {
			keycloak.realm(REALM_NAME).users().get(userId).roles().realmLevel().add(Collections.singletonList(roleRepresentation));
		}
	}

	private void removeRealmRole(Keycloak keycloak, String userId, String role) {
		RoleRepresentation roleRepresentation = keycloak.realm(REALM_NAME).roles().get(role).toRepresentation();
		if (roleRepresentation != null) {
			keycloak.realm(REALM_NAME).users().get(userId).roles().realmLevel().remove(Collections.singletonList(roleRepresentation));
		}
	}

	private Optional<UserRepresentation> getUserByUsername(Keycloak keycloak, String username) {
		List<UserRepresentation> users = keycloak.realm("SORMAS").users().search(username, true);

		return users.stream().findFirst();
	}

	private void sendActivationEmail(Keycloak keycloak, String userId) {
		keycloak.realm(REALM_NAME).users().get(userId).sendVerifyEmail();
	}

	private void sendPasswordResetEmail(Keycloak keycloak, String userId) {
		keycloak.realm(REALM_NAME).users().get(userId).executeActionsEmail(Collections.singletonList("UPDATE_PASSWORD"));
	}

	private void setLanguage(UserRepresentation userRepresentation, Language language) {
		Map<String, List<String>> attributes = userRepresentation.getAttributes();
		if (attributes == null) {
			attributes = new HashMap<>();
		}

		if(language != null) {
			attributes.put(LOCALE, Collections.singletonList(language.getLocale().getLanguage()));
		}
	}

	private Optional<Keycloak> getKeycloak() {
		return Optional.ofNullable(keycloak);
	}
}
