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

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.NonUniqueResultException;

import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.sample.ShipmentStatus;

/**
 * Created by Mate Strysewske on 06.02.2017.
 */

public class SampleDao extends AbstractAdoDao<Sample> {

    public SampleDao(Dao<Sample, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Sample> getAdoClass() {
        return Sample.class;
    }

    @Override
    public Sample build() {
        throw new UnsupportedOperationException();
    }

    public Sample build(Case associatedCase) {
        Sample sample = super.build();
        sample.setAssociatedCase(associatedCase);
        sample.setReportDateTime(new Date());
        sample.setReportingUser(ConfigProvider.getUser());
        sample.setPathogenTestResult(PathogenTestResultType.PENDING);
        return sample;
    }

    public List<Sample> queryByCase(Case caze) {
        if (caze.isSnapshot()) {
            throw new IllegalArgumentException("Does not support snapshot entities");
        }

        try {
            return queryBuilder()
                    .orderBy(Sample.SAMPLE_DATE_TIME, true)
                    .where().eq(Sample.ASSOCIATED_CASE + "_id", caze)
                    .and().eq(AbstractDomainObject.SNAPSHOT, false)
                    .query();
        } catch (SQLException e) {
            android.util.Log.e(getTableName(), "Could not perform queryByCase on Sample");
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTableName() {
        return Sample.TABLE_NAME;
    }

    @Override
    public Sample saveAndSnapshot(Sample sample) throws DaoException {
        if (Boolean.TRUE.equals(sample.getPathogenTestingRequested()) && sample.getPathogenTestResult() == null) {
            sample.setPathogenTestResult(PathogenTestResultType.PENDING);
        }
        return super.saveAndSnapshot(sample);
    }

    public long countByCriteria(SampleCriteria criteria) {
        try {
            return buildQueryBuilder(criteria).countOf();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform countByCriteria on Sample");
            throw new RuntimeException(e);
        }
    }

    public List<Sample> queryByCriteria(SampleCriteria criteria, long offset, long limit) {
        try {
            return buildQueryBuilder(criteria).orderBy(Sample.SAMPLE_DATE_TIME, true)
                    .offset(offset).limit(limit).query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform queryByCriteria on Sample");
            throw new RuntimeException(e);
        }
    }

    public Sample getReferredFrom(String sampleUuid) {
        try {
            QueryBuilder qb = queryBuilder();
            qb.where().eq(Sample.REFERRED_TO_UUID, sampleUuid)
                    .and().eq(AbstractDomainObject.SNAPSHOT, false);
            return (Sample) qb.queryForFirst();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getReferredFrom on Sample");
            throw new RuntimeException(e);
        }
    }

    private QueryBuilder<Sample, Long> buildQueryBuilder(SampleCriteria criteria) throws SQLException {
        QueryBuilder<Sample, Long> queryBuilder = queryBuilder();
        Where<Sample, Long> where = queryBuilder.where().eq(AbstractDomainObject.SNAPSHOT, false);

        if (criteria.getCaze() != null) {
            where.and().eq(Sample.ASSOCIATED_CASE + "_id", criteria.getCaze());
        } else {
            if (criteria.getShipmentStatus() != null) {
                switch (criteria.getShipmentStatus()) {
                    case NOT_SHIPPED:
                        where.and().and(
                                where.eq(Sample.SHIPPED, false),
                                where.eq(Sample.RECEIVED, false),
                                where.or(
                                        where.eq(Sample.REFERRED_TO_UUID, ""),
                                        where.isNull(Sample.REFERRED_TO_UUID)
                                )
                        );
                        break;
                    case SHIPPED:
                        where.and().and(
                                where.eq(Sample.SHIPPED, true),
                                where.eq(Sample.RECEIVED, false),
                                where.or(
                                        where.eq(Sample.REFERRED_TO_UUID, ""),
                                        where.isNull(Sample.REFERRED_TO_UUID)
                                )
                        );
                        break;
                    case RECEIVED:
                        where.and().and(
                                where.eq(Sample.RECEIVED, true),
                                where.or(
                                        where.eq(Sample.REFERRED_TO_UUID, ""),
                                        where.isNull(Sample.REFERRED_TO_UUID)
                                )
                        );
                        break;
                    case REFERRED_OTHER_LAB:
                        where.and().and(
                                where.isNotNull(Sample.REFERRED_TO_UUID),
                                where.ne(Sample.REFERRED_TO_UUID, "")
                        );
                        break;
                    default:
                        throw new IllegalArgumentException(criteria.getShipmentStatus().toString());
                }
            }
        }

        queryBuilder.setWhere(where);
        return queryBuilder;
    }

    public void deleteSampleAndAllDependingEntities(String sampleUuid) throws SQLException {
        deleteSampleAndAllDependingEntities(queryUuidWithEmbedded(sampleUuid));
    }

    public void deleteSampleAndAllDependingEntities(Sample sample) throws SQLException {
        // Cancel if not in local database
        if (sample == null) {
            return;
        }

        // Delete all pathogen tests of this sample
        List<PathogenTest> pathogenTests = DatabaseHelper.getSampleTestDao().queryBySample(sample);
        for (PathogenTest pathogenTest : pathogenTests) {
            DatabaseHelper.getSampleTestDao().deleteCascade(pathogenTest);
        }

        // Delete all additional tests of this sample
        List<AdditionalTest> additionalTests = DatabaseHelper.getAdditionalTestDao().queryBySample(sample);
        for (AdditionalTest additionalTest : additionalTests) {
            DatabaseHelper.getAdditionalTestDao().deleteCascade(additionalTest);
        }

        deleteCascade(sample);
    }

    public int getSampleCountByCaseId (Long caseId){

        try {
            return (int) queryBuilder().where().eq(Sample.ASSOCIATED_CASE + "_id", caseId).countOf();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getSampleCountByCaseUuid on Sample");
            throw new RuntimeException(e);
        }
    }

    public Sample queryByLabSampleId(String labSampleId) {

        try {

            List<Sample> results = queryBuilder().where().eq(Sample.LAB_SAMPLE_ID, labSampleId)
                    .and().eq(AbstractDomainObject.SNAPSHOT, false).query();
            if (results.size() == 0) {
                return null;
            } else if (results.size() == 1) {
                return results.get(0);
            } else {
                Log.e(getTableName(), "Found multiple results for labSampleId: " + labSampleId);
                throw new NonUniqueResultException("Found multiple results for labSampleId: " + labSampleId);
            }
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform queryByLabSampleId");
            throw new RuntimeException(e);
        }
    }
}
