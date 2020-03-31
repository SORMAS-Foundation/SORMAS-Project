/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Mate Strysewske on 16.06.2017.
 */
@RunWith(AndroidJUnit4.class)
public class ContactBackendTest {

    @Rule
    public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

    @Before
    public void initTest() {
        TestHelper.initTestEnvironment(false);
    }

    @Test
    public void shouldCreateContact() {
        assertThat(DatabaseHelper.getContactDao().queryForAll().size(), is(0));
        TestEntityCreator.createContact(null);

        assertThat(DatabaseHelper.getContactDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldCreateVisit() throws DaoException {
        assertThat(DatabaseHelper.getVisitDao().queryForAll().size(), is(0));

        Contact contact = TestEntityCreator.createContact(null);
        TestEntityCreator.createVisit(contact);

        assertThat(DatabaseHelper.getVisitDao().queryForAll().size(), is(1));
    }

    /**
     * This tests merging of contacts and visits.
     */
    @Test
    public void shouldMergeAsExpected() throws DaoException {
        Contact contact = TestEntityCreator.createContact(null);
        Visit visit = TestEntityCreator.createVisit(contact);

        contact.setDescription("AppDescription");
        visit.setVisitStatus(VisitStatus.UNCOOPERATIVE);

        DatabaseHelper.getContactDao().saveAndSnapshot(contact);
        DatabaseHelper.getContactDao().accept(contact);
        DatabaseHelper.getVisitDao().saveAndSnapshot(visit);
        DatabaseHelper.getVisitDao().accept(visit);

        Contact mergeContact = (Contact) contact.clone();
        mergeContact.setPerson((Person) contact.getPerson().clone());
        mergeContact.setCaseUuid(contact.getCaseUuid());
        mergeContact.setDisease(contact.getDisease());
        mergeContact.setDiseaseDetails(contact.getDiseaseDetails());
        mergeContact.setId(null);
        mergeContact.getPerson().getAddress().setId(null);

        mergeContact.setDescription("ServerDescription");

        Visit mergeVisit = (Visit) visit.clone();
        mergeVisit.setPerson((Person) visit.getPerson().clone());
        mergeVisit.getPerson().setAddress((Location) visit.getPerson().getAddress().clone());
        mergeVisit.setSymptoms((Symptoms) visit.getSymptoms().clone());
        mergeVisit.setId(null);
        mergeVisit.getPerson().getAddress().setId(null);
        mergeVisit.getSymptoms().setId(null);

        mergeVisit.setVisitStatus(VisitStatus.COOPERATIVE);

        DatabaseHelper.getContactDao().mergeOrCreate(mergeContact);
        DatabaseHelper.getVisitDao().mergeOrCreate(mergeVisit);

        Contact updatedContact = DatabaseHelper.getContactDao().queryUuid(contact.getUuid());
        assertThat(updatedContact.getDescription(), is("ServerDescription"));
        Visit updatedVisit = DatabaseHelper.getVisitDao().queryUuid(visit.getUuid());
        assertThat(updatedVisit.getVisitStatus(), is(VisitStatus.COOPERATIVE));
    }

    @Test
    public void shouldAcceptAsExpected() throws DaoException {
        Contact contact = TestEntityCreator.createContact(null);
        assertThat(contact.isModified(), is(false));

        contact.setDescription("NewContactDescription");

        DatabaseHelper.getContactDao().saveAndSnapshot(contact);
        contact = DatabaseHelper.getContactDao().queryUuid(contact.getUuid());

        assertThat(contact.isModified(), is(true));
        assertNotNull(DatabaseHelper.getContactDao().querySnapshotByUuid(contact.getUuid()));

        DatabaseHelper.getContactDao().accept(contact);
        contact = DatabaseHelper.getContactDao().queryUuid(contact.getUuid());

        assertNull(DatabaseHelper.getContactDao().querySnapshotByUuid(contact.getUuid()));
        assertThat(contact.isModified(), is(false));
    }

}
