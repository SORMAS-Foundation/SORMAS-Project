package de.symeda.sormas.app.event.list;

import android.content.Context;
import android.os.Bundle;
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
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.ListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.event.edit.EventNewActivity;

public class EventListActivity extends BaseListActivity {

    private EventStatus statusFilters[] = new EventStatus[]{EventStatus.POSSIBLE, EventStatus.CONFIRMED, EventStatus.NO_EVENT};

    private EventStatus filterStatus = null;
    private SearchBy searchBy = null;
    private String recordUuid = null;
    private BaseListFragment activeFragment = null;

    @Override
    protected void onCreateInner(Bundle savedInstanceState) {
        super.onCreateInner(savedInstanceState);
        filterStatus = (EventStatus) getFilterStatusArg(savedInstanceState);
        searchBy = (SearchBy) getSearchStrategyArg(savedInstanceState);
        recordUuid = getRecordUuidArg(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveFilterStatusState(outState, filterStatus);
        saveSearchStrategyState(outState, searchBy);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    public BaseListFragment getActiveListFragment() {
        if (activeFragment == null) {
            IListNavigationCapsule dataCapsule = new ListNavigationCapsule(EventListActivity.this, filterStatus, searchBy);
            activeFragment = EventListFragment.newInstance(dataCapsule);
        }

        return activeFragment;
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_landing_page_alert_menu;
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, LandingPageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return (int) (new Random(DateTime.now().getMillis() * 1000).nextInt() / 10000000);
    }

    @Override
    protected BaseListFragment getListFragment(LandingPageMenuItem menuItem) {
        EventStatus status = statusFilters[menuItem.getKey()];

        if (status == null)
            return null;

        filterStatus = status;
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(EventListActivity.this, filterStatus, searchBy);

        activeFragment = EventListFragment.newInstance(dataCapsule);
        return activeFragment;
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
        EventNewActivity.goToActivity(this);
    }
    public static void goToActivity(Context fromActivity, IListNavigationCapsule dataCapsule) {
        BaseListActivity.goToActivity(fromActivity, EventListActivity.class, dataCapsule);
    }
}
