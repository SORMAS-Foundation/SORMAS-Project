package de.symeda.sormas.app.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;

public class EventParticipantsListArrayAdapter extends ArrayAdapter<EventParticipant> {

    private static final String TAG = EventParticipantsListArrayAdapter.class.getSimpleName();

    private final Context context;
    private int resource;
    private EventParticipant eventParticipant;
    private View convertView;

    public EventParticipantsListArrayAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resource, parent, false);
        }
        this.convertView = convertView;

        eventParticipant = (EventParticipant) getItem(position);

        TextView uuid = (TextView) convertView.findViewById(R.id.eventParticipant_uuid_li);
        uuid.setText(DataHelper.getShortUuid(eventParticipant.getUuid()));

        if (eventParticipant.getResultingCaseUuid() != null) {
            TextView caseStatus = (TextView) convertView.findViewById(R.id.eventParticipant_case_li);
            Case caze = DatabaseHelper.getCaseDao().queryUuid(eventParticipant.getResultingCaseUuid());
            caseStatus.setText(caze != null ? caze.getInvestigationStatus().toString() : "");
        }

        TextView summary = (TextView) convertView.findViewById(R.id.eventParticipant_person_li);
        summary.setText(eventParticipant.getPerson()!=null?eventParticipant.getPerson().toString()+ " ":"");

        TextView description = (TextView) convertView.findViewById(R.id.eventParticipant_info_li);
        StringBuilder sb = new StringBuilder();
        sb.append(eventParticipant.getInvolvementDescription()!=null? eventParticipant.getInvolvementDescription() + ", ":"");
        String age = eventParticipant.getPerson().getApproximateAge()!=null?eventParticipant.getPerson().getApproximateAge().toString() + " ":"";
        String ageType = eventParticipant.getPerson().getApproximateAgeType()!=null?eventParticipant.getPerson().getApproximateAgeType().toString() + ", ":", ";
        sb.append(age + ageType);
        sb.append(eventParticipant.getPerson().getSex()!=null?eventParticipant.getPerson().getSex().toString():"");
        description.setText(sb.toString());

        ImageView synchronizedIcon = (ImageView) convertView.findViewById(R.id.eventParticipant_synchronized_li);
        if (eventParticipant.isModifiedOrChildModified()) {
            synchronizedIcon.setVisibility(View.VISIBLE);
            synchronizedIcon.setImageResource(R.drawable.ic_cached_black_18dp);
        } else {
            synchronizedIcon.setVisibility(View.GONE);
        }

        updateUnreadIndicator();

        return convertView;
    }

    public void updateUnreadIndicator() {
        if (eventParticipant != null && convertView != null) {
            LinearLayout itemLayout = (LinearLayout) convertView.findViewById(R.id.event_participants_list_item_layout);
            if (eventParticipant.isUnreadOrChildUnread()) {
                itemLayout.setBackgroundResource(R.color.bgColorUnread);
            } else {
                itemLayout.setBackground(null);
            }
        }
    }

}