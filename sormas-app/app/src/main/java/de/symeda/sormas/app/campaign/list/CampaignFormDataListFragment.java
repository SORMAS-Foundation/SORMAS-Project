/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.campaign.list;

import android.view.View;

import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.campaign.read.CampaignFormDataReadActivity;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;

public class CampaignFormDataListFragment extends PagedBaseListFragment<CampaignFormDataListAdapter> implements OnListItemClickListener {

    public static CampaignFormDataListFragment newInstance() {
        return newInstance(CampaignFormDataListFragment.class, null, null);
    }

    @Override
    public CampaignFormDataListAdapter getNewListAdapter() {
        return (CampaignFormDataListAdapter) ((CampaignFormDataListActivity) getActivity()).getAdapter();
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        CampaignFormData campaignFormData = (CampaignFormData) item;
        CampaignFormDataReadActivity.startActivity(getContext(), campaignFormData.getUuid(), false);
    }
}
