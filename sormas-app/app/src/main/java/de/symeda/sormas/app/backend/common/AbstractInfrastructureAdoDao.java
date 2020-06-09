package de.symeda.sormas.app.backend.common;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

public abstract class AbstractInfrastructureAdoDao<ADO extends InfrastructureAdo> extends AbstractAdoDao<ADO> {

	public AbstractInfrastructureAdoDao(Dao<ADO, Long> innerDao) {
		super(innerDao);
	}

	public List<ADO> queryActiveForAll(String orderBy, boolean ascending) {
		try {
			QueryBuilder<ADO, Long> builder = queryBuilder();
			Where<ADO, Long> where = builder.where();
			where.and(where.eq(AbstractDomainObject.SNAPSHOT, false), where.eq(InfrastructureAdo.ARCHIVED, false));
			return builder.orderBy(orderBy, ascending).query();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryForAll");
			throw new RuntimeException();
		}
	}

	public List<ADO> queryActiveForAll() {
		try {
			QueryBuilder<ADO, Long> builder = queryBuilder();
			Where<ADO, Long> where = builder.where();
			where.and(where.eq(AbstractDomainObject.SNAPSHOT, false), where.eq(InfrastructureAdo.ARCHIVED, false));
			return builder.query();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<ADO> queryActiveForEq(String fieldName, Object value, String orderBy, boolean ascending) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(fieldName, value);
			where.and().eq(AbstractDomainObject.SNAPSHOT, false);
			where.and().eq(InfrastructureAdo.ARCHIVED, false).query();
			return builder.orderBy(orderBy, ascending).query();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryForEq");
			throw new RuntimeException(e);
		}
	}
}
