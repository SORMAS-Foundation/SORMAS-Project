/*
 * This file is part of SORMAS®.
 *
 * SORMAS® is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SORMAS® is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SORMAS®.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.classification;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

public class DiseaseClassificationDao extends AbstractAdoDao<DiseaseClassification> {

    public DiseaseClassificationDao(Dao<DiseaseClassification, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    public DiseaseClassification getByDisease(Disease disease) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.eq(DiseaseClassification.DISEASE, disease);
            where.and().eq(AbstractDomainObject.SNAPSHOT, false).query();
            return (DiseaseClassification) builder.queryForFirst();
        } catch (SQLException | IllegalArgumentException e) {
            Log.e(getTableName(), "Could not perform getByDisease");
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Class<DiseaseClassification> getAdoClass() {
        return DiseaseClassification.class;
    }

    @Override
    public String getTableName() {
        return DiseaseClassification.TABLE_NAME;
    }

}
