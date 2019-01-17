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

import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

class CaseListViewModel extends ViewModel {

    private MutableLiveData<List<Case>> cases;
    private InvestigationStatus investigationStatus = InvestigationStatus.PENDING;

    LiveData<List<Case>> getCases() {
        if (cases == null) {
            cases = new MutableLiveData<>();
            loadCases();
        }

        return cases;
    }

    void setInvestigationStatusAndReload(InvestigationStatus investigationStatus) {
        if (cases == null) {
            throw new RuntimeException("Cases must be initialized before calling setInvestigationStatusAndReload");
        }

        if (this.investigationStatus == investigationStatus) {
            return;
        }

        this.investigationStatus = investigationStatus;
        loadCases();
    }

    private void loadCases() {
        new LoadCasesTask(this).execute();
    }

    private static class LoadCasesTask extends AsyncTask<Void, Void, List<Case>> {
        private CaseListViewModel model;

        LoadCasesTask(CaseListViewModel model) {
            this.model = model;
        }

        @Override
        protected List<Case> doInBackground(Void... args) {
            return DatabaseHelper.getCaseDao().queryForEq(Case.INVESTIGATION_STATUS, model.investigationStatus, Case.REPORT_DATE, false);
        }

        @Override
        protected void onPostExecute(List<Case> data) {
            model.cases.setValue(data);
        }
    }

}
