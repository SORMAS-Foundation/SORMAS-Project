package de.symeda.sormas.app.campaign.list;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.backend.therapy.Treatment;
import de.symeda.sormas.app.core.adapter.databinding.BindingPagedListAdapter;
import de.symeda.sormas.app.core.adapter.databinding.BindingViewHolder;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
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
            BindingViewHolder<CampaignFormData, RowCampaignListItemLayoutBinding> pagedHolder = (BindingViewHolder) holder;
            pagedHolder.setOnListItemClickListener(this.mOnListItemClickListener);
            CampaignFormData item = getItem(position);

            pagedHolder.setOnListItemClickListener(this.mOnListItemClickListener);

            if (item.isModifiedOrChildModified()) {
                pagedHolder.binding.imgSyncIcon.setVisibility(View.VISIBLE);
                pagedHolder.binding.imgSyncIcon.setImageResource(R.drawable.ic_sync_blue_24dp);
            } else {
                pagedHolder.binding.imgSyncIcon.setVisibility(View.GONE);
            }
        }


    }


}
