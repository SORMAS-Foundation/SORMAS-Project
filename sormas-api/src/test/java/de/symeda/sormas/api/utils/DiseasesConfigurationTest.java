package de.symeda.sormas.api.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;

public class DiseasesConfigurationTest {

	public class TestClass {

		@SuppressWarnings("unused")
		private String testNone;

		@Diseases()
		private String testEmpty;
		
		@Diseases(Disease.EVD)
		private String testOne;

		@Diseases({Disease.CHOLERA, Disease.CSM})
		private String testMultiple;

	}

	@Test
	public void testIsMissing() throws Exception {

		assertTrue(DiseasesConfiguration.isMissing(TestClass.class, "testNone", Disease.AVIAN_INFLUENCA));
		assertFalse(DiseasesConfiguration.isMissing(TestClass.class, "testEmpty", Disease.AVIAN_INFLUENCA));
		assertFalse(DiseasesConfiguration.isMissing(TestClass.class, "testOne", Disease.AVIAN_INFLUENCA));
		assertFalse(DiseasesConfiguration.isMissing(TestClass.class, "testMultiple", Disease.AVIAN_INFLUENCA));
	}

	@Test
	public void testIsDefined() throws Exception {

		assertFalse(DiseasesConfiguration.isDefined(TestClass.class, "testNone", Disease.AVIAN_INFLUENCA));
		assertFalse(DiseasesConfiguration.isDefined(TestClass.class, "testEmpty", Disease.AVIAN_INFLUENCA));
		assertFalse(DiseasesConfiguration.isDefined(TestClass.class, "testOne", Disease.AVIAN_INFLUENCA));
		assertTrue(DiseasesConfiguration.isDefined(TestClass.class, "testOne", Disease.EVD));
		assertFalse(DiseasesConfiguration.isDefined(TestClass.class, "testMultiple", Disease.AVIAN_INFLUENCA));
		assertTrue(DiseasesConfiguration.isDefined(TestClass.class, "testMultiple", Disease.CHOLERA));
		assertTrue(DiseasesConfiguration.isDefined(TestClass.class, "testMultiple", Disease.CSM));
	}

}
