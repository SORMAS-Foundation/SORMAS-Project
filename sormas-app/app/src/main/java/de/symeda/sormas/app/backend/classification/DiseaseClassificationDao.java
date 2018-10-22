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

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

public class DiseaseClassificationDao extends AbstractAdoDao<DiseaseClassification> {

    public DiseaseClassificationDao(Dao<DiseaseClassification, Long> innerDao) throws SQLException {
        super(innerDao);
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
