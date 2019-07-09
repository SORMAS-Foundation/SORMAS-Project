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

package de.symeda.sormas.app.backend.sample;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;

public class PathogenTestDao extends AbstractAdoDao<PathogenTest> {

    public PathogenTestDao(Dao<PathogenTest, Long> innerDao) {
        super(innerDao);
    }

    @Override
    protected Class<PathogenTest> getAdoClass() {
        return PathogenTest.class;
    }

    @Override
    public PathogenTest build() {
        throw new UnsupportedOperationException();
    }

    public PathogenTest build(Sample associatedSample) {
        PathogenTest pathogenTest = super.build();
        pathogenTest.setSample(associatedSample);
        pathogenTest.setTestDateTime(new Date());
        pathogenTest.setLabUser(ConfigProvider.getUser());
        return pathogenTest;
    }

    public PathogenTest queryMostRecentBySample(Sample sample) {
        if (sample == null) {
            return null;
        }

        if (sample.isSnapshot()) {
            throw new IllegalArgumentException("Does not support snapshot entities");
        }

        try {
            List<PathogenTest> tests = queryBuilder()
                    .orderBy(PathogenTest.TEST_DATE_TIME, false)
                    .where().eq(PathogenTest.SAMPLE + "_id", sample)
                    .and().eq(AbstractDomainObject.SNAPSHOT, false)
                    .query();
            if (!tests.isEmpty()) {
                return tests.get(0);
            } else {
                return null;
            }
        } catch (SQLException e) {
            android.util.Log.e(getTableName(), "Could not perform queryMostRecentBySample on PathogenTest");
            throw new RuntimeException(e);
        }
    }

    public List<PathogenTest> queryBySample(Sample sample) {
        if (sample.isSnapshot()) {
            throw new IllegalArgumentException("Does not support snapshot entities");
        }

        try {
            return queryBuilder()
                    .orderBy(PathogenTest.TEST_DATE_TIME, true)
                    .where().eq(PathogenTest.SAMPLE + "_id", sample)
                    .and().eq(AbstractDomainObject.SNAPSHOT, false)
                    .query();
        } catch (SQLException e) {
            android.util.Log.e(getTableName(), "Could not perform queryBySample on PathogenTest");
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTableName() {
        return PathogenTest.TABLE_NAME;
    }

}
