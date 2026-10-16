package de.symeda.sormas.api.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Test;

public class OidcCallerPrincipalTest {

	@Test
	public void testSerializationPreservesCallerAndToken() throws Exception {
		OidcCallerPrincipal principal = new OidcCallerPrincipal("test-user", "test-access-token");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
			output.writeObject(principal);
		}
		try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
			OidcCallerPrincipal restored = (OidcCallerPrincipal) input.readObject();
			assertEquals(principal.getName(), restored.getName());
			assertEquals(principal.getAccessToken(), restored.getAccessToken());
		}
	}

	@Test
	public void testToStringDoesNotExposeToken() {
		OidcCallerPrincipal principal = new OidcCallerPrincipal("test-user", "test-access-token");
		assertFalse(principal.toString().contains(principal.getAccessToken()));
	}
}
