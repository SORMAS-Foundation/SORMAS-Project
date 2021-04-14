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

import java.util.List;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentCampaignDataNewLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;

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
        record.setArea(record.getRegion().getArea());
        contentBinding.setData(record);

        Item campaignItem = record.getCampaign() != null ? DataUtils.toItem(record.getCampaign()) : null;

        if (campaignItem != null && !initialCampaigns.contains(campaignItem)) {
            initialCampaigns.add(campaignItem);
        }

        contentBinding.campaignFormDataCampaign.initializeSpinner(initialCampaigns, record.getCampaign());

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
