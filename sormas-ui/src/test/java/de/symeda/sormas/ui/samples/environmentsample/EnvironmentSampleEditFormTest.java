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

package de.symeda.sormas.ui.samples.environmentsample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.server.VaadinSession;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleMaterial;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.SormasDefaultConverterFactory;

/**
 * #14519 - Measurement fields hidden for {@link EnvironmentSampleMaterial#VECTORS} must not block saving.
 */
public class EnvironmentSampleEditFormTest extends AbstractUiBeanTest {

	private static final String[] NUMERIC_MEASUREMENT_FIELDS = {
		EnvironmentSampleDto.SAMPLE_VOLUME,
		EnvironmentSampleDto.TURBIDITY,
		EnvironmentSampleDto.SAMPLE_TEMPERATURE,
		EnvironmentSampleDto.CHLORINE_RESIDUALS,
		EnvironmentSampleDto.PH_VALUE };

	/**
	 * Vaadin v7 fields get their String-to-number converter from the converter factory of the current
	 * {@link VaadinSession}. Tests have no session, so without this mock the measurement fields had no converter:
	 * e.g. loading {@code sampleVolume = 10f} failed with "Unable to convert value of type java.lang.Float to
	 * presentation type class java.lang.String". The production UI sets {@link SormasDefaultConverterFactory} the same way.
	 */
	@BeforeEach
	public void mockVaadinSessionWithConverterFactory() {

		VaadinSession session = mock(VaadinSession.class);
		when(session.getConverterFactory()).thenReturn(new SormasDefaultConverterFactory());
		when(session.getLocale()).thenReturn(Locale.ENGLISH);
		when(session.hasLock()).thenReturn(true);
		VaadinSession.setCurrent(session);
	}

	@AfterEach
	public void resetVaadinSession() {
		VaadinSession.setCurrent(null);
	}

	@Test
	public void invalidSampleVolumeIsClearedWhenMaterialChangesToVectors() {

		EnvironmentSampleEditForm form = createForm(EnvironmentSampleMaterial.WATER);
		TextField sampleVolumeField = form.getField(EnvironmentSampleDto.SAMPLE_VOLUME);

		sampleVolumeField.setValue("abc");
		assertFalse(sampleVolumeField.isValid(), "precondition: 'abc' is not a valid sample volume");

		selectMaterial(form, EnvironmentSampleMaterial.VECTORS);

		assertFalse(sampleVolumeField.isVisible());
		assertTrue(sampleVolumeField.isEmpty());
		assertTrue(sampleVolumeField.isValid());
	}

	@Test
	public void allMeasurementFieldsAreClearedWhenMaterialChangesToVectors() {

		EnvironmentSampleEditForm form = createForm(EnvironmentSampleMaterial.WATER);
		for (String propertyId : NUMERIC_MEASUREMENT_FIELDS) {
			form.<TextField> getField(propertyId).setValue("7");
		}
		NullableOptionGroup heavyRainField = form.getField(EnvironmentSampleDto.HEAVY_RAIN);
		heavyRainField.setNullableValue(YesNoUnknown.YES);

		selectMaterial(form, EnvironmentSampleMaterial.VECTORS);

		for (String propertyId : NUMERIC_MEASUREMENT_FIELDS) {
			assertTrue(form.<TextField> getField(propertyId).isEmpty(), propertyId + " should be cleared");
		}
		assertNull(heavyRainField.getNullableValue(), EnvironmentSampleDto.HEAVY_RAIN + " should be cleared");
	}

	@Test
	public void measurementFieldsAreVisibleAndEmptyAfterSwitchingBackFromVectors() {

		EnvironmentSampleEditForm form = createForm(EnvironmentSampleMaterial.WATER);
		TextField sampleVolumeField = form.getField(EnvironmentSampleDto.SAMPLE_VOLUME);
		sampleVolumeField.setValue("abc");

		selectMaterial(form, EnvironmentSampleMaterial.VECTORS);
		selectMaterial(form, EnvironmentSampleMaterial.WATER);

		assertTrue(sampleVolumeField.isVisible());
		assertTrue(sampleVolumeField.isEmpty());
		assertTrue(sampleVolumeField.isValid());
	}

	@Test
	public void measurementsAreKeptWhenSwitchingBetweenNonVectorMaterials() {

		EnvironmentSampleEditForm form = createForm(EnvironmentSampleMaterial.WATER);
		TextField sampleVolumeField = form.getField(EnvironmentSampleDto.SAMPLE_VOLUME);
		sampleVolumeField.setValue("10");

		selectMaterial(form, EnvironmentSampleMaterial.SOIL);

		assertEquals("10", sampleVolumeField.getValue());
	}

	@Test
	public void storedMeasurementOfExistingVectorSampleIsKeptOnLoad() {

		EnvironmentSampleDto sample = EnvironmentSampleDto.build(null, null);
		sample.setSampleMaterial(EnvironmentSampleMaterial.VECTORS);
		sample.setSampleVolume(10f);

		EnvironmentSampleEditForm form = new EnvironmentSampleEditForm(false, true);
		form.setValue(sample);

		assertFalse(form.<TextField> getField(EnvironmentSampleDto.SAMPLE_VOLUME).isEmpty());
	}

	private EnvironmentSampleEditForm createForm(EnvironmentSampleMaterial material) {

		EnvironmentSampleDto sample = EnvironmentSampleDto.build(null, null);
		sample.setSampleMaterial(material);

		EnvironmentSampleEditForm form = new EnvironmentSampleEditForm(false, true);
		form.setValue(sample);
		return form;
	}

	private void selectMaterial(EnvironmentSampleEditForm form, EnvironmentSampleMaterial material) {
		form.<ComboBox> getField(EnvironmentSampleDto.SAMPLE_MATERIAL).setValue(material);
	}
}
