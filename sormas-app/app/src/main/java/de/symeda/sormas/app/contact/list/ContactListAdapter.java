/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.contact.list;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.core.adapter.databinding.BindingPagedListAdapter;
import de.symeda.sormas.app.core.adapter.databinding.BindingViewHolder;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;
import de.symeda.sormas.app.databinding.RowReadContactListItemLayoutBinding;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;

public class ContactListAdapter extends BindingPagedListAdapter<Contact, RowReadContactListItemLayoutBinding> {

	private FollowUpStatus currentListFilter;

	public ContactListAdapter(FollowUpStatus initialListFilter) {
		super(R.layout.row_read_contact_list_item_layout);
		this.currentListFilter = initialListFilter;
	}

	public ContactListAdapter() {
		super(R.layout.row_read_contact_list_item_layout);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		if (getItemViewType(position) == TYPE_ITEM) {
			BindingViewHolder<Contact, RowReadContactListItemLayoutBinding> pagedHolder = (BindingViewHolder) holder;
			Contact item = getItem(position);

			pagedHolder.setOnListItemClickListener(this.mOnListItemClickListener);

			indicateContactClassification(pagedHolder.binding.contactClassificationIcon, item);

			if (item.isModifiedOrChildModified()) {
				pagedHolder.binding.imgSyncIcon.setVisibility(View.VISIBLE);
				pagedHolder.binding.imgSyncIcon.setImageResource(R.drawable.ic_sync_blue_24dp);
			} else {
				pagedHolder.binding.imgSyncIcon.setVisibility(View.GONE);
			}

			if (DiseaseConfigurationCache.getInstance().hasFollowUp(item.getDisease())
				&& (currentListFilter == null || currentListFilter != FollowUpStatus.NO_FOLLOW_UP)) {
				int numberOfVisits = DatabaseHelper.getVisitDao().getVisitCount(item, null);
				int numberOfCooperativeVisits = DatabaseHelper.getVisitDao().getVisitCount(item, VisitStatus.COOPERATIVE);

				pagedHolder.binding.numberOfVisits.setText(
					String.format(
						pagedHolder.binding.getRoot().getResources().getString(R.string.number_of_visits_long_format),
						numberOfCooperativeVisits,
						numberOfVisits - numberOfCooperativeVisits));
			} else {
				pagedHolder.binding.numberOfVisits.setVisibility(View.GONE);
			}

		}

		// TODO #704
//        updateUnreadIndicator(holder, record);
	}

//    public void updateUnreadIndicator(DataBoundViewHolder<RowReadContactListItemLayoutBinding> holder, Contact item) {
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

	public void indicateContactClassification(ImageView img, Contact record) {
		Resources resources = img.getContext().getResources();
		Drawable drw = ContextCompat.getDrawable(img.getContext(), R.drawable.indicator_status_circle);
		StatusElaborator elaborator = StatusElaboratorFactory.getElaborator(record.getContactClassification());
		drw.setColorFilter(resources.getColor(elaborator.getColorIndicatorResource()), PorterDuff.Mode.SRC_OVER);
		img.setBackground(drw);
	}

	public void setCurrentListFilter(FollowUpStatus currentListFilter) {
		this.currentListFilter = currentListFilter;
	}
}
