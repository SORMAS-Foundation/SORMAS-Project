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
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.user.event.PasswordResetEvent;
import de.symeda.sormas.backend.user.event.UserCreateEvent;
import de.symeda.sormas.backend.user.event.UserUpdateEvent;

/**
 * @author Alex Vidrean
 * @since 15-Aug-20
 */
@Stateless
@LocalBean
public class KeycloakService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	private static final String OIDC_REALM = "realm";
	private static final String OIDC_SERVER_URL = "auth-server-url";
	private static final String OIDC_SECRET = "credentials.secret";

	private static final String REALM_NAME = "SORMAS";

	private static final String ACTION_UPDATE_PASSWORD = "UPDATE_PASSWORD";
	private static final String ACTION_VERIFY_EMAIL = "VERIFY_EMAIL";

	private static final List<UserRole> REST_ROLES = Arrays.asList(UserRole.REST_USER, UserRole.REST_EXTERNAL_VISITS_USER);

	private Keycloak keycloak = null;

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

		keycloak = KeycloakBuilder.builder()
			.realm(JsonPath.read(keycloakJsonConfig, OIDC_REALM))
			.serverUrl(JsonPath.read(keycloakJsonConfig, OIDC_SERVER_URL))
			.clientId("sormas-backend")
			.clientSecret(JsonPath.read(keycloakJsonConfig, OIDC_SECRET))
			.grantType(OAuth2Constants.CLIENT_CREDENTIALS)
			.build();

	}

	public void handleUserCreateEvent(@Observes UserCreateEvent userCreateEvent) {
		Optional<Keycloak> keycloak = getKeycloak();
		if (!keycloak.isPresent()) {
			logger.warn("Cannot obtain keycloak instance. Will not create user in keycloak");
			return;
		}

		User user = userCreateEvent.getUser();
		String userId = createUser(keycloak.get(), user, null);
		if (StringUtils.isNotBlank(user.getUserEmail())) {
			sendActivationEmail(keycloak.get(), userId);
		}
	}

	public void handleUserUpdateEvent(@Observes UserUpdateEvent userUpdateEvent) {
		Optional<Keycloak> keycloak = getKeycloak();
		if (!keycloak.isPresent()) {
			logger.warn("Cannot obtain keycloak instance. Will not update user in keycloak");
			return;
		}

		User newUser = userUpdateEvent.getNewUser();
		User oldUser = userUpdateEvent.getOldUser();

		try {
			Optional<UserRepresentation> userRepresentation = updateUser(keycloak.get(), oldUser, newUser);
			if (!userRepresentation.isPresent()) {
				logger.debug("Cannot find user in Keycloak. Will try to create it");
				createUser(keycloak.get(), newUser, newUser.getPassword());
			}
		} catch (Exception e) {
			userUpdateEvent.getExceptionCallback().accept(e.getMessage());
			logger.error(e.getMessage(), e);
		}
	}

	public void handlePasswordResetEvent(@Observes PasswordResetEvent passwordResetEvent) {
		Optional<Keycloak> keycloak = getKeycloak();
		if (!keycloak.isPresent()) {
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

	private UserRepresentation createUserRepresentation(User user, String hashedPassword) {
		UserRepresentation userRepresentation = new UserRepresentation();

		userRepresentation.setEnabled(user.isActive());
		userRepresentation.setUsername(user.getUserName());
		userRepresentation.setFirstName(user.getFirstName());
		userRepresentation.setLastName(user.getLastName());
		setLanguage(userRepresentation, user.getLanguage());
		if(StringUtils.isNotBlank(hashedPassword)) {
			setCredentials(userRepresentation, hashedPassword, user.getSeed());
		}

		if (StringUtils.isNotBlank(user.getUserEmail())) {
			userRepresentation.setEmail(user.getUserEmail());
			userRepresentation.setRequiredActions(Arrays.asList(ACTION_VERIFY_EMAIL, ACTION_UPDATE_PASSWORD));
		}

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

	private String createUser(Keycloak keycloak, User user, String presetPassword) {
		UserRepresentation userRepresentation = createUserRepresentation(user, presetPassword);
		Response response = keycloak.realm(REALM_NAME).users().create(userRepresentation);
		if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
			throw new WebApplicationException(response);
		}

		String[] pathSegments = response.getLocation().getPath().split("/");
		String userId = pathSegments[pathSegments.length - 1];

		try {
			ensureRestRoles(keycloak, userId, user.getUserRoles(), Collections.emptySet());
		} catch (Exception e) {
			logger.warn("Cannot set the user roles property, will remove the user");
			keycloak.realm(REALM_NAME).users().delete(userId);
			throw e;
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
		ensureRestRoles(keycloak, newUserRepresentation.getId(), newUser.getUserRoles(), oldUser.getUserRoles());

		updateUserRepresentation(newUserRepresentation, newUser);
		keycloak.realm(REALM_NAME).users().get(newUserRepresentation.getId()).update(newUserRepresentation);

		return Optional.of(newUserRepresentation);
	}

	private void ensureRestRoles(Keycloak keycloak, String userRepresentationId, Set<UserRole> newUserRoles, Set<UserRole> oldUserRoles) {
		Set<UserRole> newRestRoles = newUserRoles.stream().filter(REST_ROLES::contains).collect(Collectors.toSet());
		Set<UserRole> oldRestRoles = oldUserRoles.stream().filter(REST_ROLES::contains).collect(Collectors.toSet());

		List<RoleRepresentation> rolesToAdd = newRestRoles.stream()
			.filter(restRole -> !oldRestRoles.contains(restRole))
			.map(restRole -> keycloak.realm(REALM_NAME).roles().get(restRole.name()).toRepresentation())
			.collect(Collectors.toList());

		List<RoleRepresentation> rolesToRemove = oldRestRoles.stream()
			.filter(restRole -> !newRestRoles.contains(restRole))
			.map(restRole -> keycloak.realm(REALM_NAME).roles().get(restRole.name()).toRepresentation())
			.collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(rolesToAdd)) {
			keycloak.realm(REALM_NAME).users().get(userRepresentationId).roles().realmLevel().add(rolesToAdd);
		}
		if (CollectionUtils.isNotEmpty(rolesToRemove)) {
			keycloak.realm(REALM_NAME).users().get(userRepresentationId).roles().realmLevel().remove(rolesToRemove);
		}
	}

	private Optional<UserRepresentation> getUserByUsername(Keycloak keycloak, String username) {
		List<UserRepresentation> users = keycloak.realm(REALM_NAME).users().search(username, true);

		return users.stream().findFirst();
	}

	private void sendActivationEmail(Keycloak keycloak, String userId) {
		keycloak.realm(REALM_NAME).users().get(userId).sendVerifyEmail();
	}

	private void sendPasswordResetEmail(Keycloak keycloak, String userId) {
		keycloak.realm(REALM_NAME).users().get(userId).executeActionsEmail(Collections.singletonList(ACTION_UPDATE_PASSWORD));
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
		credential.setTemporary(false);
		credential.setSecretData(secretData.build().toString());
		credential.setCredentialData(credentialData.build().toString());
		userRepresentation.setCredentials(singletonList(credential));
	}

	private Optional<Keycloak> getKeycloak() {
		return Optional.ofNullable(keycloak);
	}

}
