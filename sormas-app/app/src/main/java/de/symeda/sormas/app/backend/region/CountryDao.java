package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.app.backend.common.AbstractInfrastructureAdoDao;

public class CountryDao extends AbstractInfrastructureAdoDao<Country> {

	public CountryDao(Dao<Country, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<Country> getAdoClass() {
		return Country.class;
	}

	@Override
	public String getTableName() {
		return Country.TABLE_NAME;
	}

	@Override
	public Country saveAndSnapshot(Country source) {
		throw new UnsupportedOperationException();
	}
}
