package de.symeda.sormas.app.sample.list;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.core.adapter.databinding.ISetOnListItemClickListener;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.RowSampleListItemLayoutBinding;

public class SampleListAdapter extends DataBoundAdapter<RowSampleListItemLayoutBinding> implements ISetOnListItemClickListener {

    private List<Sample> data;
    private OnListItemClickListener mOnListItemClickListener;

    public SampleListAdapter(int rowLayout, OnListItemClickListener onListItemClickListener, List<Sample> data) {
        super(rowLayout);
        this.mOnListItemClickListener = onListItemClickListener;

        if (data == null)
            this.data = new ArrayList<>();
        else
            this.data = new ArrayList<>(data);
    }

    @Override
    protected void bindItem(DataBoundViewHolder<RowSampleListItemLayoutBinding> holder,
                            int position, List<Object> payloads) {

        Sample record = data.get(position);
        holder.setData(record);
        holder.binding.setTestResultMessage(getSampleTestResultMessage(holder.context, record));
        holder.setOnListItemClickListener(this.mOnListItemClickListener);

        //indicateShipmentStatus(holder.binding.imgShipmentStatusIcon, record);

        //Sync Icon
        if (record.isModifiedOrChildModified()) {
            holder.binding.imgSyncIcon.setVisibility(View.VISIBLE);
            holder.binding.imgSyncIcon.setImageResource(R.drawable.ic_sync_blue_24dp);
        } else {
            holder.binding.imgSyncIcon.setVisibility(View.GONE);
        }

        // TODO #704
//        updateUnreadIndicator(holder, record);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

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
//    }

    private String getSampleTestResultMessage(Context context, Sample record) {
        SampleTest mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(record);
        if (record.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE) {
            return context.getResources().getString(R.string.inadequate_specimen_cond);
        } else {
            if (mostRecentTest != null) {
                return mostRecentTest.getTestResult().toString();
            } else {
                return context.getResources().getString(R.string.no_recent_test);
            }
        }
    }

    public void addAll(List<Sample> data) {
        if (data == null)
            return;

        this.data.addAll(data);
    }

    public void replaceAll(List<Sample> data) {
        if (data == null)
            return;

        this.data.clear();
        this.data.addAll(data);
    }

    public void clear() {
        if (this.data == null)
            return;

        this.data.clear();
    }

    @Override
    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.mOnListItemClickListener = onListItemClickListener;
    }
}
