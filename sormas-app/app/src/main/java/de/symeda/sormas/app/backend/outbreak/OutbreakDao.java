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

package de.symeda.sormas.app.backend.outbreak;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.task.Task;

public class OutbreakDao extends AbstractAdoDao<Outbreak> {

    public OutbreakDao(Dao<Outbreak,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Outbreak> getAdoClass() {
        return Outbreak.class;
    }

    @Override
    public String getTableName() {
        return Outbreak.TABLE_NAME;
    }

    public boolean hasOutbreak(District district, Disease disease) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and(
                    where.eq(Outbreak.DISTRICT, district),
                    where.eq(Outbreak.DISEASE, disease)
            );
            int result = (int) builder.countOf();
            return result > 0;
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getNumberOfCasesForEpiWeekAndDisease");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Outbreak saveAndSnapshot(Outbreak source) throws DaoException {
        throw new UnsupportedOperationException();
    }

    public void deleteOutbreakAndAllDependingEntities(String outbreakUuid) throws SQLException {
        Outbreak outbreak = queryUuidWithEmbedded(outbreakUuid);

        // Cancel if not in local database
        if (outbreak == null) {
            return;
        }

        deleteCascade(outbreak);
    }
}
