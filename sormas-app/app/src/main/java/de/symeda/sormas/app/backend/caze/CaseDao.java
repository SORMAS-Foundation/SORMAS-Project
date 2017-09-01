package de.symeda.sormas.app.backend.caze;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
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
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.util.LocationService;

public class CaseDao extends AbstractAdoDao<Case> {

    public CaseDao(Dao<Case,Long> innerDao) throws SQLException {
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

    // TODO #69 build some date filter for finding the right case (this is implemented in CaseService.java too)
    public Case getByPersonAndDisease(Person person, Disease disease) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and(
                    where.eq(Case.PERSON + "_id", person),
                    where.eq(Case.DISEASE, disease)
            );

            return (Case) builder.queryForFirst();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getByPersonAndDisease");
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
        caze.setReportingUser(ConfigProvider.getUser());

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
        caze.setHealthFacility(currentUser.getHealthFacility());
        if (caze.getHealthFacility() != null) {
            caze.setCommunity(caze.getHealthFacility().getCommunity());
        }

        return caze;
    }

    public Case moveCase(Case caseToMove) throws DaoException {
        Case caze = queryForIdWithEmbedded(caseToMove.getId());

        Facility facility = caseToMove.getHealthFacility();
        Community community = caseToMove.getCommunity();
        District district = caseToMove.getDistrict();
        Region region = caseToMove.getRegion();
        String facilityDetails = caseToMove.getHealthFacilityDetails();
        User surveillanceOfficer = caseToMove.getSurveillanceOfficer();

        // If the facility has changed, add a previous hospitalization
        if (!caze.getHealthFacility().getUuid().equals(facility.getUuid())) {
            caze.getHospitalization().getPreviousHospitalizations().add(DatabaseHelper.getPreviousHospitalizationDao().buildPreviousHospitalization(caze));
            caze.getHospitalization().setHospitalizedPreviously(YesNoUnknown.YES);
            caze.getHospitalization().setAdmissionDate(null);
            caze.getHospitalization().setDischargeDate(null);
            caze.getHospitalization().setIsolated(null);
        }

        // If the district has changed and there is exactly one surveillance officer assigned to that district,
        // assign them as the new surveillance officer; assign null otherwise
        if (!caze.getDistrict().getUuid().equals(district.getUuid())) {
            List<User> districtOfficers = DatabaseHelper.getUserDao().getByDistrictAndRole(district, UserRole.SURVEILLANCE_OFFICER);
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

    @Override
    public void markAsRead(Case caze) {
        super.markAsRead(caze);
        DatabaseHelper.getPersonDao().markAsRead(caze.getPerson());
    }

    @Override
    public Case mergeOrCreate(Case source) throws DaoException {
        Case currentCase = queryUuid(source.getUuid());
        Case mergedCase = super.mergeOrCreate(source);

        // Build and send a notification when the disease has changed
        if (currentCase != null && mergedCase != null && currentCase.getDisease() != mergedCase.getDisease()) {
            Context context = DatabaseHelper.getContext();

            Intent notificationIntent = new Intent(context, CaseEditActivity.class);
            notificationIntent.putExtra(CaseEditActivity.KEY_CASE_UUID, mergedCase.getUuid());

            StringBuilder content = new StringBuilder();
            content.append("<b>").append(mergedCase.toString()).append("</b><br/>");

            PendingIntent pi = PendingIntent.getActivity(context, mergedCase.getId().intValue(), notificationIntent, 0);
            Resources r = context.getResources();

            Notification notification = new NotificationCompat.Builder(context)
                    .setTicker(r.getString(R.string.headline_case_notification))
                    .setSmallIcon(R.mipmap.ic_launcher)
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
            LocationService locationService = LocationService.getLocationService(DatabaseHelper.getContext());
            Location location = locationService.getLocation();
            if (location != null) {
                // Use the geo-coordinates of the current location object if it's not older than 15 minutes
                if (new Date().getTime() <= location.getTime() + (1000 * 60 * 15)) {
                    caze.setReportLat((float) location.getLatitude());
                    caze.setReportLon((float) location.getLongitude());
                }
            }
        }

        return super.saveAndSnapshot(caze);
    }

}
