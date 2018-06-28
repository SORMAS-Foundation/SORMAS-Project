package de.symeda.sormas.app.caze.list;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.core.adapter.databinding.ISetOnListItemClickListener;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.RowCaseListItemLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.app.backend.caze.Case;

/**
 * Created by Orson on 05/12/2017.
 */

public class CaseListAdapter extends DataBoundAdapter<RowCaseListItemLayoutBinding> implements ISetOnListItemClickListener {

    private static final String TAG = CaseListAdapter.class.getSimpleName();

    private final Context context;
    private List<Case> data = new ArrayList<>();
    private OnListItemClickListener mOnListItemClickListener;

    private LayerDrawable backgroundRowItem;
    private Drawable unreadListItemIndicator;


    public CaseListAdapter(Context context, int rowLayout, OnListItemClickListener onListItemClickListener, List<Case> data) {
        super(rowLayout);
        this.context = context;
        this.mOnListItemClickListener = onListItemClickListener;

        if (data == null)
            this.data = new ArrayList<>();
        else
            this.data = new ArrayList<>(data);
    }

    @Override
    protected void bindItem(DataBoundViewHolder<RowCaseListItemLayoutBinding> holder,
                            int position, List<Object> payloads) {

        Case record = data.get(position);
        holder.setData(record);
        holder.setOnListItemClickListener(this.mOnListItemClickListener);

        indicateCaseClassification(holder.binding.imgCaseStatusIcon, record);

        //Sync Icon
        if (record.isModified()) {
            holder.binding.imgSyncIcon.setVisibility(View.VISIBLE);
            holder.binding.imgSyncIcon.setImageResource(R.drawable.ic_sync_blue_24dp);
        } else {
            holder.binding.imgSyncIcon.setVisibility(View.GONE);
        }

        updateUnreadIndicator(holder, record);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateUnreadIndicator(DataBoundViewHolder<RowCaseListItemLayoutBinding> holder, Case item) {
        backgroundRowItem = (LayerDrawable) ContextCompat.getDrawable(holder.context, R.drawable.background_list_activity_row);
        unreadListItemIndicator = backgroundRowItem.findDrawableByLayerId(R.id.unreadListItemIndicator);

        if (item != null) {
            if (item.isUnreadOrChildUnread()) {
                unreadListItemIndicator.setTint(holder.context.getResources().getColor(R.color.unreadIcon));
            } else {
                unreadListItemIndicator.setTint(holder.context.getResources().getColor(android.R.color.transparent));
            }
        }
    }

    public void indicateCaseClassification(ImageView imgCaseClassificationIcon, Case item) {
        Resources resources = imgCaseClassificationIcon.getContext().getResources();
        Drawable drw = (Drawable)ContextCompat.getDrawable(imgCaseClassificationIcon.getContext(), R.drawable.indicator_status_circle);

        if (item.getCaseClassification() == CaseClassification.NOT_CLASSIFIED) {
            drw.setColorFilter(resources.getColor(R.color.indicatorCaseNotYetClassified), PorterDuff.Mode.SRC_OVER);
        } else if (item.getCaseClassification() == CaseClassification.SUSPECT) {
            drw.setColorFilter(resources.getColor(R.color.indicatorCaseSuspected), PorterDuff.Mode.SRC_OVER);
        } else if (item.getCaseClassification() == CaseClassification.PROBABLE) {
            drw.setColorFilter(resources.getColor(R.color.indicatorCaseProbable), PorterDuff.Mode.SRC_OVER);
        } else if (item.getCaseClassification() == CaseClassification.CONFIRMED) {
            drw.setColorFilter(resources.getColor(R.color.indicatorCaseConfirmed), PorterDuff.Mode.SRC_OVER);
        } else if (item.getCaseClassification() == CaseClassification.NO_CASE) {
            drw.setColorFilter(resources.getColor(R.color.indicatorNotACase), PorterDuff.Mode.SRC_OVER);
        }

        imgCaseClassificationIcon.setBackground(drw);
    }

    public Case getCase(int position) {
        if (position < 0)
            return null;

        if (position >= this.data.size())
            return null;

        return (Case)this.data.get(position);
    }

    public void addAll(List<Case> data) {
        if (data == null)
            return;

        this.data.addAll(data);
    }

    public void replaceAll(List<Case> data) {
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
