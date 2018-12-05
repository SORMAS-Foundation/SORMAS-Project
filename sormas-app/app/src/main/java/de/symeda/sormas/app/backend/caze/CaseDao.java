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

package de.symeda.sormas.app.backend.caze;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.report.WeeklyReport;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.backend.sample.SampleTestDao;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.util.LocationService;

public class CaseDao extends AbstractAdoDao<Case> {

    public CaseDao(Dao<Case, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Case> getAdoClass() {
        return Case.class;
    }

    @Override
    public String getTableName() {
        return Case.TABLE_NAME;
    }

    @Override
    @Deprecated
    /**
     * Deprecated: Call queryUuidBasic, queryUuidReference or queryUuidWithEmbedded instead
     */
    public Case queryUuid(String uuid) {
        return super.queryUuid(uuid);
    }

    public Case queryUuidBasic(String uuid) {
        return super.queryUuid(uuid);
    }

    @Override
    public Date getLatestChangeDate() {
        Date date = super.getLatestChangeDate();
        if (date == null) {
            return null;
        }

        Date symptomsDate = getLatestChangeDateJoin(Symptoms.TABLE_NAME, Case.SYMPTOMS);
        if (symptomsDate != null && symptomsDate.after(date)) {
            date = symptomsDate;
        }

        Date hospitalizationDate = DatabaseHelper.getHospitalizationDao().getLatestChangeDate();
        if (hospitalizationDate != null && hospitalizationDate.after(date)) {
            date = hospitalizationDate;
        }
        Date epiDataDate = DatabaseHelper.getEpiDataDao().getLatestChangeDate();
        if (epiDataDate != null && epiDataDate.after(date)) {
            date = epiDataDate;
        }

        return date;
    }

    public List<Case> queryBaseForEq(String fieldName, Object value, String orderBy, boolean ascending) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.eq(fieldName, value);
            where.and().eq(AbstractDomainObject.SNAPSHOT, false).query();
            builder.selectColumns(Case.UUID, Case.LAST_OPENED_DATE, Case.LOCAL_CHANGE_DATE, Case.MODIFIED, Case.REPORT_DATE,
                    Case.REPORTING_USER, Case.DISEASE, Case.DISEASE_DETAILS, Case.PERSON,
                    Case.CASE_CLASSIFICATION, Case.INVESTIGATION_STATUS, Case.OUTCOME,
                    Case.HEALTH_FACILITY);
            return builder.orderBy(orderBy, ascending).query();
        } catch (SQLException | IllegalArgumentException e) {
            Log.e(getTableName(), "Could not perform queryForEq");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Case build() {
        throw new UnsupportedOperationException("Use build(Person) instead");
    }

    public Case build(Person person) {
        Case caze = super.build();
        caze.setPerson(person);

        caze.setReportDate(new Date());
        User user = ConfigProvider.getUser();
        caze.setReportingUser(user);

        if (user.hasUserRole(UserRole.SURVEILLANCE_OFFICER)) {
            caze.setSurveillanceOfficer(user);
        } else if (user.hasUserRole(UserRole.HOSPITAL_INFORMANT) || user.hasUserRole(UserRole.COMMUNITY_INFORMANT)) {
            caze.setSurveillanceOfficer(user.getAssociatedOfficer());
        }

        caze.setInvestigationStatus(InvestigationStatus.PENDING);
        caze.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
        caze.setOutcome(CaseOutcome.NO_OUTCOME);

        // Symptoms
        caze.setSymptoms(DatabaseHelper.getSymptomsDao().build());

        // Hospitalization
        caze.setHospitalization(DatabaseHelper.getHospitalizationDao().build());

        // Epi Data
        caze.setEpiData(DatabaseHelper.getEpiDataDao().build());

        // Location
        User currentUser = ConfigProvider.getUser();
        caze.setRegion(currentUser.getRegion());
        caze.setDistrict(currentUser.getDistrict());

        if (currentUser.getCommunity() != null) {
            caze.setCommunity(currentUser.getCommunity());
        } else {
            caze.setHealthFacility(currentUser.getHealthFacility());
            if (caze.getHealthFacility() != null) {
                caze.setCommunity(caze.getHealthFacility().getCommunity());
            }
        }

        return caze;
    }

    public Case build(Person person, Case caze) {
        Case newCase = build(person);
        if (caze != null) {
            newCase.setDisease(caze.getDisease());
            newCase.setDiseaseDetails(caze.getDiseaseDetails());
            newCase.setPlagueType(caze.getPlagueType());
            newCase.setDengueFeverType(caze.getDengueFeverType());
        }

        return newCase;
    }

    public Case build(EventParticipant eventParticipant) {
        Case newCase = build(eventParticipant.getPerson());
        Event event = eventParticipant.getEvent();
        newCase.setDisease(event.getDisease());
        newCase.setDiseaseDetails(event.getDiseaseDetails());
        return newCase;
    }

    public Case transferCase(Case caseToTransfer) throws DaoException {
        Case caze = queryForIdWithEmbedded(caseToTransfer.getId());

        Facility facility = caseToTransfer.getHealthFacility();
        Community community = caseToTransfer.getCommunity();
        District district = caseToTransfer.getDistrict();
        Region region = caseToTransfer.getRegion();
        String facilityDetails = caseToTransfer.getHealthFacilityDetails();
        User surveillanceOfficer = caseToTransfer.getSurveillanceOfficer();

        // If the facility has changed, add a previous hospitalization
        if (!caze.getHealthFacility().getUuid().equals(facility.getUuid())) {
            caze.getHospitalization().getPreviousHospitalizations().add(DatabaseHelper.getPreviousHospitalizationDao().buildPreviousHospitalizationFromHospitalization(caze));
            caze.getHospitalization().setHospitalizedPreviously(YesNoUnknown.YES);
            caze.getHospitalization().setAdmissionDate(new Date());
            caze.getHospitalization().setDischargeDate(null);
            caze.getHospitalization().setIsolated(null);
        }

        // If the district has changed and there is exactly one surveillance officer assigned to that district,
        // assign them as the new surveillance officer; assign null otherwise
        if (!caze.getDistrict().getUuid().equals(district.getUuid())) {
            List<User> districtOfficers = DatabaseHelper.getUserDao().getByDistrictAndRole(district, UserRole.SURVEILLANCE_OFFICER, User.UUID);
            if (districtOfficers.size() == 1) {
                surveillanceOfficer = districtOfficers.get(0);
            } else {
                surveillanceOfficer = null;
            }
        }

        caze.setRegion(region);
        caze.setDistrict(district);
        caze.setCommunity(community);
        caze.setHealthFacility(facility);
        caze.setHealthFacilityDetails(facilityDetails);
        caze.setSurveillanceOfficer(surveillanceOfficer);

        saveAndSnapshot(caze);

        for (Task task : DatabaseHelper.getTaskDao().queryByCase(caze)) {
            if (task.getTaskStatus() != TaskStatus.PENDING) {
                continue;
            }

            if (surveillanceOfficer != null) {
                task.setAssigneeUser(surveillanceOfficer);
            } else {
                // TODO roles? what happens when there are no supervisors? assignee user cannot be null
                List<User> survSupervisors = DatabaseHelper.getUserDao().getByRegionAndRole(region, UserRole.SURVEILLANCE_SUPERVISOR);
                List<User> caseSupervisors = DatabaseHelper.getUserDao().getByRegionAndRole(region, UserRole.CASE_SUPERVISOR);
                if (survSupervisors.size() >= 1) {
                    task.setAssigneeUser(survSupervisors.get(0));
                } else if (caseSupervisors.size() >= 1) {
                    task.setAssigneeUser(caseSupervisors.get(0));
                } else {
                    task.setAssigneeUser(null);
                }
            }

            DatabaseHelper.getTaskDao().saveAndSnapshot(task);
        }

        return caze;
    }

    /**
     * Returns the number of cases reported by the current user over the course of the given epi week.
     */
    public int getNumberOfCasesForEpiWeek(EpiWeek epiWeek, User informant) {
        return getNumberOfCasesForEpiWeekAndDisease(epiWeek, null, informant);
    }

    /**
     * Returns the number of cases with the given disease reported by the current user over the course of the given epi week.
     */
    public int getNumberOfCasesForEpiWeekAndDisease(EpiWeek epiWeek, Disease disease, User informant) {
        if (!(informant.hasUserRole(UserRole.HOSPITAL_INFORMANT) || informant.hasUserRole(UserRole.COMMUNITY_INFORMANT))) {
            throw new UnsupportedOperationException("Can only retrieve the number of reported cases by epi week and disease for Informants.");
        }

        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and(
                    where.eq(Case.REPORTING_USER, informant),
                    where.ge(Case.REPORT_DATE, DateHelper.getEpiWeekStart(epiWeek)),
                    where.le(Case.REPORT_DATE, DateHelper.getEpiWeekEnd(epiWeek))
            );

            if (disease != null) {
                where.and(where, where.eq(Case.DISEASE, disease));
            }

            return (int) builder.countOf();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getNumberOfCasesForEpiWeekAndDisease");
            throw new RuntimeException(e);
        }
    }

    // TODO #704
//    @Override
//    /**
//     * @param caze person has to be initialized
//     */
//    public void markAsRead(Case caze) {
//        super.markAsRead(caze);
//        DatabaseHelper.getPersonDao().markAsRead(caze.getPerson());
//    }

    @Override
    public Case mergeOrCreate(Case source) throws DaoException {
        Case currentCase = super.queryUuid(source.getUuid());

        // date of outcome can be set by the server site automatically and at the same time in the app
        // see CaseEditActivity.updateOutcomeAndPersonCondition
        if (currentCase != null && currentCase.isModified()
                && currentCase.getOutcomeDate() != null
                && source.getOutcomeDate() != null
                && currentCase.getOutcomeDate() != source.getOutcomeDate()) {
            // this could be the situation, but we also have to check the snapshot - the outcome date has to be null
            Case snapshotCase = querySnapshotByUuid(source.getUuid());
            if (snapshotCase != null && snapshotCase.getOutcomeDate() == null) {
                // we now have to ignore the conflict -> the outcome date of the app always wins
                source.setOutcomeDate(currentCase.getOutcomeDate());
            }
        }

        Case mergedCase = super.mergeOrCreate(source);

        // Build and send a notification when the disease has changed
        if (currentCase != null && mergedCase != null && currentCase.getDisease() != mergedCase.getDisease()) {
            Context context = DatabaseHelper.getContext();

            StringBuilder content = new StringBuilder();
            content.append("<b>").append(mergedCase.toString()).append("</b><br/>");

            Intent notificationIntent = new Intent(context, CaseReadActivity.class);
            notificationIntent.putExtras(CaseReadActivity.buildBundle(mergedCase.getUuid(), false).get());
            PendingIntent pi = PendingIntent.getActivity(context, mergedCase.getId().intValue(), notificationIntent, 0);
            Resources r = context.getResources();

            Notification notification = new NotificationCompat.Builder(context)
                    .setTicker(r.getString(R.string.headline_case_notification))
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentTitle(r.getString(R.string.headline_case_notification))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(content.toString())))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int notificationId = mergedCase.getId().intValue();
            notificationManager.notify(notificationId, notification);
        }
        return mergedCase;
    }

    @Override
    public Case saveAndSnapshot(final Case caze) throws DaoException {
        // If a new case is created, use the last available location to update its report latitude and longitude
        if (caze.getId() == null) {
            Location location = LocationService.instance().getLocation();
            if (location != null) {
                caze.setReportLat(location.getLatitude());
                caze.setReportLon(location.getLongitude());
                caze.setReportLatLonAccuracy(location.getAccuracy());
            }
        }

        return super.saveAndSnapshot(caze);
    }

    public void deleteCaseAndAllDependingEntities(String caseUuid) throws SQLException {
        Case caze = queryUuidWithEmbedded(caseUuid);

        // Cancel if case is not in local database
        if (caze == null) {
            return;
        }

        // Delete contacts, contact tasks and visits
        List<Contact> contacts = DatabaseHelper.getContactDao().getByCase(caze);
        for (Contact contact : contacts) {
            List<Visit> visits = DatabaseHelper.getVisitDao().getByContact(contact);
            for (Visit visit : visits) {
                // Only delete the visit if no other contact with the same person and disease is present in the system;
                // otherwise the visit might also be used in another contact
                if (DatabaseHelper.getContactDao().getCountByPersonAndDisease(visit.getPerson(), visit.getDisease()) <= 1) {
                    DatabaseHelper.getVisitDao().deleteCascade(visit);
                }
            }
            List<Task> tasks = DatabaseHelper.getTaskDao().queryByContact(contact);
            for (Task task : tasks) {
                DatabaseHelper.getTaskDao().deleteCascade(task);
            }

            DatabaseHelper.getContactDao().deleteCascade(contact);
        }

        // Delete samples and sample tests
        List<Sample> samples = DatabaseHelper.getSampleDao().queryByCase(caze);
        for (Sample sample : samples) {
            List<SampleTest> sampleTests = DatabaseHelper.getSampleTestDao().queryBySample(sample);
            for (SampleTest sampleTest : sampleTests) {
                DatabaseHelper.getSampleTestDao().deleteCascade(sampleTest);
            }

            DatabaseHelper.getSampleDao().deleteCascade(sample);
        }

        // Delete case tasks
        List<Task> tasks = DatabaseHelper.getTaskDao().queryByCase(caze);
        for (Task task : tasks) {
            DatabaseHelper.getTaskDao().deleteCascade(task);
        }

        // Delete case
        deleteCascade(caze);
    }
}
