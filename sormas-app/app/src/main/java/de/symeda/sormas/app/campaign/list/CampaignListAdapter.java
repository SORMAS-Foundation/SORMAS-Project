package de.symeda.sormas.app.campaign.list;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.core.adapter.databinding.BindingPagedListAdapter;
import de.symeda.sormas.app.databinding.RowCaseListItemLayoutBinding;

public class CampaignListAdapter extends BindingPagedListAdapter<Case, RowCaseListItemLayoutBinding> {
    public CampaignListAdapter() {
        super(R.layout.row_campaign_list_item_layout);
    }
}
