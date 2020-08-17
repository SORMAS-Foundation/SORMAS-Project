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

import com.auth0.jwt.internal.org.apache.commons.lang3.SerializationUtils;
import de.symeda.sormas.backend.user.event.PasswordResetEvent;
import de.symeda.sormas.backend.user.event.UserCreateEvent;
import de.symeda.sormas.backend.util.PasswordHelper;
import org.apache.commons.collections.CollectionUtils;
import org.glassfish.soteria.WrappingCallerPrincipal;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Alex Vidrean
 * @since 15-Aug-20
 */
@Stateless
@LocalBean
public class KeycloakService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private SessionContext context;

	public Response createUser(Keycloak instance, User user) {
		UserRepresentation userRepresentation = toUserRepresentation(user);
		return instance.realm("SORMAS").users().create(userRepresentation);
	}

	public void sendActivationEmail(Keycloak instance, String userId) {
		instance.realm("SORMAS").users().get(userId).sendVerifyEmail();
	}

	public void sendPasswordResetEmail(Keycloak instance, String username) {
		List<UserRepresentation> users = instance.realm("SORMAS").users().search(username, true);
		if(CollectionUtils.isEmpty(users)) {
			throw new IllegalArgumentException("Username is invalid");
		}

		if (users.size() > 1) {
			throw new IllegalArgumentException("Username is used by multiple users");
		}

		String userId = users.get(0).getId();

		instance.realm("SORMAS").users().get(userId).executeActionsEmail(Collections.singletonList("UPDATE_PASSWORD"));
	}

	public void handleUserCreateEvent(@Observes UserCreateEvent userCreateEvent) {
		Optional<Keycloak> instance = getInstance();
		if (!instance.isPresent()) {
			logger.debug("Keycloak not recognized for current context, will skip keycloak user create");
			return;
		}

		User user = userCreateEvent.getUser();
		Response response = createUser(instance.get(), user);
		if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
			throw new WebApplicationException(response);
		}

		sendActivationEmail(instance.get(), extractUserId(response));
	}

	public void handlePasswordResetEvent(@Observes PasswordResetEvent passwordResetEvent) {
		Optional<Keycloak> instance = getInstance();
		if (!instance.isPresent()) {
			logger.debug("Keycloak not recognized for current context, will skip keycloak user create");
			return;
		}

		User user = passwordResetEvent.getUser();
		sendPasswordResetEmail(instance.get(), user.getUserName());

	}

	private Optional<Keycloak> getInstance() {
		Principal principal = context.getCallerPrincipal();
		if (!(principal instanceof WrappingCallerPrincipal) || !(((WrappingCallerPrincipal) principal).getWrapped() instanceof Serializable)) {
			logger.debug("Cannot validate principle as KeycloakPrincipal");
			return Optional.empty();
		}
		byte[] serialized = SerializationUtils.serialize((Serializable) ((WrappingCallerPrincipal) principal).getWrapped());
		KeycloakPrincipal keycloakPrincipal = SerializationUtils.deserialize(serialized);

		return Optional.of(Keycloak.getInstance("http://localhost:7080/auth",
			"SORMAS",
			"sormas-ui",
			keycloakPrincipal.getKeycloakSecurityContext().getTokenString()));
	}

	private UserRepresentation toUserRepresentation(User user) {
		UserRepresentation userRepresentation = new UserRepresentation();

		userRepresentation.setEnabled(true);
		userRepresentation.setUsername(user.getUserName());
		userRepresentation.setFirstName(user.getFirstName());
		userRepresentation.setLastName(user.getLastName());
		userRepresentation.setEmail(user.getUserEmail());
		userRepresentation.setRequiredActions(Arrays.asList("VERIFY_EMAIL", "UPDATE_PASSWORD"));

		return userRepresentation;
	}

	private String extractUserId(Response response) {
		String[] pathSegments = response.getLocation().getPath().split("/");
		return pathSegments[pathSegments.length - 1];
	}

}
