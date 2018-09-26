package de.symeda.sormas.api.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class InfoProviderTest {

	@Before
	public void prepareTest() {
		try {
			Field instance = InfoProvider.class.getDeclaredField("instance");
			instance.setAccessible(true);
			instance.set(null, spy(InfoProvider.class));
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testIsCompatibleToApiString() throws Exception {

		Mockito.when(InfoProvider.get().getVersion()).thenReturn("0.7.0");
		Mockito.when(InfoProvider.get().getMinimumRequiredVersion()).thenReturn("0.5.0");
		
		// testMatchingVersionCompatibility
		assertEquals(CompatibilityCheckResponse.COMPATIBLE, InfoProvider.get().isCompatibleToApi("0.5.0"));
		assertEquals(CompatibilityCheckResponse.COMPATIBLE, InfoProvider.get().isCompatibleToApi("0.7.0"));

		// testHotfixVersionCompatibility
		assertEquals(CompatibilityCheckResponse.COMPATIBLE, InfoProvider.get().isCompatibleToApi("0.5.99"));

		// testTooOldVersionIncompatibility
		assertEquals(CompatibilityCheckResponse.TOO_OLD, InfoProvider.get().isCompatibleToApi("0.4.0"));
		assertEquals(CompatibilityCheckResponse.TOO_OLD, InfoProvider.get().isCompatibleToApi("0.0.7"));

		// testTooNewVersionIncompatibility
		assertEquals(CompatibilityCheckResponse.TOO_NEW, InfoProvider.get().isCompatibleToApi("0.7.1"));
		assertEquals(CompatibilityCheckResponse.TOO_NEW, InfoProvider.get().isCompatibleToApi("0.8.0"));
		assertEquals(CompatibilityCheckResponse.TOO_NEW, InfoProvider.get().isCompatibleToApi("1.0.0"));

		// testMalformedVersionReturnsError
		try { 
			InfoProvider.get().isCompatibleToApi("1.0");
			fail();
		}
		catch (IllegalArgumentException e) { }
		try { 
			InfoProvider.get().isCompatibleToApi("wrong.format.test");
			fail();
		}
		catch (IllegalArgumentException e) { }
	}
}
