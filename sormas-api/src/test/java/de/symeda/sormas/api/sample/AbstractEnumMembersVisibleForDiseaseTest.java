package de.symeda.sormas.api.sample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;

public abstract class AbstractEnumMembersVisibleForDiseaseTest<T extends Enum<?>> {

	protected final DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(getDisease());

	abstract protected T[] getAllowedMembers();

	abstract protected Disease getDisease();

	abstract protected Class<T> getEnumClass();

	@Test
	void requiredMemberIsVisible() {
		Assertions.assertAll(Arrays.stream(getAllowedMembers()).map((type) -> (Executable) () -> {
			assertTrue(checker.isVisible(getEnumClass(), type.name()), toReadableString(type) + " must be visible for " + getDisease());
		}).collect(Collectors.toList()));
	}

	@Test
	void onlyRequiredMembersAreVisible() {
		T[] forbiddenSampleMaterials = ArrayUtils.removeElements(getEnumClass().getEnumConstants(), getAllowedMembers());

		Assertions.assertAll(Arrays.stream(forbiddenSampleMaterials).map((type) -> (Executable) () -> {
			assertFalse(checker.isVisible(getEnumClass(), type.name()), toReadableString(type) + " must not be visible for " + getDisease());
		}).collect(Collectors.toList()));
	}

	private String toReadableString(T type) {
		return String.format("[%s].[%s] '%s'", type.getDeclaringClass().getSimpleName(), type.name(), type);
	}
}
