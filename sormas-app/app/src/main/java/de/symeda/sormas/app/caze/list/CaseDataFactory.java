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

package de.symeda.sormas.app.caze.list;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import de.symeda.sormas.api.caze.InvestigationStatus;

public class CaseDataFactory extends DataSource.Factory {

    private MutableLiveData<CaseDataSource> mutableDataSource;
    private CaseDataSource caseDataSource;

    public CaseDataFactory() {
        this.mutableDataSource = new MutableLiveData<>();
    }

    @Override
    public DataSource create() {
        caseDataSource = new CaseDataSource(InvestigationStatus.PENDING);
        mutableDataSource.postValue(caseDataSource);
        return caseDataSource;
    }

    public MutableLiveData<CaseDataSource> getMutableDataSource() {
        return mutableDataSource;
    }

}
