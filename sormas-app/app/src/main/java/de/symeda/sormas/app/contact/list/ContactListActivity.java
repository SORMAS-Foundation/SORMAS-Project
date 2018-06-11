package de.symeda.sormas.app.contact.list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;

import org.joda.time.DateTime;

import java.util.Random;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.ListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.util.MenuOptionsHelper;

/**
 * Created by Orson on 07/12/2017.
 */

public class ContactListActivity extends BaseListActivity {

    private final int DATA_XML_PAGE_MENU = R.xml.data_landing_page_contact_menu; // "xml/data_landing_page_contact_menu.xml";

    private static final int MENU_INDEX_CONTACT_FOLLOWUP_ONGOING = 0;
    private static final int MENU_INDEX_CONTACT_FOLLOWUP_COMPLETED = 1;
    private static final int MENU_INDEX_CONTACT_FOLLOWUP_CANCELLED = 2;
    private static final int MENU_INDEX_CONTACT_LOST_TO_FOLLOWUP = 2;
    private static final int MENU_INDEX_CONTACT_NO_FOLLOWUP = 2;

    private FollowUpStatus statusFilters[] = new FollowUpStatus[] { FollowUpStatus.FOLLOW_UP, FollowUpStatus.COMPLETED,
            FollowUpStatus.CANCELED, FollowUpStatus.LOST,FollowUpStatus.NO_FOLLOW_UP };

    private FollowUpStatus filterStatus = null;
    private SearchBy searchBy = null;
    private String recordUuid = null;
    private BaseListActivityFragment activeFragment = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SaveSearchStrategyState(outState, searchBy);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {
        filterStatus = (FollowUpStatus) getFilterStatusArg(arguments);
        searchBy = (SearchBy) getSearchStrategyArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public BaseListActivityFragment getActiveReadFragment() {
        if (activeFragment == null) {
            IListNavigationCapsule dataCapsule = new ListNavigationCapsule(ContactListActivity.this, filterStatus, searchBy);
            activeFragment = ContactListFragment.newInstance(this, dataCapsule);
        }

        return activeFragment;
    }

    @Override
    public int getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, LandingPageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return (int)(new Random(DateTime.now().getMillis() * 1000).nextInt()/10000000);
    }

    @Override
    protected BaseListActivityFragment getNextFragment(LandingPageMenuItem menuItem) {
        FollowUpStatus status = statusFilters[menuItem.getKey()];

        if (status == null)
            return null;

        filterStatus = status;
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(ContactListActivity.this, filterStatus, searchBy);

        activeFragment = ContactListFragment.newInstance(this, dataCapsule);
        return activeFragment;
    }

    @Override
    public Enum getStatus() {
        return null;
    }

    @Override
    public boolean showStatusFrame() {
        return false;
    }

    @Override
    public boolean showTitleBar() {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getNewMenu().setTitle(R.string.action_new_contact);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleListModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level2_contacts_list;
    }

    public static void goToActivity(Context fromActivity, IListNavigationCapsule dataCapsule) {
        BaseListActivity.goToActivity(fromActivity, ContactListActivity.class, dataCapsule);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (activeFragment != null)
            activeFragment.cancelTaskExec();
    }
}
