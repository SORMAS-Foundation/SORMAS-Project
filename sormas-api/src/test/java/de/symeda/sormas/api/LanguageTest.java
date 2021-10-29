package de.symeda.sormas.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class LanguageTest {

	@Test
	public void testFromLocaleString() {

		assertThat(Language.fromLocaleString("en"), is(Language.EN));
		assertThat(Language.fromLocaleString("EN"), is(Language.EN));

		assertThat(Language.fromLocaleString("es-EC"), is(Language.ES_EC));


		assertThat(Language.fromLocaleString("fr-FR"), is(Language.FR));
		assertThat(Language.fromLocaleString("FR_fr"), is(Language.FR));

		// at the moment only defined fallbacks are supported
		assertThat(Language.fromLocaleString("es"), is(Language.ES_EC));
		assertThat(Language.fromLocaleString("fr"), is(Language.FR));

		//default fallback is en
		assertThat(Language.fromLocaleString(""), is(Language.EN));
		assertThat(Language.fromLocaleString("tlh"), is(Language.EN));
		assertThat(Language.fromLocaleString("en-GB"), is(Language.EN));
		assertThat(Language.fromLocaleString("de-AU"), is(Language.EN));
	}
}
