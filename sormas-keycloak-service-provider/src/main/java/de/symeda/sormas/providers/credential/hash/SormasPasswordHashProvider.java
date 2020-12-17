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

package de.symeda.sormas.providers.credential.hash;

import de.symeda.sormas.api.utils.PasswordHelper;
import org.keycloak.Config;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.credential.hash.PasswordHashProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.PasswordPolicy;
import org.keycloak.models.credential.PasswordCredentialModel;

/**
 * Custom Password Hash Provider to be used as a backwards compatible mechanism for users migrated from the SORMAS default Authentication
 * Provider.
 *
 * @author Alex Vidrean
 * @since 14-Dec-20
 * @see <a href="https://www.keycloak.org/docs/latest/server_development/#_providers">Service Provider Interfaces (SPI)</a>
 */
public class SormasPasswordHashProvider implements PasswordHashProviderFactory, PasswordHashProvider {

	public static final String ID = "sormas-sha256";

	@Override
	public boolean policyCheck(PasswordPolicy policy, PasswordCredentialModel credential) {
		return credential.getPasswordCredentialData().getHashIterations() == policy.getHashIterations()
			&& ID.equals(credential.getPasswordCredentialData().getAlgorithm());
	}

	@Override
	public PasswordCredentialModel encodedCredential(String rawPassword, int iterations) {
		String salt = getSalt();
		String encodedPassword = encodePassword(rawPassword, salt);
		return PasswordCredentialModel.createFromValues(ID, salt.getBytes(), iterations, encodedPassword);
	}

	@Override
	public boolean verify(String rawPassword, PasswordCredentialModel credential) {
		return encodePassword(rawPassword, new String(credential.getPasswordSecretData().getSalt()))
			.equals(credential.getPasswordSecretData().getValue());
	}

	@Override
	public PasswordHashProvider create(KeycloakSession session) {
		return this;
	}

	@Override
	public void init(Config.Scope config) {
	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {
	}

	public void close() {
	}

	@Override
	public String getId() {
		return ID;
	}

	private String getSalt() {
		return PasswordHelper.createPass(16);
	}

	private String encodePassword(String rawPassword, String salt) {
		return PasswordHelper.encodePassword(rawPassword, salt);
	}

}
