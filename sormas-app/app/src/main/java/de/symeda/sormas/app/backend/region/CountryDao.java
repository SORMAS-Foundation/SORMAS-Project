package de.symeda.sormas.app.backend.region;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.infrastructure.AbstractInfrastructureAdoDao;
import de.symeda.sormas.app.backend.infrastructure.InfrastructureAdo;

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

	public List<Country> queryActiveBySubcontinent(Subcontinent subcontinent) {
		try {
			QueryBuilder<Country, Long> builder = queryBuilder();
			Where<Country, Long> where = builder.where();
			where.and(
				where.eq(AbstractDomainObject.SNAPSHOT, false),
				where.eq(InfrastructureAdo.ARCHIVED, false),
				where.eq(Country.SUBCONTINENT + "_id", subcontinent));

			return builder.orderBy(Country.NAME, true).query();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryActiveBySubcontinent");
			throw new RuntimeException(e);
		}
	}
}
