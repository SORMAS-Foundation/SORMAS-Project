package de.symeda.sormas.backend.patch.customizablefield.mappers;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.backend.AbstractBeanTest;

class CustomizableFieldValuePatchMapperRegistryBeanTest extends AbstractBeanTest {

	@Test
	void verify_all_customizable_fields_test_are_supported() {
		// if this test fails: this means a CustomizableFieldType was added and is not yet added.
		// check with the business if the new type is need or not:
		// needed: add new mapper
		// not needed: add into IGNORED_TYPES (be sure to have checked with business)

		Set<CustomizableFieldType> supportedTypes = getBean(CustomizableFieldValuePatchMapperRegistry.class).getAllSupportedTypes();
		Set<CustomizableFieldType> IGNORED_TYPES = Set.of();

		Assertions.assertAll(
			Arrays.stream(CustomizableFieldType.values())
				.filter(type -> !IGNORED_TYPES.contains(type))
				.map(
					type -> (Executable) () -> Assertions
						.assertTrue(supportedTypes.contains(type), String.format("[%s] is not supported, but should", type)))
				.collect(Collectors.toList()));

	}
}
