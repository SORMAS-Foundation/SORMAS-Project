package de.symeda.sormas.api.i18n;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties.UTF8Control;

public class I18nPropertiesTest {

	@Test
	public void testControlToBundleName() {

		UTF8Control control = new UTF8Control();
		assertThat(control.toBundleName("captions", Locale.ROOT), is("captions"));
		assertThat(control.toBundleName("captions", new Locale("en")), is("captions_en"));
		assertThat(control.toBundleName("captions", new Locale("en", "NG")), is("captions_en-NG"));
	}

	@Test
	public void testUTF8Control() throws IllegalAccessException, InstantiationException, IOException {

		UTF8Control control = new UTF8Control();

		{
			Locale locale = Locale.ROOT;
			ResourceBundle bundle = control.newBundle("captions", locale, "java.properties", getClass().getClassLoader(), true);
			Set<String> keys = bundle.keySet();
			assertThat("Root Language", keys, not(empty()));
		}

		for (Language l : Language.values()) {
			ResourceBundle bundle = control.newBundle("captions", l.getLocale(), "java.properties", getClass().getClassLoader(), true);

			Set<String> keys = bundle.keySet();
			if (l.equals(Language.EN)) {
				assertThat(l.name(), keys, empty());
			} else {
				assertThat(l.name(), keys, not(empty()));
			}
		}
	}

	@Test
	public void testInfrastructureCaptions() {

		// test usage of (sub)continent-names containing brackets and spaces
		Language currentLanguage = I18nProperties.getUserLanguage();
		I18nProperties.setUserLanguage(Language.DE);
		assertThat(I18nProperties.getContinentName("Australia (Continent)"), is("Australien (Kontinent)"));
		assertThat(I18nProperties.getSubcontinentName("Australia (Subcontinent)"), is("Australien (Subkontinent)"));
		assertThat(I18nProperties.getContinentName("CustomContinent (Atlantis)"), is("CustomContinent (Atlantis)"));
		assertThat(I18nProperties.getSubcontinentName("CustomSubcontinent (Atlantis)"), is("CustomSubcontinent (Atlantis)"));
		I18nProperties.setUserLanguage(currentLanguage);
	}
}
