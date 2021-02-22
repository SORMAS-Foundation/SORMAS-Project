package de.symeda.sormas.app.backend.activityascase;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.api.activityascase.ActivityAsCaseType;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.exposure.Exposure;
import de.symeda.sormas.app.backend.location.Location;

public class ActivityAsCaseDao extends AbstractAdoDao<ActivityAsCase> {

	public ActivityAsCaseDao(Dao<ActivityAsCase, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<ActivityAsCase> getAdoClass() {
		return ActivityAsCase.class;
	}

	@Override
	public ActivityAsCase build() {
		ActivityAsCase activityAsCase = super.build();
		activityAsCase.setLocation(DatabaseHelper.getLocationDao().build());
		activityAsCase.setReportingUser(ConfigProvider.getUser());
		return activityAsCase;
	}

	public ActivityAsCase build(ActivityAsCaseType activityAsCaseType) {
		ActivityAsCase activityAsCase = super.build();
		activityAsCase.setActivityAsCaseType(activityAsCaseType);
		activityAsCase.setLocation(DatabaseHelper.getLocationDao().build());
		return activityAsCase;
	}

	@Override
	public void deleteCascade(ActivityAsCase activityAsCase) throws SQLException {
		DatabaseHelper.getLocationDao().delete(activityAsCase.getLocation());
		super.delete(activityAsCase);
	}

	public List<ActivityAsCase> getByEpiData(EpiData epiData) {
		if (epiData.isSnapshot()) {
			return querySnapshotsForEq(Exposure.EPI_DATA + "_id", epiData, ActivityAsCase.CHANGE_DATE, false);
		}

		return queryForEq(ActivityAsCase.EPI_DATA + "_id", epiData, ActivityAsCase.CHANGE_DATE, false);
	}

	@Override
	public Date getLatestChangeDate() {
		Date date = super.getLatestChangeDate();
		if (date == null) {
			return null;
		}

		Date locationDate = getLatestChangeDateJoin(Location.TABLE_NAME, ActivityAsCase.LOCATION);
		if (locationDate != null && locationDate.after(date)) {
			date = locationDate;
		}

		return date;
	}

	@Override
	public String getTableName() {
		return ActivityAsCase.TABLE_NAME;
	}
}
