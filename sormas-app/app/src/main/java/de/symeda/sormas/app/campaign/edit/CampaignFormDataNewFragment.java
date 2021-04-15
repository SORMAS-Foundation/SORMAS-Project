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

package de.symeda.sormas.app.campaign.edit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlCheckBoxField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.databinding.FragmentCampaignDataNewLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.TextViewBindingAdapters;

import static de.symeda.sormas.app.campaign.edit.CampaignFormDataEditUtils.createControlCheckBoxField;
import static de.symeda.sormas.app.campaign.edit.CampaignFormDataEditUtils.createControlTextEditField;
import static de.symeda.sormas.app.campaign.edit.CampaignFormDataEditUtils.getCampaignFormDataEntry;

public class CampaignFormDataNewFragment extends BaseEditFragment<FragmentCampaignDataNewLayoutBinding, CampaignFormData, CampaignFormData> {

    private CampaignFormData record;
    private List<Item> initialCampaigns;
    private List<Item> initialAreas;
    private List<Item> initialRegions;
    private List<Item> initialDistricts;
    private List<Item> initialCommunities;

    public static CampaignFormDataNewFragment newInstance(CampaignFormData activityRootData) {
        return newInstance(CampaignFormDataNewFragment.class, null, activityRootData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        final LinearLayout dynamicLayout = view.findViewById(R.id.dynamicLayout);

        final CampaignFormMeta campaignFormMeta = DatabaseHelper.getCampaignFormMetaDao().queryForId(record.getCampaignFormMeta().getId());
        final List<CampaignFormDataEntry> formValues = record.getFormValues();
        final Map<String, String> formValuesMap = new HashMap<>();
        formValues.forEach(campaignFormDataEntry -> formValuesMap.put(campaignFormDataEntry.getId(), campaignFormDataEntry.getValue().toString()));

        for (CampaignFormElement campaignFormElement : campaignFormMeta.getCampaignFormElements()) {
            CampaignFormElementType type = CampaignFormElementType.fromString(campaignFormElement.getType());

            if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                String value = formValuesMap.get(campaignFormElement.getId());
                if (value != null) {
                    ControlPropertyField dynamicField = null;
                    if (type == CampaignFormElementType.YES_NO) {
                        dynamicField = createControlCheckBoxField(campaignFormElement, requireContext());
                        ControlCheckBoxField.setValue((ControlCheckBoxField) dynamicField, Boolean.valueOf(value));
                    } else {
                        dynamicField = createControlTextEditField(campaignFormElement, requireContext());
                        ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                    }
                    dynamicField.setShowCaption(true);
                    dynamicLayout.addView(dynamicField, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    dynamicField.addValueChangedListener(field -> {
                        final CampaignFormDataEntry campaignFormDataEntry = getCampaignFormDataEntry(formValues, campaignFormElement);
                        campaignFormDataEntry.setValue(field.getValue());
                    });
                } else {
                    Log.e(getClass().getName(), "No form value for element id : " + campaignFormElement.getId());
                }
            } else if (type == CampaignFormElementType.SECTION) {
                dynamicLayout.addView(new ImageView(requireContext(), null, R.style.FullHorizontalDividerStyle));
            } else if (type == CampaignFormElementType.LABEL) {
                TextView textView = new TextView(requireContext());
                TextViewBindingAdapters.setHtmlValue(textView, campaignFormElement.getCaption());
                dynamicLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        }
        return view;
    }


    @Override
    public int getEditLayout() {
        return R.layout.fragment_campaign_data_new_layout;
    }

    @Override
    public CampaignFormData getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {

        record = getActivityRootData();

        initialCampaigns = DataUtils.toItems(DatabaseHelper.getCampaignDao().queryActiveForAll());

        initialAreas = InfrastructureDaoHelper.loadAreas();
        initialRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
        initialDistricts = InfrastructureDaoHelper.loadDistricts(record.getRegion());
        initialCommunities = InfrastructureDaoHelper.loadCommunities(record.getDistrict());
    }

    @Override
    protected void onLayoutBinding(FragmentCampaignDataNewLayoutBinding contentBinding) {
        contentBinding.setData(record);

        Item campaignItem = record.getCampaign() != null ? DataUtils.toItem(record.getCampaign()) : null;

        if (campaignItem != null && !initialCampaigns.contains(campaignItem)) {
            initialCampaigns.add(campaignItem);
        }

        contentBinding.campaignFormDataCampaign.initializeSpinner(initialCampaigns, record.getCampaign());

        contentBinding.campaignFormDataFormDate.initializeDateField(getFragmentManager());

        InfrastructureDaoHelper.initializeRegionAreaFields(
                contentBinding.campaignFormDataArea,
                initialAreas,
                record.getArea(),
                contentBinding.campaignFormDataRegion,
                initialRegions,
                record.getRegion(),
                contentBinding.campaignFormDataDistrict,
                initialDistricts,
                record.getDistrict(),
                contentBinding.campaignFormDataCommunity,
                initialCommunities,
                record.getCommunity());
    }
}
