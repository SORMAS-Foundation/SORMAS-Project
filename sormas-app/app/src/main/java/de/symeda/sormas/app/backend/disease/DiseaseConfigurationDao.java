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

package de.symeda.sormas.app.backend.disease;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;

public class DiseaseConfigurationDao extends AbstractAdoDao<DiseaseConfiguration> {

	public DiseaseConfigurationDao(Dao<DiseaseConfiguration, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<DiseaseConfiguration> getAdoClass() {
		return DiseaseConfiguration.class;
	}

	@Override
	public String getTableName() {
		return DiseaseConfiguration.TABLE_NAME;
	}

	public List<Disease> getAllDiseases(Boolean active, Boolean primary, Boolean caseBased) {

		final Set<Disease> diseases = EnumSet.noneOf(Disease.class);

		final List<DiseaseConfiguration> diseaseConfigurations = queryForAll();

		final User currentUser = ConfigProvider.getUser();

		for (DiseaseConfiguration configuration : diseaseConfigurations) {
			final Disease limitedDisease = currentUser.getLimitedDisease();
			if (limitedDisease == null) {
				final Disease disease = configuration.getDisease();
				boolean activeFlagMatches = active.equals(configuration.getActive()) || active.equals(disease.isDefaultActive());
				boolean primaryFlagMatches = primary.equals(configuration.getPrimaryDisease()) || primary.equals(disease.isDefaultPrimary());
				boolean caseBasedFlagMatches = caseBased.equals(configuration.getCaseBased()) || caseBased.equals(disease.isDefaultCaseBased());
				if (activeFlagMatches && primaryFlagMatches && caseBasedFlagMatches) {
					diseases.add(disease);
				}
			} else if (limitedDisease == configuration.getDisease()) {
				boolean activeFlagMatches = active.equals(configuration.getActive()) || active.equals(limitedDisease.isDefaultActive());
				boolean primaryFlagMatches = primary.equals(configuration.getPrimaryDisease()) || primary.equals(limitedDisease.isDefaultPrimary());
				boolean caseBasedFlagMatches =
					caseBased.equals(configuration.getCaseBased()) || caseBased.equals(limitedDisease.isDefaultCaseBased());
				if (activeFlagMatches && primaryFlagMatches && caseBasedFlagMatches) {
					diseases.add(limitedDisease);
				}
			}
		}

		return diseases.stream().sorted(Comparator.comparing(Disease::toString)).collect(Collectors.toList());
	}

	public DiseaseConfiguration getDiseaseConfiguration(Disease disease) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(DiseaseConfiguration.DISEASE, disease);
			return (DiseaseConfiguration) builder.queryForFirst();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getDiseaseConfiguration");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void create(DiseaseConfiguration data) throws SQLException {
		super.create(data);
		DiseaseConfigurationCache.reset();
	}

	@Override
	protected void update(DiseaseConfiguration data) throws SQLException {
		super.update(data);
		DiseaseConfigurationCache.reset();
	}
}
