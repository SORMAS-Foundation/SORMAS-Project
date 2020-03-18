package de.symeda.sormas.backend.geocoding;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class GeocodingFacadeEjbTest {

	@Test
	public void testEscape() throws Exception {
		
		assertThat(GeocodingFacadeEjb.escape("Test"), is("Test"));

		assertThat(GeocodingFacadeEjb.escape("Halle (Saale)"), is("Halle \\(Saale\\)"));
	}

}
