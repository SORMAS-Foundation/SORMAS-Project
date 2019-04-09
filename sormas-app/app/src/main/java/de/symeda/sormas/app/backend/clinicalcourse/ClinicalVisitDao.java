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

package de.symeda.sormas.app.backend.clinicalcourse;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

public class ClinicalVisitDao extends AbstractAdoDao<ClinicalVisit> {

    public ClinicalVisitDao(Dao<ClinicalVisit, Long> innerDao) {
        super(innerDao);
    }

    public List<ClinicalVisit> findBy(ClinicalVisitCriteria criteria) {
        try {
            return buildQueryBuilder(criteria).orderBy(ClinicalVisit.VISIT_DATE_TIME, true).query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform findBy on ClinicalVisit");
            throw new RuntimeException(e);
        }
    }

    private QueryBuilder<ClinicalVisit, Long> buildQueryBuilder(ClinicalVisitCriteria criteria) throws SQLException {
        QueryBuilder<ClinicalVisit, Long> queryBuilder = queryBuilder();
        Where<ClinicalVisit, Long> where = queryBuilder.where().eq(AbstractDomainObject.SNAPSHOT, false);

        if (criteria.getClinicalCourse() != null) {
            where.and().eq(ClinicalVisit.CLINICAL_COURSE + "_id", criteria.getClinicalCourse());
        }

        queryBuilder.setWhere(where);
        return queryBuilder;
    }

    @Override
    protected Class<ClinicalVisit> getAdoClass() {
        return ClinicalVisit.class;
    }

    @Override
    public String getTableName() {
        return ClinicalVisit.TABLE_NAME;
    }

}
