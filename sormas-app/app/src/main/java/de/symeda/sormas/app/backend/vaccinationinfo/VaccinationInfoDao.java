package de.symeda.sormas.app.backend.vaccinationinfo;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

public class VaccinationInfoDao extends AbstractAdoDao<VaccinationInfo> {

	public VaccinationInfoDao(Dao<VaccinationInfo, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<VaccinationInfo> getAdoClass() {
		return VaccinationInfo.class;
	}

	@Override
	public String getTableName() {
		return VaccinationInfo.TABLE_NAME;
	}
}
