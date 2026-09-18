package de.symeda.sormas.api.sample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import de.symeda.sormas.api.AbstractUnitTest;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;

public abstract class AbstractSampleMaterialTest extends AbstractUnitTest {

	protected final DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(getDisease());

	abstract protected SampleMaterial[] getAllowedSampleMaterial();

	abstract protected Disease getDisease();

	@Test
	void requiredSampleMaterialsAreVisible() {
		Assertions.assertAll(Arrays.stream(getAllowedSampleMaterial()).map((type) -> (Executable) () -> {
			assertTrue(checker.isVisible(SampleMaterial.class, type.name()), toReadableString(type) + " must be visible for " + getDisease());
		}).collect(Collectors.toList()));
	}

	@Test
	void onlyRequiredSampleMaterialsAreVisible() {
		SampleMaterial[] forbiddenSampleMaterials = ArrayUtils.removeElements(SampleMaterial.values(), getAllowedSampleMaterial());

		Assertions.assertAll(Arrays.stream(forbiddenSampleMaterials).map((type) -> (Executable) () -> {
			assertFalse(checker.isVisible(PathogenTestType.class, type.name()), toReadableString(type) + " must not be visible for " + getDisease());
		}).collect(Collectors.toList()));
	}

	private String toReadableString(SampleMaterial type) {
		return String.format("[%s] '%s'", type.name(), type);
	}
}
