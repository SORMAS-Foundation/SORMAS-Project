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

package de.symeda.sormas.ui.security.config;

import fish.payara.security.annotations.ClaimsDefinition;
import fish.payara.security.annotations.LogoutDefinition;
import fish.payara.security.annotations.OpenIdAuthenticationDefinition;
import fish.payara.security.annotations.OpenIdProviderMetadata;
import fish.payara.security.openid.api.DisplayType;
import fish.payara.security.openid.api.OpenIdConstant;
import fish.payara.security.openid.api.PromptType;

import java.lang.annotation.Annotation;

/**
 * Default implementation of  {@link OpenIdAuthenticationDefinition} to allow inline declaration.
 * <p/>
 * Configurations to be provided trough Payara MP.
 *
 * @author Alex Vidrean
 * @since 12-Aug-20
 */
public class DefaultOpenIdAuthenticationDefinition implements OpenIdAuthenticationDefinition {

	@Override
	public String providerURI() {
		return "";
	}

	@Override
	public OpenIdProviderMetadata providerMetadata() {
		return new OpenIdProviderMetadata() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return OpenIdProviderMetadata.class;
			}

			@Override
			public String issuer() {
				return "";
			}

			@Override
			public String authorizationEndpoint() {
				return "";
			}

			@Override
			public String tokenEndpoint() {
				return "";
			}

			@Override
			public String userinfoEndpoint() {
				return "";
			}

			@Override
			public String endSessionEndpoint() {
				return "";
			}

			@Override
			public String jwksURI() {
				return "";
			}

			@Override
			public String[] scopesSupported() {
				return new String[0];
			}

			@Override
			public String[] responseTypesSupported() {
				return new String[0];
			}

			@Override
			public String[] subjectTypesSupported() {
				return new String[0];
			}

			@Override
			public String[] idTokenSigningAlgValuesSupported() {
				return new String[0];
			}

			@Override
			public String[] idTokenEncryptionAlgValuesSupported() {
				return new String[0];
			}

			@Override
			public String[] idTokenEncryptionEncValuesSupported() {
				return new String[0];
			}

			@Override
			public String[] claimsSupported() {
				return new String[0];
			}

			@Override
			public boolean disableScopeValidation() {
				return false;
			}
		};
	}

	@Override
	public ClaimsDefinition claimsDefinition() {
		return new ClaimsDefinition() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return ClaimsDefinition.class;
			}

			@Override
			public String callerNameClaim() {
				return OpenIdConstant.PREFERRED_USERNAME;
			}

			@Override
			public String callerGroupsClaim() {
				return OpenIdConstant.GROUPS;
			}
		};
	}

	@Override
	public LogoutDefinition logout() {
		//TODO: investigate how to configure logout for SORMAS when token expires
		return new LogoutDefinition() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return LogoutDefinition.class;
			}

			@Override
			public boolean notifyProvider() {
				return false;
			}

			@Override
			public String redirectURI() {
				return "";
			}

			@Override
			public boolean accessTokenExpiry() {
				return false;
			}

			@Override
			public boolean identityTokenExpiry() {
				return false;
			}
		};
	}

	@Override
	public String clientId() {
		return "";
	}

	@Override
	public String clientSecret() {
		return "";
	}

	@Override
	public String redirectURI() {
		return "${baseURL}/Callback";
	}

	@Override
	public String[] scope() {
		return new String[] { OpenIdConstant.OPENID_SCOPE };
	}

	@Override
	public String responseType() {
		return "code";
	}

	@Override
	public String responseMode() {
		return "";
	}

	@Override
	public PromptType[] prompt() {
		return new PromptType[] {};
	}

	@Override
	public DisplayType display() {
		return DisplayType.PAGE;
	}

	@Override
	public boolean useNonce() {
		return true;
	}

	@Override
	public boolean useSession() {
		return true;
	}

	@Override
	public String[] extraParameters() {
		return new String[] {};
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return fish.payara.security.annotations.OpenIdAuthenticationDefinition.class;
	}

	@Override
	public int jwksConnectTimeout() {
		return 500;
	}

	@Override
	public int jwksReadTimeout() {
		return 500;
	}

	@Override
	public boolean tokenAutoRefresh() {
		return false;
	}

	@Override
	public int tokenMinValidity() {
		return 10 * 1000;
	}

	@Override
	public boolean userClaimsFromIDToken() {
		return false;
	}
}
