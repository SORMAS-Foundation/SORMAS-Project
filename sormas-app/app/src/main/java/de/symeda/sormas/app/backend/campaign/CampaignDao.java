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

package de.symeda.sormas.app.backend.campaign;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

public class CampaignDao extends AbstractAdoDao<Campaign> {

    public CampaignDao(Dao<Campaign, Long> innerDao) {
        super(innerDao);
    }

    @Override
    protected Class<Campaign> getAdoClass() {
		return Campaign.class;
    }

    @Override
    public String getTableName() {
        return Campaign.TABLE_NAME;
    }

    public List<Campaign> getAllActive() {
        try {
            QueryBuilder<Campaign, Long> queryBuilder = queryBuilder();

            List<Where<Campaign, Long>> whereStatements = new ArrayList<>();
            Where<Campaign, Long> where = queryBuilder.where();
            whereStatements.add(where.eq(Campaign.ARCHIVED, false));

            if (!whereStatements.isEmpty()) {
                Where<Campaign, Long> whereStatement = where.and(whereStatements.size());
                queryBuilder.setWhere(whereStatement);
            }
            return queryBuilder.orderBy(Campaign.CHANGE_DATE, false).query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getAllActive on Campaign");
            throw new RuntimeException(e);
        }
    }

    public Campaign getLastStartedCampaign() {
        try {
            QueryBuilder<Campaign, Long> queryBuilder = queryBuilder();

            List<Where<Campaign, Long>> whereStatements = new ArrayList<>();
            Where<Campaign, Long> where = queryBuilder.where();
			where.and(where.eq(Campaign.ARCHIVED, false), where.le(Campaign.START_DATE, new Date()));
			whereStatements.add(where);

            if (!whereStatements.isEmpty()) {
                Where<Campaign, Long> whereStatement = where.and(whereStatements.size());
                queryBuilder.setWhere(whereStatement);
            }
            return queryBuilder.orderBy(Campaign.START_DATE, false).queryForFirst();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getLastStartedCampaign on Campaign");
            throw new RuntimeException(e);
        }
    }
}
