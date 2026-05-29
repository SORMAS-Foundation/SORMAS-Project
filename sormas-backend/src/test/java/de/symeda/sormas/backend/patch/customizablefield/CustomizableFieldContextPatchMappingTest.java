package de.symeda.sormas.backend.patch.customizablefield;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.backend.AbstractUnitTest;

class CustomizableFieldContextPatchMappingTest extends AbstractUnitTest {

	@Test
	void allContextsMappedInEnum() {
		Set<CustomizableFieldContext> mappedContexts = Arrays.stream(CustomizableFieldContextPatchMapping.values())
			.map(CustomizableFieldContextPatchMapping::getCustomizableFieldContext)
			.collect(Collectors.toSet());

		for (CustomizableFieldContext context : CustomizableFieldContext.values()) {
			assertTrue(
				mappedContexts.contains(context),
				"CustomizableFieldContext." + context.name() + " has no entry in CustomizableFieldContextPatchMapping");
		}
	}
}
