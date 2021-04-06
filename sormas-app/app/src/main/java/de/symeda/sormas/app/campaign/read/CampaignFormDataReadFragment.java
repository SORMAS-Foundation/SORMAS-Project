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

package de.symeda.sormas.app.campaign.read;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.controls.ControlTextReadField;
import de.symeda.sormas.app.databinding.FragmentCampaignDataReadLayoutBinding;

public class CampaignFormDataReadFragment extends BaseReadFragment<FragmentCampaignDataReadLayoutBinding, CampaignFormData, CampaignFormData> {

    private CampaignFormData record;

    public static CampaignFormDataReadFragment newInstance(CampaignFormData activityRootData) {
        return newInstance(
                CampaignFormDataReadFragment.class, null, activityRootData);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
    }

    @Override
    protected void onLayoutBinding(FragmentCampaignDataReadLayoutBinding contentBinding) {
        contentBinding.setData(record);

        final LinearLayout dynamicLayout = getView().findViewById(R.id.dynamicLayout);
        final CampaignFormMeta campaignFormMeta = DatabaseHelper.getCampaignFormMetaDao().queryForId(record.getCampaignFormMeta().getId());
        final List<CampaignFormDataEntry> formValues = record.getFormValues();
        final Map<String, Object> formValuesMap = new HashMap<>();
        formValues.forEach(campaignFormDataEntry -> formValuesMap.put(campaignFormDataEntry.getId(), campaignFormDataEntry.getValue()));
        campaignFormMeta.getCampaignFormElements().forEach(campaignFormElement -> {
            ControlTextReadField controlTextReadField = new ControlTextReadField(getActivity());
            controlTextReadField.setValue(formValuesMap.get(campaignFormElement.getId()));
            dynamicLayout.addView(controlTextReadField);
        });
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_campaign_data_read_layout;
    }

    @Override
    public CampaignFormData getPrimaryData() {
        return record;
    }
}
