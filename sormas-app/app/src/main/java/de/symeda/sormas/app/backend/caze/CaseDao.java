package de.symeda.sormas.app.backend.caze;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.CaseEditActivity;

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

}
