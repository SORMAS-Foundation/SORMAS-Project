/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.caze.list;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import androidx.core.content.ContextCompat;
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

public class CaseListAdapter extends DataBoundAdapter<RowCaseListItemLayoutBinding> implements ISetOnListItemClickListener {

    private static final String TAG = CaseListAdapter.class.getSimpleName();

    private final Context context;
    private List<Case> data;
    private OnListItemClickListener mOnListItemClickListener;

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
        if (record.isModified() || record.getPerson().isModified()) {
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

//    public void updateUnreadIndicator(DataBoundViewHolder<RowCaseListItemLayoutBinding> holder, Case item) {
//        LayerDrawable backgroundRowItem = (LayerDrawable) ContextCompat.getDrawable(holder.context, R.drawable.background_list_activity_row);
//        Drawable unreadListItemIndicator = backgroundRowItem.findDrawableByLayerId(R.id.unreadListItemIndicator);
//
//        if (item != null) {
//            if (item.isUnreadOrChildUnread()) {
//                unreadListItemIndicator.setTint(holder.context.getResources().getColor(R.color.unreadIcon));
//            } else {
//                unreadListItemIndicator.setTint(holder.context.getResources().getColor(android.R.color.transparent));
//            }
//        }
//    }

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
