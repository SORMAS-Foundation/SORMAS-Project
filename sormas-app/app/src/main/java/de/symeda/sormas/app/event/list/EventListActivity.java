package de.symeda.sormas.app.event.list;

import android.content.Context;
import android.view.Menu;
import android.widget.AdapterView;

import org.joda.time.DateTime;

import java.util.Random;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.list.CaseListActivity;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.event.edit.EventNewActivity;

public class EventListActivity extends BaseListActivity {

    private EventStatus statusFilters[] = new EventStatus[]{EventStatus.POSSIBLE, EventStatus.CONFIRMED, EventStatus.NO_EVENT};

    public static void startActivity(Context context, EventStatus listFilter) {
        BaseListActivity.startActivity(context, EventListActivity.class, buildBundle(listFilter));
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_landing_page_alert_menu;
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, PageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return (int) (new Random(DateTime.now().getMillis() * 1000).nextInt() / 10000000);
    }

    @Override
    protected BaseListFragment buildListFragment(PageMenuItem menuItem) {
        EventStatus listFilter = statusFilters[menuItem.getKey()];
        return EventListFragment.newInstance(listFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getNewMenu().setTitle(R.string.action_new_event);
        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level2_events_list;
    }

    @Override
    public boolean isEntryCreateAllowed() {
        User user = ConfigProvider.getUser();
        return user.hasUserRight(UserRight.EVENT_CREATE);
    }

    @Override
    public void goToNewView() {
        EventNewActivity.startActivity(this);
    }
}
