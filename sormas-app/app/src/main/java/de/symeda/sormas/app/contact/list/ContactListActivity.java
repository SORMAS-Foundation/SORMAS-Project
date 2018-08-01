package de.symeda.sormas.app.contact.list;

import android.content.Context;
import android.view.Menu;
import android.widget.AdapterView;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Random;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.PageMenuItem;

public class ContactListActivity extends BaseListActivity {

    private FollowUpStatus statusFilters[] = new FollowUpStatus[]{FollowUpStatus.FOLLOW_UP, FollowUpStatus.COMPLETED,
            FollowUpStatus.CANCELED, FollowUpStatus.LOST, FollowUpStatus.NO_FOLLOW_UP};

    public static void startActivity(Context context, FollowUpStatus listFilter) {
        BaseListActivity.startActivity(context, ContactListActivity.class, buildBundle(listFilter));
    }

    @Override
    public List<PageMenuItem> getPageMenuData() {
        return PageMenuItem.fromEnum(statusFilters, getContext());
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, PageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return (int) (new Random(DateTime.now().getMillis() * 1000).nextInt() / 10000000);
    }

    @Override
    protected BaseListFragment buildListFragment(PageMenuItem menuItem) {
        FollowUpStatus listFilter = statusFilters[menuItem.getKey()];
        return ContactListFragment.newInstance(listFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getNewMenu().setTitle(R.string.action_new_contact);
        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level2_contacts_list;
    }
}
