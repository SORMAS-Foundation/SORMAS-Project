package de.symeda.sormas.api.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.ApplicableToPathogenTests;
import de.symeda.sormas.api.utils.Diseases;

public class SyphilisSerologyMethodTest {

	private static List<PathogenTestType> parentTypesOf(SyphilisSerologyMethod method) {
		try {
			ApplicableToPathogenTests annotation =
				SyphilisSerologyMethod.class.getField(method.name()).getAnnotation(ApplicableToPathogenTests.class);
			return Arrays.asList(annotation.value());
		} catch (NoSuchFieldException e) {
			throw new AssertionError(method.name() + " must declare @ApplicableToPathogenTests", e);
		}
	}

	@Test
	public void nonTreponemalMethodsBelongToTheNonTreponemalType() {
		for (SyphilisSerologyMethod method : List.of(SyphilisSerologyMethod.VDRL, SyphilisSerologyMethod.RPR, SyphilisSerologyMethod.TRUST)) {
			assertThat(method.name(), parentTypesOf(method), containsInAnyOrder(PathogenTestType.NON_TREPONEMAL_TESTS));
		}
	}

	@Test
	public void treponemalMethodsBelongToTheTreponemalType() {
		List<SyphilisSerologyMethod> treponemal = List.of(
			SyphilisSerologyMethod.TPPA_TPHA,
			SyphilisSerologyMethod.FTA_ABS,
			SyphilisSerologyMethod.EIA,
			SyphilisSerologyMethod.CLIA,
			SyphilisSerologyMethod.RDT);
		for (SyphilisSerologyMethod method : treponemal) {
			assertThat(method.name(), parentTypesOf(method), containsInAnyOrder(PathogenTestType.TREPONEMAL_TESTS));
		}
	}

	@Test
	public void otherBelongsToBothTypes() {
		assertThat(
			parentTypesOf(SyphilisSerologyMethod.OTHER),
			containsInAnyOrder(PathogenTestType.NON_TREPONEMAL_TESTS, PathogenTestType.TREPONEMAL_TESTS));
	}

	@Test
	public void everyMethodIsScopedToSyphilisOnly() {
		assertThat(
			Diseases.DiseasesConfiguration.getVisibleValues(SyphilisSerologyMethod.class, Disease.SYPHILIS),
			hasSize(SyphilisSerologyMethod.values().length));
		assertThat(Diseases.DiseasesConfiguration.getVisibleValues(SyphilisSerologyMethod.class, Disease.MEASLES), is(empty()));
	}

	@Test
	public void everyMethodRendersItsConfiguredCaption() {
		assertEquals("VDRL", SyphilisSerologyMethod.VDRL.toString());
		assertEquals("RPR", SyphilisSerologyMethod.RPR.toString());
		assertEquals("TRUST", SyphilisSerologyMethod.TRUST.toString());
		assertEquals("TPPA / TPHA", SyphilisSerologyMethod.TPPA_TPHA.toString());
		assertEquals("FTA-ABS", SyphilisSerologyMethod.FTA_ABS.toString());
		assertEquals("EIA", SyphilisSerologyMethod.EIA.toString());
		assertEquals("CLIA", SyphilisSerologyMethod.CLIA.toString());
		assertEquals("Rapid test (RDT)", SyphilisSerologyMethod.RDT.toString());
		assertEquals("Other", SyphilisSerologyMethod.OTHER.toString());
	}
}
