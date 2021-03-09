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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.JsonPath;

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

    private static final String DEFAULT_USER_ROLE = UserRole._USER;

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
     * @param userCreateEvent a real / mock user created event
     * @see de.symeda.sormas.backend.common.StartupShutdownService
     */
    public void handleUserCreateEvent(@Observes UserCreateEvent userCreateEvent) {
        Optional<Keycloak> keycloak = getKeycloak();
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
     * @param userUpdateEvent contains the old user and the new user information
     */
    public void handleUserUpdateEvent(@Observes UserUpdateEvent userUpdateEvent) {
        Optional<Keycloak> keycloak = getKeycloak();
        if (!keycloak.isPresent()) {
            logger.warn("Cannot obtain keycloak instance. Will not update user in keycloak");
            return;
        }

        User newUser = userUpdateEvent.getNewUser();

        try {
            Optional<UserRepresentation> userRepresentation = updateUser(keycloak.get(), newUser);
            if (!userRepresentation.isPresent()) {
                logger.debug("Cannot find user in Keycloak. Will try to create it");
                createUser(keycloak.get(), newUser);
            }
        } catch (Exception e) {
            userUpdateEvent.getExceptionCallback().accept(e.getMessage());
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
     * @param passwordResetEvent user and the plain text password which was set
     */
    public void handlePasswordResetEvent(@Observes PasswordResetEvent passwordResetEvent) {
        Optional<Keycloak> keycloak = getKeycloak();
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

    private void updateUserRepresentation(UserRepresentation userRepresentation, User user) {
        userRepresentation.setEnabled(user.isActive());
        userRepresentation.setUsername(user.getUserName());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEmail(user.getUserEmail());
        setLanguage(userRepresentation, user.getLanguage());
    }

    private String createUser(Keycloak keycloak, User user) {
        UserRepresentation userRepresentation = createUserRepresentation(user, user.getPassword());
        Response response = keycloak.realm(REALM_NAME).users().create(userRepresentation);
        if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
            throw new WebApplicationException(response);
        }

        String[] pathSegments = response.getLocation().getPath().split("/");
        String userId = pathSegments[pathSegments.length - 1];

        try {
            ensureRoles(keycloak, userId, user.getUserRoles());
        } catch (Exception e) {
            logger.warn("Cannot set the user roles property, will remove the user");
            keycloak.realm(REALM_NAME).users().delete(userId);
            throw e;
        }

        return userId;
    }

    private Optional<UserRepresentation> updateUser(Keycloak keycloak, User newUser) {
        Optional<UserRepresentation> userRepresentation = getUserByUsername(keycloak, newUser.getUserName());

        if (!userRepresentation.isPresent()) {
            logger.warn("Cannot find user to update for username {}", newUser.getUserName());
            return Optional.empty();
        }

        UserRepresentation newUserRepresentation = userRepresentation.get();
        ensureRoles(keycloak, newUserRepresentation.getId(), newUser.getUserRoles());

        updateUserRepresentation(newUserRepresentation, newUser);
        keycloak.realm(REALM_NAME).users().get(newUserRepresentation.getId()).update(newUserRepresentation);

        return Optional.of(newUserRepresentation);
    }

    private void ensureRoles(Keycloak keycloak, String userRepresentationId, Set<UserRole> userRoles) {
        RealmResource realm = keycloak.realm(REALM_NAME);

        Map<String, RoleRepresentation> keycloakRoles = getRealmRoles(keycloak);
        UserResource userResource = realm.users().get(userRepresentationId);
        Set<String> sormasRoles = Arrays.stream(UserRole.values()).map(Enum::name).collect(Collectors.toSet());

        List<RoleRepresentation> oldUserRoles = userResource.roles().realmLevel().listAll()
                .stream().filter(role -> sormasRoles.contains(role.getName())).collect(Collectors.toList());

        List<RoleRepresentation> newUserRoles = userRoles
                .stream()
                .map(userRole -> keycloakRoles.get(userRole.name()))
                .filter(Objects::nonNull).collect(Collectors.toList());

        if (keycloakRoles.containsKey(DEFAULT_USER_ROLE)) {
            newUserRoles.add(keycloakRoles.get(DEFAULT_USER_ROLE));
        }

        if (CollectionUtils.isNotEmpty(oldUserRoles)) {
            userResource.roles().realmLevel().remove(oldUserRoles);
        }
        userResource.roles().realmLevel().add(newUserRoles);
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

    private Map<String, RoleRepresentation> getRealmRoles(Keycloak keycloak) {
        return keycloak.realm(REALM_NAME).roles().list().stream().collect(Collectors.toMap(RoleRepresentation::getName, Function.identity()));
    }

    private Optional<Keycloak> getKeycloak() {
        return Optional.ofNullable(keycloak);
    }

}
