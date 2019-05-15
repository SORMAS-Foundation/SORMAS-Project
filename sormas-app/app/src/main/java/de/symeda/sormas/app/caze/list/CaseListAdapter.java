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

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.core.adapter.databinding.BindingPagedListAdapter;
import de.symeda.sormas.app.core.adapter.databinding.BindingViewHolder;
import de.symeda.sormas.app.databinding.RowCaseListItemLayoutBinding;

public class CaseListAdapter extends BindingPagedListAdapter<Case, RowCaseListItemLayoutBinding> {

    CaseListAdapter() {
        super(R.layout.row_case_list_item_layout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if (getItemViewType(position) == TYPE_ITEM) {
            BindingViewHolder<Case, RowCaseListItemLayoutBinding> pagedHolder = (BindingViewHolder)holder;
            Case item = getItem(position);

            pagedHolder.setOnListItemClickListener(this.mOnListItemClickListener);

            indicateCaseClassification(pagedHolder.binding.imgCaseStatusIcon, item);

            // #1123 Temporary code to get more information
            if (item.getPerson() == null) {
                throw new NullPointerException("getPerson() is null; Case: " + item.getUuid() + "; Position in list: " + position
                        + "; Change date: " + item.getChangeDate() + "; Creation date: " + item.getCreationDate()
                        + "; Symptoms null?: " + (item.getSymptoms() == null));
            }

            //Sync Icon
            if (item.isModified() || item.getPerson().isModified()) {
                pagedHolder.binding.imgSyncIcon.setVisibility(View.VISIBLE);
                pagedHolder.binding.imgSyncIcon.setImageResource(R.drawable.ic_sync_blue_24dp);
            } else {
                pagedHolder.binding.imgSyncIcon.setVisibility(View.GONE);
            }

            // TODO #704
            //updateUnreadIndicator(holder, record);
        }
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
        Drawable drw = ContextCompat.getDrawable(imgCaseClassificationIcon.getContext(), R.drawable.indicator_status_circle);

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

}
