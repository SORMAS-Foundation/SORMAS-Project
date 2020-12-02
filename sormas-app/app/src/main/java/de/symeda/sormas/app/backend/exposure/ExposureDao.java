package de.symeda.sormas.app.backend.exposure;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.location.Location;

public class ExposureDao extends AbstractAdoDao<Exposure> {

	public ExposureDao(Dao<Exposure, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<Exposure> getAdoClass() {
		return Exposure.class;
	}

	@Override
	public Exposure build() {
		Exposure exposure = super.build();
		exposure.setLocation(DatabaseHelper.getLocationDao().build());
		exposure.setReportingUser(ConfigProvider.getUser());
		return exposure;
	}

	public Exposure build(ExposureType exposureType) {
		Exposure exposure = super.build();
		exposure.setExposureType(exposureType);
		exposure.setLocation(DatabaseHelper.getLocationDao().build());
		return exposure;
	}

	@Override
	public void deleteCascade(Exposure exposure) throws SQLException {
		DatabaseHelper.getLocationDao().delete(exposure.getLocation());
		super.delete(exposure);
	}

	public List<Exposure> getByEpiData(EpiData epiData) {
		if (epiData.isSnapshot()) {
			return querySnapshotsForEq(Exposure.EPI_DATA + "_id", epiData, Exposure.CHANGE_DATE, false);
		}

		return queryForEq(Exposure.EPI_DATA + "_id", epiData, Exposure.CHANGE_DATE, false);
	}

	@Override
	public Date getLatestChangeDate() {
		Date date = super.getLatestChangeDate();
		if (date == null) {
			return null;
		}

		Date locationDate = getLatestChangeDateJoin(Location.TABLE_NAME, Exposure.LOCATION);
		if (locationDate != null && locationDate.after(date)) {
			date = locationDate;
		}

		return date;
	}

	@Override
	public String getTableName() {
		return Exposure.TABLE_NAME;
	}
}
