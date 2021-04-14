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

package de.symeda.sormas.app.backend.campaign.form;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.campaign.Campaign;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

public class CampaignFormMetaDao extends AbstractAdoDao<CampaignFormMeta> {

	public CampaignFormMetaDao(Dao<CampaignFormMeta, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<CampaignFormMeta> getAdoClass() {
		return CampaignFormMeta.class;
	}

	@Override
	public String getTableName() {
		return CampaignFormMeta.TABLE_NAME;
	}

	public List<CampaignFormMeta> getAllFormsForCampaign(Campaign campaign) {
		try {
			QueryBuilder<CampaignFormMeta, Long> queryBuilder = queryBuilder();

			queryBuilder.distinct();
			QueryBuilder<CampaignFormData, Long> campaignFormDataQueryBuilder = DatabaseHelper.getCampaignFormDataDao().queryBuilder();
			if (campaign != null) {
				campaignFormDataQueryBuilder.where().eq(CampaignFormData.CAMPAIGN_ID, campaign.getId());
			}

			return queryBuilder.join(campaignFormDataQueryBuilder).orderBy(CampaignFormMeta.FORM_NAME, false).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getAllFormsForCampaign on CampaignFormMeta");
			throw new RuntimeException(e);
		}
	}
}
