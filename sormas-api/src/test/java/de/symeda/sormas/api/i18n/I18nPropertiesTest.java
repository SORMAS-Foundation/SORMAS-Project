package de.symeda.sormas.api.i18n;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.ResourceBundle;
import java.util.Set;

import org.junit.Test;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties.UTF8Control;

public class I18nPropertiesTest {

	@Test
	public void testNormalizeCountryCode() throws Exception {
		assertThat(I18nProperties.UTF8Control.normalizeCountryCode("captions_en"), is("captions_en"));
		assertThat(I18nProperties.UTF8Control.normalizeCountryCode("captions_en-ng"), is("captions_en-NG"));
	}
	
	@Test
	public void testUTF8Control() throws Exception {
		
		ResourceBundle deDeBundle = new UTF8Control().newBundle("captions", Language.DE.getLocaleWithCountryCode(), "java.properties", getClass().getClassLoader(), true);
		Set<String> deDeKeys = deDeBundle.keySet();
		assertThat(deDeKeys, not(empty()));
	}
}
