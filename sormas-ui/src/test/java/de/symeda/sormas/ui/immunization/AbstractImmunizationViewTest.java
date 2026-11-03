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
package de.symeda.sormas.ui.immunization;

import static de.symeda.sormas.ui.immunization.AbstractImmunizationView.ORIGIN_CASE_PARAM;
import static de.symeda.sormas.ui.immunization.AbstractImmunizationView.ORIGIN_CONTACT_PARAM;
import static de.symeda.sormas.ui.immunization.AbstractImmunizationView.ORIGIN_EVENT_PARTICIPANT_PARAM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link AbstractImmunizationView#getUrlParam(String, String)}, which reads the origin (case, contact or event participant)
 * from the view parameters, e.g. {@code "XYZ789?caseUuid=ABC123"} → {@code "ABC123"} for {@code "caseUuid"}.
 */
public class AbstractImmunizationViewTest {

	private static final String IMMUNIZATION_UUID = "XYZ789";
	private static final String ORIGIN_UUID = "ABC123";

	/**
	 * Opened from the immunization directory or a person: no parameters at all, e.g. {@code null}, {@code ""} or {@code "  "}.
	 */
	@Test
	public void testGetUrlParamWithBlankParams() {
		assertNull(AbstractImmunizationView.getUrlParam(null, ORIGIN_CASE_PARAM));
		assertNull(AbstractImmunizationView.getUrlParam("", ORIGIN_CASE_PARAM));
		assertNull(AbstractImmunizationView.getUrlParam("  ", ORIGIN_CASE_PARAM));
	}

	/**
	 * Only the immunization UUID, e.g. {@code "XYZ789"} or {@code "XYZ789?"}: no origin.
	 */
	@Test
	public void testGetUrlParamWithoutQuery() {
		assertNull(AbstractImmunizationView.getUrlParam(IMMUNIZATION_UUID, ORIGIN_CASE_PARAM));
		assertNull(AbstractImmunizationView.getUrlParam(IMMUNIZATION_UUID + "?", ORIGIN_CASE_PARAM));
	}

	/**
	 * Each origin parameter is found, e.g. {@code "XYZ789?contactUuid=ABC123"} → {@code "ABC123"} for {@code "contactUuid"}.
	 */
	@Test
	public void testGetUrlParamForEachOrigin() {
		assertEquals(ORIGIN_UUID, AbstractImmunizationView.getUrlParam(IMMUNIZATION_UUID + "?caseUuid=" + ORIGIN_UUID, ORIGIN_CASE_PARAM));
		assertEquals(
			ORIGIN_UUID,
			AbstractImmunizationView.getUrlParam(IMMUNIZATION_UUID + "?contactUuid=" + ORIGIN_UUID, ORIGIN_CONTACT_PARAM));
		assertEquals(
			ORIGIN_UUID,
			AbstractImmunizationView.getUrlParam(IMMUNIZATION_UUID + "?eventParticipantUuid=" + ORIGIN_UUID, ORIGIN_EVENT_PARTICIPANT_PARAM));
	}

	/**
	 * A parameter of another origin is not returned, e.g. {@code "XYZ789?caseUuid=ABC123"} → {@code null} for {@code "contactUuid"}.
	 */
	@Test
	public void testGetUrlParamOfOtherOrigin() {
		final String params = IMMUNIZATION_UUID + "?caseUuid=" + ORIGIN_UUID;

		assertNull(AbstractImmunizationView.getUrlParam(params, ORIGIN_CONTACT_PARAM));
		assertNull(AbstractImmunizationView.getUrlParam(params, ORIGIN_EVENT_PARTICIPANT_PARAM));
	}

	/**
	 * Several parameters, e.g. {@code "XYZ789?contactUuid=C1&caseUuid=ABC123"}: each name returns its own value.
	 */
	@Test
	public void testGetUrlParamWithSeveralParams() {
		final String params = IMMUNIZATION_UUID + "?contactUuid=C1&caseUuid=" + ORIGIN_UUID;

		assertEquals(ORIGIN_UUID, AbstractImmunizationView.getUrlParam(params, ORIGIN_CASE_PARAM));
		assertEquals("C1", AbstractImmunizationView.getUrlParam(params, ORIGIN_CONTACT_PARAM));
		assertNull(AbstractImmunizationView.getUrlParam(params, ORIGIN_EVENT_PARTICIPANT_PARAM));
	}

	/**
	 * Only the exact name matches, e.g. {@code "XYZ789?xcaseUuid=ABC123"} → {@code null} for {@code "caseUuid"}.
	 */
	@Test
	public void testGetUrlParamRequiresExactName() {
		assertNull(AbstractImmunizationView.getUrlParam(IMMUNIZATION_UUID + "?xcaseUuid=" + ORIGIN_UUID, ORIGIN_CASE_PARAM));
		assertNull(AbstractImmunizationView.getUrlParam(IMMUNIZATION_UUID + "?CASEUUID=" + ORIGIN_UUID, ORIGIN_CASE_PARAM));
	}

	/**
	 * Missing or blank value, e.g. {@code "XYZ789?caseUuid"}, {@code "XYZ789?caseUuid="} or {@code "XYZ789?caseUuid=  "} →
	 * {@code null}.
	 */
	@Test
	public void testGetUrlParamWithBlankValue() {
		assertNull(AbstractImmunizationView.getUrlParam(IMMUNIZATION_UUID + "?caseUuid", ORIGIN_CASE_PARAM));
		assertNull(AbstractImmunizationView.getUrlParam(IMMUNIZATION_UUID + "?caseUuid=", ORIGIN_CASE_PARAM));
		assertNull(AbstractImmunizationView.getUrlParam(IMMUNIZATION_UUID + "?caseUuid=  ", ORIGIN_CASE_PARAM));
	}

	/**
	 * A blank value does not hide a later valid one, e.g. {@code "XYZ789?caseUuid=&caseUuid=ABC123"} → {@code "ABC123"}.
	 */
	@Test
	public void testGetUrlParamSkipsBlankValue() {
		assertEquals(
			ORIGIN_UUID,
			AbstractImmunizationView.getUrlParam(IMMUNIZATION_UUID + "?caseUuid=&caseUuid=" + ORIGIN_UUID, ORIGIN_CASE_PARAM));
	}

	/**
	 * Name and value are trimmed, e.g. {@code "XYZ789? caseUuid = ABC123 "} → {@code "ABC123"}.
	 */
	@Test
	public void testGetUrlParamTrims() {
		assertEquals(ORIGIN_UUID, AbstractImmunizationView.getUrlParam(IMMUNIZATION_UUID + "? caseUuid = " + ORIGIN_UUID + " ", ORIGIN_CASE_PARAM));
	}

	/**
	 * Only the first {@code ?} and the first {@code =} split, e.g. {@code "XYZ789?caseUuid=A=B?C"} → {@code "A=B?C"}.
	 */
	@Test
	public void testGetUrlParamSplitsOnlyOnFirstSeparator() {
		assertEquals("A=B?C", AbstractImmunizationView.getUrlParam(IMMUNIZATION_UUID + "?caseUuid=A=B?C", ORIGIN_CASE_PARAM));
	}
}
