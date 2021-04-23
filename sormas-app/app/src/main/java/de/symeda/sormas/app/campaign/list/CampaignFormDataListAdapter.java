package de.symeda.sormas.app.campaign.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.core.adapter.databinding.BindingPagedListAdapter;
import de.symeda.sormas.app.core.adapter.databinding.BindingViewHolder;
import de.symeda.sormas.app.databinding.RowCampaignListItemLayoutBinding;
import de.symeda.sormas.app.databinding.RowCaseListItemLayoutBinding;

public class CampaignFormDataListAdapter extends BindingPagedListAdapter<CampaignFormData, RowCampaignListItemLayoutBinding> {
    public CampaignFormDataListAdapter() {
        super(R.layout.row_campaign_list_item_layout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if (getItemViewType(position) == TYPE_ITEM) {
            BindingViewHolder<CampaignFormData, RowCaseListItemLayoutBinding> pagedHolder = (BindingViewHolder) holder;
            pagedHolder.setOnListItemClickListener(this.mOnListItemClickListener);
        }
    }
}
