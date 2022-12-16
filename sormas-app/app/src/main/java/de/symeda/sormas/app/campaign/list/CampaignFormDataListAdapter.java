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
                 //   updateUnreadIndicator(holder, position);
        }


    }
//
//    public void updateUnreadIndicator(DataBoundViewHolder<RowSampleListItemLayoutBinding> holder, Sample item) {
//        backgroundRowItem = (LayerDrawable) ContextCompat.getDrawable(holder.context, R.drawable.background_list_activity_row);
//        unreadListItemIndicator = backgroundRowItem.findDrawableByLayerId(R.id.unreadListItemIndicator);
//
//        if (item != null) {
//            if (item.isUnreadOrChildUnread()) {
//                unreadListItemIndicator.setTint(holder.context.getResources().getColor(R.color.unreadIcon));
//            } else {
//                unreadListItemIndicator.setTint(holder.context.getResources().getColor(android.R.color.transparent));
//            }
//        }

}
