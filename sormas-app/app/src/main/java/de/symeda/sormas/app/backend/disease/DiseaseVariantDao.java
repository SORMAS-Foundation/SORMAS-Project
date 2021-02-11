/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.disease;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;

public class DiseaseVariantDao extends AbstractAdoDao<DiseaseVariant> {

    public DiseaseVariantDao(Dao<DiseaseVariant, Long> innerDao) {
        super(innerDao);
    }

    @Override
    protected Class<DiseaseVariant> getAdoClass() {
        return DiseaseVariant.class;
    }

    @Override
    public String getTableName() {
        return DiseaseVariant.TABLE_NAME;
    }

    public List<DiseaseVariant> getAllByDisease(Disease disease) {
        if (disease == null || !disease.isVariantAllowed()) {
            return Collections.emptyList();
        }

        try {
            QueryBuilder<DiseaseVariant, Long> diseaseVariantBuilder = queryBuilder();
            Where<DiseaseVariant, Long> where = diseaseVariantBuilder.where();
            where.eq(DiseaseVariant.DISEASE, disease);
            return diseaseVariantBuilder.query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getAllByDisease");
            throw new RuntimeException(e);
        }
    }
}
