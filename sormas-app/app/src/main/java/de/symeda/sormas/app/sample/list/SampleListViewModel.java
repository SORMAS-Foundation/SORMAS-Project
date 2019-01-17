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

package de.symeda.sormas.app.sample.list;

import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.sample.ShipmentStatus;

public class SampleListViewModel extends ViewModel {

    private MutableLiveData<List<Sample>> samples;
    private ShipmentStatus shipmentStatus = ShipmentStatus.NOT_SHIPPED;
    private Case caze;

    public LiveData<List<Sample>> getSamples() {
        if (samples == null) {
            samples = new MutableLiveData<>();
            loadSamples();
        }

        return samples;
    }

    public LiveData<List<Sample>> getSamples(Case caze) {
        this.caze = caze;
        return getSamples();
    }

    void setShipmentStatusAndReload(ShipmentStatus shipmentStatus) {
        if (this.shipmentStatus == shipmentStatus) {
            return;
        }

        this.shipmentStatus = shipmentStatus;
        loadSamples();
    }

    private void loadSamples() {
        new LoadSamplesTask(this).execute();
    }

    private static class LoadSamplesTask extends AsyncTask<Void, Void, List<Sample>> {
        private SampleListViewModel model;

        LoadSamplesTask(SampleListViewModel model) {
            this.model = model;
        }

        @Override
        protected List<Sample> doInBackground(Void... args) {
            if (model.caze != null) {
                return DatabaseHelper.getSampleDao().queryByCase(model.caze);
            } else {
                switch (model.shipmentStatus) {
                    case NOT_SHIPPED:
                        return DatabaseHelper.getSampleDao().queryNotShipped();
                    case SHIPPED:
                        return DatabaseHelper.getSampleDao().queryShipped();
                    case RECEIVED:
                        return DatabaseHelper.getSampleDao().queryReceived();
                    case REFERRED_OTHER_LAB:
                        return DatabaseHelper.getSampleDao().queryReferred();
                    default:
                        throw new IllegalArgumentException(model.shipmentStatus.toString());
                }
            }
        }

        @Override
        protected void onPostExecute(List<Sample> data) {
            model.samples.setValue(data);
        }
    }

}
