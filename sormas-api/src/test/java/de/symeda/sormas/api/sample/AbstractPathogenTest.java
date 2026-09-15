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

public abstract class AbstractPathogenTest extends AbstractUnitTest {

	protected final DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(getDisease());

	abstract protected PathogenTestType[] getAllowedPathogenTests();

	abstract protected Disease getDisease();

	@Test
	void requiredTestTypesAreSelectable() {
		Assertions.assertAll(Arrays.stream(getAllowedPathogenTests()).map((type) -> (Executable) () -> {
			assertTrue(checker.isVisible(PathogenTestType.class, type.name()), type + " must be visible for " + getDisease());
			assertTrue(PathogenTestType.isSelectableForNewTests(type), type + " must be selectable for new tests");
		}).collect(Collectors.toList()));
	}

	@Test
	void onlyRequiredTestTypesAreSelectable() {
		PathogenTestType[] forbiddenPathogenTestTypes = ArrayUtils.removeElements(PathogenTestType.values(), getAllowedPathogenTests());

		Assertions.assertAll(Arrays.stream(forbiddenPathogenTestTypes).map((type) -> (Executable) () -> {
			assertFalse(checker.isVisible(PathogenTestType.class, type.name()), type + " must not be visible for " + getDisease());
		}).collect(Collectors.toList()));
	}
}
