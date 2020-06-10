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

package de.symeda.sormas.app.backend.sample;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

public class AdditionalTestDao extends AbstractAdoDao<AdditionalTest> {

	public AdditionalTestDao(Dao<AdditionalTest, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<AdditionalTest> getAdoClass() {
		return AdditionalTest.class;
	}

	public AdditionalTest queryMostRecentBySample(Sample sample) {
		if (sample == null) {
			return null;
		}

		if (sample.isSnapshot()) {
			throw new IllegalArgumentException("Does not support snapshot entities");
		}

		try {
			return queryBuilder().orderBy(AdditionalTest.TEST_DATE_TIME, false)
				.where()
				.eq(AdditionalTest.SAMPLE + "_id", sample)
				.and()
				.eq(AbstractDomainObject.SNAPSHOT, false)
				.queryForFirst();
		} catch (SQLException e) {
			android.util.Log.e(getTableName(), "Could not perform queryMostRecentBySample on AdditionalTest");
			throw new RuntimeException(e);
		}
	}

	public List<AdditionalTest> queryBySample(Sample sample) {
		if (sample.isSnapshot()) {
			throw new IllegalArgumentException("Does not support snapshot entities");
		}

		try {
			return queryBuilder().orderBy(AdditionalTest.TEST_DATE_TIME, true)
				.where()
				.eq(AdditionalTest.SAMPLE + "_id", sample)
				.and()
				.eq(AbstractDomainObject.SNAPSHOT, false)
				.query();
		} catch (SQLException e) {
			android.util.Log.e(getTableName(), "Could not perform queryBySample on AdditionalTest");
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getTableName() {
		return AdditionalTest.TABLE_NAME;
	}
}
