/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.ui.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.vaadin.v7.data.util.ObjectProperty;

import de.symeda.sormas.api.sample.SamplePurpose;

/**
 * Tests the switch of {@link NullableOptionGroup} between its two modes: multi-select with
 * {@link NullableOptionGroupConverter} while not required, single-select without a converter while required.
 * <p>
 * Example (the sample form): "Sample purpose" is loaded with EXTERNAL while not required (value {@code [EXTERNAL]}), then
 * set to required; committing must save EXTERNAL instead of failing with "Unable to convert value of type
 * java.util.Collections$SingletonSet to model type SamplePurpose".
 */
class NullableOptionGroupTest {

	@Test
	void shouldCommitLoadedValueAfterSwitchingToRequired() {
		ObjectProperty<SamplePurpose> property = new ObjectProperty<>(SamplePurpose.EXTERNAL, SamplePurpose.class);
		NullableOptionGroup field = createBufferedField(property);
		assertEquals(Collections.singleton(SamplePurpose.EXTERNAL), field.getValue());

		field.setRequired(true);

		assertEquals(SamplePurpose.EXTERNAL, field.getValue());
		assertDoesNotThrow(field::commit);
		assertEquals(SamplePurpose.EXTERNAL, property.getValue());
	}

	@Test
	void shouldCommitChangedValueAfterSwitchingToRequired() {
		ObjectProperty<SamplePurpose> property = new ObjectProperty<>(SamplePurpose.EXTERNAL, SamplePurpose.class);
		NullableOptionGroup field = createBufferedField(property);
		field.setRequired(true);

		field.setValue(SamplePurpose.INTERNAL);

		assertDoesNotThrow(field::commit);
		assertEquals(SamplePurpose.INTERNAL, property.getValue());
	}

	@Test
	void shouldKeepEmptyValueWhenSwitchingToRequired() {
		ObjectProperty<SamplePurpose> property = new ObjectProperty<>(null, SamplePurpose.class);
		NullableOptionGroup field = createBufferedField(property);

		field.setRequired(true);

		assertNull(field.getValue());
	}

	@Test
	void shouldSwitchToRequiredWhileReadOnly() {
		// setRequired temporarily lifts read-only, so the value is converted and read-only is restored
		ObjectProperty<SamplePurpose> property = new ObjectProperty<>(SamplePurpose.EXTERNAL, SamplePurpose.class);
		NullableOptionGroup field = createBufferedField(property);
		field.setReadOnly(true);

		field.setRequired(true);

		assertTrue(field.isReadOnly());
		assertEquals(SamplePurpose.EXTERNAL, field.getValue());
	}

	@Test
	void shouldKeepValueWhenSwitchingBackToNotRequired() {
		ObjectProperty<SamplePurpose> property = new ObjectProperty<>(SamplePurpose.EXTERNAL, SamplePurpose.class);
		NullableOptionGroup field = createBufferedField(property);
		field.setRequired(true);

		field.setRequired(false);

		assertTrue(field.getValue() instanceof Set, "multi-select again");
		assertEquals(SamplePurpose.EXTERNAL, field.getNullableValue());
		assertDoesNotThrow(field::commit);
		assertEquals(SamplePurpose.EXTERNAL, property.getValue());
	}

	@Test
	void shouldReturnNullableValueInBothModes() {
		ObjectProperty<SamplePurpose> property = new ObjectProperty<>(SamplePurpose.INTERNAL, SamplePurpose.class);
		NullableOptionGroup field = createBufferedField(property);
		assertEquals(SamplePurpose.INTERNAL, field.getNullableValue());

		// required: the value is not a set any more, getNullableValue must not fail with a ClassCastException
		field.setRequired(true);
		assertEquals(SamplePurpose.INTERNAL, field.getNullableValue());
	}

	@Test
	void shouldCommitNotRequiredValue() {
		// the regular case without a mode switch is unchanged
		ObjectProperty<SamplePurpose> property = new ObjectProperty<>(SamplePurpose.EXTERNAL, SamplePurpose.class);
		NullableOptionGroup field = createBufferedField(property);

		field.setNullableValue(SamplePurpose.INTERNAL);

		assertFalse(field.isRequired());
		assertDoesNotThrow(field::commit);
		assertEquals(SamplePurpose.INTERNAL, property.getValue());
	}

	/** A buffered option group bound to the property, as in the forms (FieldGroup fields are buffered). */
	private static NullableOptionGroup createBufferedField(ObjectProperty<SamplePurpose> property) {
		NullableOptionGroup field = new NullableOptionGroup(null, Arrays.asList(SamplePurpose.values()));
		field.setBuffered(true);
		field.setPropertyDataSource(property);
		return field;
	}
}
