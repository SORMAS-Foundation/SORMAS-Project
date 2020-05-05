package de.symeda.sormas.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class LanguageTest {

	@Test
	public void testFromLocaleString() throws Exception {
		assertThat(Language.fromLocaleString("en"), is(Language.EN));
		assertThat(Language.fromLocaleString("EN"), is(Language.EN));
		
		assertThat(Language.fromLocaleString("en-NG"), is(Language.EN_NG));
		assertThat(Language.fromLocaleString("EN_ng"), is(Language.EN_NG));
		
		assertThat(Language.fromLocaleString("en-GH"), is(Language.EN_GH));
		assertThat(Language.fromLocaleString("EN_gh"), is(Language.EN_GH));

		assertThat(Language.fromLocaleString("de-DE"), is(Language.DE_DE));
		assertThat(Language.fromLocaleString("DE_de"), is(Language.DE_DE));
		
		assertThat(Language.fromLocaleString("es-EC"), is(Language.ES_EC));

		assertThat(Language.fromLocaleString("fi-FI"), is(Language.FI_FI));
	
		// at the moment only defined fallbacks are supported

		assertThat(Language.fromLocaleString("de"), is(Language.DE_DE));
		assertThat(Language.fromLocaleString("DE"), is(Language.DE_DE));
		assertThat(Language.fromLocaleString("fi"), is(Language.FI_FI));
		assertThat(Language.fromLocaleString("es"), is(Language.ES_EC));
		
		//default fallback is en
		assertThat(Language.fromLocaleString(""), is(Language.EN));
		assertThat(Language.fromLocaleString("tlh"), is(Language.EN));
		assertThat(Language.fromLocaleString("en-GB"), is(Language.EN));
		assertThat(Language.fromLocaleString("de-CH"), is(Language.EN));


	}

}
