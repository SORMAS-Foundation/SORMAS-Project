package de.symeda.sormas.api.user;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.security.enterprise.CallerPrincipal;

/**
 * Authenticated caller and the access token for this authentication session.
 * Must not be persisted with user data or stored in the user cache.
 */
public final class OidcCallerPrincipal extends CallerPrincipal implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String accessToken;

	public OidcCallerPrincipal(String username, String accessToken) {
		super(username);
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	// CallerPrincipal is not serializable and has no no-argument constructor.
	private Object writeReplace() {
		return new SerializationProxy(getName(), accessToken);
	}

	private void readObject(ObjectInputStream stream) throws InvalidObjectException {
		throw new InvalidObjectException("Serialization proxy required");
	}

	private static final class SerializationProxy implements Serializable {

		private static final long serialVersionUID = 1L;

		private final String username;
		private final String accessToken;

		private SerializationProxy(String username, String accessToken) {
			this.username = username;
			this.accessToken = accessToken;
		}

		private Object readResolve() {
			return new OidcCallerPrincipal(username, accessToken);
		}
	}
}
