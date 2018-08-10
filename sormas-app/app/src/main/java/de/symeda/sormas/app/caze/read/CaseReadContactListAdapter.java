package de.symeda.sormas.app.caze.read;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;
import de.symeda.sormas.app.databinding.RowReadContactListItemLayoutBinding;
import de.symeda.sormas.app.event.read.EventReadTaskListAdapter;

public class CaseReadContactListAdapter extends DataBoundAdapter<RowReadContactListItemLayoutBinding> {

    private static final String TAG = EventReadTaskListAdapter.class.getSimpleName();

    private List<Contact> data;
    private OnListItemClickListener mOnListItemClickListener;

    public CaseReadContactListAdapter(int rowLayout, OnListItemClickListener onListItemClickListener, List<Contact> data) {
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

        indicateContactClassification(holder.binding.imgContactClassificationStatusIcon, record);


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

//    public void updateUnreadIndicator(DataBoundViewHolder<RowReadContactListItemLayoutBinding> holder, Contact item) {
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

    public void indicateContactClassification(ImageView img, Contact record) {
        Resources resources = img.getContext().getResources();
        Drawable drw = (Drawable)ContextCompat.getDrawable(img.getContext(), R.drawable.indicator_status_circle);
        StatusElaborator elaborator = StatusElaboratorFactory.getElaborator(record.getContactClassification());
        drw.setColorFilter(resources.getColor(elaborator.getColorIndicatorResource()), PorterDuff.Mode.SRC_OVER);
        img.setBackground(drw);
    }

}