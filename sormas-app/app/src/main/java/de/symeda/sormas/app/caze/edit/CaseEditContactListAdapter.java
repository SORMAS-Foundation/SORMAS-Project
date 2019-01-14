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

package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.core.adapter.databinding.ISetOnListItemClickListener;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;
import de.symeda.sormas.app.databinding.RowReadContactListItemLayoutBinding;

public class CaseEditContactListAdapter extends DataBoundAdapter<RowReadContactListItemLayoutBinding> implements ISetOnListItemClickListener {

    private static final String TAG = CaseEditContactListAdapter.class.getSimpleName();

    private List<Contact> data;
    private OnListItemClickListener mOnListItemClickListener;

    public CaseEditContactListAdapter(int rowLayout, OnListItemClickListener onListItemClickListener, List<Contact> data) {
        super(rowLayout);
        this.mOnListItemClickListener = onListItemClickListener;

        if (data == null)
            this.data = new ArrayList<>();
        else
            this.data = new ArrayList<>(data);
    }


    @Override
    protected void bindItem(DataBoundViewHolder<RowReadContactListItemLayoutBinding> holder,
                            int position, List<Object> payloads) {

        Contact record = data.get(position);
        holder.setData(record);
        holder.setOnListItemClickListener(this.mOnListItemClickListener);

        indicateContactClassification(holder.binding.contactClassificationIcon, record);


        //Sync Icon
        if (record.isModifiedOrChildModified()) {
            holder.binding.imgSyncIcon.setVisibility(View.VISIBLE);
            holder.binding.imgSyncIcon.setImageResource(R.drawable.ic_sync_blue_24dp);
        } else {
            holder.binding.imgSyncIcon.setVisibility(View.GONE);
        }

        // Number of visits
        if (DiseaseHelper.hasContactFollowUp(record.getCaseDisease(), null)) {
            int numberOfVisits = DatabaseHelper.getVisitDao().getVisitCount(record, null);
            int numberOfCooperativeVisits = DatabaseHelper.getVisitDao().getVisitCount(record, VisitStatus.COOPERATIVE);

            holder.binding.numberOfVisits.setText(String.format(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, "numberOfVisitsLongFormat"),
                    numberOfCooperativeVisits, numberOfVisits - numberOfCooperativeVisits));
        } else {
            holder.binding.numberOfVisits.setVisibility(View.GONE);
        }

        // TODO #704
//        updateUnreadIndicator(holder, record);

    }

    @Override
    public int getItemCount() {
        return data.size();
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
        Drawable drw = (Drawable)ContextCompat.getDrawable(img.getContext(), R.drawable.indicator_status_circle);
        StatusElaborator elaborator = StatusElaboratorFactory.getElaborator(record.getContactClassification());
        drw.setColorFilter(resources.getColor(elaborator.getColorIndicatorResource()), PorterDuff.Mode.SRC_OVER);
        img.setBackground(drw);
    }

    public Contact getContact(int position) {
        if (position < 0)
            return null;

        if (position >= this.data.size())
            return null;

        return (Contact)this.data.get(position);
    }

    public void addAll(List<Contact> data) {
        if (data == null)
            return;

        this.data.addAll(data);
    }

    public void replaceAll(List<Contact> data) {
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