package de.symeda.sormas.app.campaign.list;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.core.adapter.databinding.BindingPagedListAdapter;
import de.symeda.sormas.app.databinding.RowCaseListItemLayoutBinding;

public class CampaignFormDataListAdapter extends BindingPagedListAdapter<CampaignFormData, RowCaseListItemLayoutBinding> {
    public CampaignFormDataListAdapter() {
        super(R.layout.row_campaign_list_item_layout);
    }
}
