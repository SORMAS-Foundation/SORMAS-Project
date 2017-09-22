package de.symeda.sormas.app.contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.contact.Contact;

/**
 * Created by Stefan Szczesny on 30.11.2016.
 */
public class ContactsListArrayAdapter extends ArrayAdapter<Contact> {

    private static final String TAG = ContactsListArrayAdapter.class.getSimpleName();

    private final Context context;
    private int resource;
    private Contact contact;
    private View convertView;

    public ContactsListArrayAdapter(Context context, int resource) {
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

        contact = (Contact)getItem(position);

        TextView uuid = (TextView) convertView.findViewById(R.id.contact_uuid_li);
        uuid.setText(DataHelper.getShortUuid(contact.getUuid()));

        TextView lastContactDate = (TextView) convertView.findViewById(R.id.contact_lastContactDate_li);
        int days = DateHelper.getDaysBetween(contact.getLastContactDate(), new Date());
        String contactDateString = days+" "+ convertView.getResources().getText(R.string.label_days)+ " " + convertView.getResources().getText(R.string.label_ago);
        if(days>30) {
            contactDateString = DateHelper.formatDate(contact.getLastContactDate());
        }
        else if(days == 0) {
            contactDateString = convertView.getResources().getText(R.string.label_today).toString();
        }
        lastContactDate.setText(contactDateString);

        TextView person = (TextView) convertView.findViewById(R.id.contact_person_li);
        person.setText(contact.getPerson().toString());

        TextView information = (TextView) convertView.findViewById(R.id.contact_information_li);
        StringBuilder sb = new StringBuilder();
        sb.append(contact.getContactClassification()).append(", " + contact.getContactProximity());
        information.setText(sb.toString());

        ImageView synchronizedIcon = (ImageView) convertView.findViewById(R.id.contact_synchronized_li);
        if (contact.isModifiedOrChildModified()) {
            synchronizedIcon.setVisibility(View.VISIBLE);
            synchronizedIcon.setImageResource(R.drawable.ic_cached_black_18dp);
        } else {
            synchronizedIcon.setVisibility(View.GONE);
        }

        updateUnreadIndicator();

        return convertView;
    }

    public void updateUnreadIndicator() {
        if (contact !=  null && convertView != null) {
            LinearLayout itemLayout = (LinearLayout) convertView.findViewById(R.id.contacts_list_item_layout);
            if (contact.isUnreadOrChildUnread()) {
                itemLayout.setBackgroundResource(R.color.bgColorUnread);
            } else {
                itemLayout.setBackground(null);
            }
        }
    }

}