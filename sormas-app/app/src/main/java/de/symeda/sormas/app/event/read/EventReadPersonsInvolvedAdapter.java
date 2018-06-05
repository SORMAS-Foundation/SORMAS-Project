package de.symeda.sormas.app.event.read;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.databinding.RowReadEventPersonsInvolvedItemLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.event.EventParticipant;

/**
 * Created by Orson on 26/12/2017.
 */



public class EventReadPersonsInvolvedAdapter extends DataBoundAdapter<RowReadEventPersonsInvolvedItemLayoutBinding> {

    private static final String TAG = EventReadPersonsInvolvedAdapter.class.getSimpleName();

    private final Context context;
    private List<EventParticipant> data = new ArrayList<>();
    private OnListItemClickListener mOnListItemClickListener;
    //private ActionCallback mActionCallback;

    private LayerDrawable backgroundRowItem;
    private Drawable unreadListItemIndicator;

    public EventReadPersonsInvolvedAdapter(Context context, int rowLayout, OnListItemClickListener onListItemClickListener) {
        this(context, rowLayout, onListItemClickListener, new ArrayList<EventParticipant>());
    }

    public EventReadPersonsInvolvedAdapter(Context context, int rowLayout, OnListItemClickListener onListItemClickListener, List<EventParticipant> data) {
        super(rowLayout);
        this.context = context;
        this.mOnListItemClickListener = onListItemClickListener;

        if (data == null)
            this.data = new ArrayList<>();
        else
            this.data = new ArrayList<>(data);
    }

    @Override
    protected void bindItem(DataBoundViewHolder<RowReadEventPersonsInvolvedItemLayoutBinding> holder,
                            int position, List<Object> payloads) {


        EventParticipant record = data.get(position);
        holder.setData(record);
        holder.setOnListItemClickListener(this.mOnListItemClickListener);


        //holder.binding.setCallback(mOnListItemClickListener);

        //holder.itemView.callOnClick();

        //Sync Icon
        if (record.isModifiedOrChildModified()) {
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

    public void updateUnreadIndicator(DataBoundViewHolder<RowReadEventPersonsInvolvedItemLayoutBinding> holder, EventParticipant item) {
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
}