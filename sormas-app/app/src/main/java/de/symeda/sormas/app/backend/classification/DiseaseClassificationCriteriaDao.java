/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.classification;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

public class DiseaseClassificationCriteriaDao extends AbstractAdoDao<DiseaseClassificationCriteria> {

	public DiseaseClassificationCriteriaDao(Dao<DiseaseClassificationCriteria, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	public DiseaseClassificationCriteria getByDisease(Disease disease) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(DiseaseClassificationCriteria.DISEASE, disease);
			where.and().eq(AbstractDomainObject.SNAPSHOT, false).query();
			return (DiseaseClassificationCriteria) builder.queryForFirst();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform getByDisease");
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Class<DiseaseClassificationCriteria> getAdoClass() {
		return DiseaseClassificationCriteria.class;
	}

	@Override
	public String getTableName() {
		return DiseaseClassificationCriteria.TABLE_NAME;
	}
}
