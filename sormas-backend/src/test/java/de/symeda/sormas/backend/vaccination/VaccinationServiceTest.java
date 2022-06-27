/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.vaccination;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.symptoms.Symptoms;

public class VaccinationServiceTest {

	private final VaccinationService vaccinationService = new VaccinationService();

	@Test
	public void testIsVaccinationRelevantCase() {
		Case caze = new Case();
		Vaccination vaccination = new Vaccination();

		/*
		 * Case report date
		 * 09/09/1990 13:00:00
		 */
		Date caseReportDate = new Date(652878000000L);

		caze.setReportDate(caseReportDate);

		vaccination.setReportDate(DateHelper.addDays(caseReportDate, 13));
		assertTrue(vaccinationService.isVaccinationRelevant(caze, vaccination));

		vaccination.setReportDate(DateHelper.addDays(caseReportDate, 14));
		assertFalse(vaccinationService.isVaccinationRelevant(caze, vaccination));

		vaccination.setReportDate(DateHelper.addDays(caseReportDate, 14));
		vaccination.setVaccinationDate(DateHelper.subtractDays(caseReportDate, 1));
		assertTrue(vaccinationService.isVaccinationRelevant(caze, vaccination));

		vaccination.setVaccinationDate(caseReportDate);
		assertFalse(vaccinationService.isVaccinationRelevant(caze, vaccination));

		vaccination.setVaccinationDate(null);

		/*
		 * Onset date
		 * 09/27/1990 13:00:00
		 */
		Date onsetDate = new Date(654433200000L);
		Symptoms symptoms = new Symptoms();
		symptoms.setOnsetDate(onsetDate);
		caze.setSymptoms(symptoms);

		vaccination.setReportDate(DateHelper.addDays(onsetDate, 13));
		assertTrue(vaccinationService.isVaccinationRelevant(caze, vaccination));

		vaccination.setReportDate(DateHelper.addDays(onsetDate, 14));
		assertFalse(vaccinationService.isVaccinationRelevant(caze, vaccination));

		vaccination.setReportDate(DateHelper.addDays(onsetDate, 14));
		vaccination.setVaccinationDate(DateHelper.subtractDays(onsetDate, 1));
		assertTrue(vaccinationService.isVaccinationRelevant(caze, vaccination));

		vaccination.setVaccinationDate(onsetDate);
		assertFalse(vaccinationService.isVaccinationRelevant(caze, vaccination));

	}

	@Test
	public void testIsVaccinationRelevantContact() {

		Contact contact = new Contact();
		Vaccination vaccination = new Vaccination();

		/*
		 * Contact report date
		 * 09/09/1990 13:00:00
		 */
		Date contactReportDate = new Date(652878000000L);

		contact.setReportDateTime(contactReportDate);

		vaccination.setReportDate(DateHelper.addDays(contactReportDate, 13));
		assertTrue(vaccinationService.isVaccinationRelevant(contact, vaccination));

		vaccination.setReportDate(DateHelper.addDays(contactReportDate, 14));
		assertFalse(vaccinationService.isVaccinationRelevant(contact, vaccination));

		vaccination.setReportDate(DateHelper.addDays(contactReportDate, 14));
		vaccination.setVaccinationDate(DateHelper.subtractDays(contactReportDate, 1));
		assertTrue(vaccinationService.isVaccinationRelevant(contact, vaccination));

		vaccination.setVaccinationDate(contactReportDate);
		assertFalse(vaccinationService.isVaccinationRelevant(contact, vaccination));

		vaccination.setVaccinationDate(null);

		/*
		 * Last contact date
		 * 09/27/1990 13:00:00
		 */
		Date lastContactDate = new Date(654433200000L);

		contact.setLastContactDate(lastContactDate);

		vaccination.setReportDate(DateHelper.addDays(lastContactDate, 13));
		assertTrue(vaccinationService.isVaccinationRelevant(contact, vaccination));

		vaccination.setReportDate(DateHelper.addDays(lastContactDate, 14));
		assertFalse(vaccinationService.isVaccinationRelevant(contact, vaccination));

		vaccination.setReportDate(DateHelper.addDays(lastContactDate, 14));
		vaccination.setVaccinationDate(DateHelper.subtractDays(lastContactDate, 1));
		assertTrue(vaccinationService.isVaccinationRelevant(contact, vaccination));

		vaccination.setVaccinationDate(lastContactDate);
		assertFalse(vaccinationService.isVaccinationRelevant(contact, vaccination));

	}

	@Test
	public void testIsVaccinationRelevantEvent() {

		Event event = new Event();
		Vaccination vaccination = new Vaccination();

		/*
		 * Event report date
		 * 09/09/1990 13:00:00
		 */
		Date eventReportDate = new Date(652878000000L);

		event.setReportDateTime(eventReportDate);

		vaccination.setReportDate(DateHelper.addDays(eventReportDate, 13));
		assertTrue(vaccinationService.isVaccinationRelevant(event, vaccination));

		vaccination.setReportDate(DateHelper.addDays(eventReportDate, 14));
		assertFalse(vaccinationService.isVaccinationRelevant(event, vaccination));

		vaccination.setReportDate(DateHelper.addDays(eventReportDate, 14));
		vaccination.setVaccinationDate(DateHelper.subtractDays(eventReportDate, 1));
		assertTrue(vaccinationService.isVaccinationRelevant(event, vaccination));

		vaccination.setVaccinationDate(eventReportDate);
		assertFalse(vaccinationService.isVaccinationRelevant(event, vaccination));

		vaccination.setVaccinationDate(null);

		/*
		 * Event end date
		 * 09/27/1990 13:00:00
		 */
		Date eventEndDate2 = new Date(654433200000L);

		event.setEndDate(eventEndDate2);

		vaccination.setReportDate(DateHelper.addDays(eventEndDate2, 13));
		assertTrue(vaccinationService.isVaccinationRelevant(event, vaccination));

		vaccination.setReportDate(DateHelper.addDays(eventEndDate2, 14));
		assertFalse(vaccinationService.isVaccinationRelevant(event, vaccination));

		vaccination.setReportDate(DateHelper.addDays(eventEndDate2, 14));
		vaccination.setVaccinationDate(DateHelper.subtractDays(eventEndDate2, 1));
		assertTrue(vaccinationService.isVaccinationRelevant(event, vaccination));

		vaccination.setVaccinationDate(eventEndDate2);
		assertFalse(vaccinationService.isVaccinationRelevant(event, vaccination));

		vaccination.setVaccinationDate(null);

		/*
		 * Event start date
		 * 10/05/1990 13:00:00
		 */
		Date eventStartDate = new Date(655128000000L);

		event.setStartDate(eventStartDate);

		vaccination.setReportDate(DateHelper.addDays(eventStartDate, 13));
		assertTrue(vaccinationService.isVaccinationRelevant(event, vaccination));

		vaccination.setReportDate(DateHelper.addDays(eventStartDate, 14));
		assertFalse(vaccinationService.isVaccinationRelevant(event, vaccination));

		vaccination.setReportDate(DateHelper.addDays(eventStartDate, 14));
		vaccination.setVaccinationDate(DateHelper.subtractDays(eventStartDate, 1));
		assertTrue(vaccinationService.isVaccinationRelevant(event, vaccination));

		vaccination.setVaccinationDate(eventStartDate);
		assertFalse(vaccinationService.isVaccinationRelevant(event, vaccination));

	}
}
