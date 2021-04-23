/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.campaign.data;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;

public class CampaignFormDataDao extends AbstractAdoDao<CampaignFormData> {

	public CampaignFormDataDao(Dao<CampaignFormData, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<CampaignFormData> getAdoClass() {
		return CampaignFormData.class;
	}

	@Override
	public String getTableName() {
		return CampaignFormData.TABLE_NAME;
	}

	public List<CampaignFormData> queryByCriteria(CampaignFormDataCriteria criteria, long offset, long limit) {
		try {
			return buildQueryBuilder(criteria).orderBy(CampaignFormData.FORM_DATE, false).offset(offset).limit(limit).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByCriteria on CampaignFormData");
			throw new RuntimeException(e);
		}
	}

	public long countByCriteria(CampaignFormDataCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform countByCriteria on CampaignFormData");
			throw new RuntimeException(e);
		}
	}

	private QueryBuilder<CampaignFormData, Long> buildQueryBuilder(CampaignFormDataCriteria criteria) throws SQLException {
		QueryBuilder<CampaignFormData, Long> queryBuilder = queryBuilder();

		List<Where<CampaignFormData, Long>> whereStatements = new ArrayList<>();
		Where<CampaignFormData, Long> where = queryBuilder.where();
		whereStatements.add(where.eq(AbstractDomainObject.SNAPSHOT, false));

		if (criteria.getCampaign() != null) {
			whereStatements.add(where.eq(CampaignFormData.CAMPAIGN +  "_id", criteria.getCampaign().getId()));
		}

		if (criteria.getCampaignFormMeta() != null) {
			whereStatements.add((where.eq(CampaignFormData.CAMPAIGN_FORM_META +  "_id", criteria.getCampaignFormMeta().getId())));
		}

		if (!whereStatements.isEmpty()) {
			Where<CampaignFormData, Long> whereStatement = where.and(whereStatements.size());
			queryBuilder.setWhere(whereStatement);
		}

		return queryBuilder;
	}

	@Override
	public CampaignFormData build() {
		CampaignFormData campaignFormData = super.build();
		User user = ConfigProvider.getUser();

		if (user.getRegion() != null) {
			campaignFormData.setArea(user.getRegion().getArea());
			campaignFormData.setRegion(user.getRegion());
		}
		if (user.getDistrict() != null) {
			campaignFormData.setDistrict(user.getDistrict());
		}
		if (user.getCommunity() != null) {
			campaignFormData.setCommunity(user.getCommunity());
		}

		return campaignFormData;
	}

}
