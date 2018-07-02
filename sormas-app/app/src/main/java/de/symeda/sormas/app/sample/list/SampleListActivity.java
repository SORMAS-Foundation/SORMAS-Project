package de.symeda.sormas.app.sample.list;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;

import org.joda.time.DateTime;

import java.util.Random;

import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.ListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.util.MenuOptionsHelper;

public class SampleListActivity extends BaseListActivity {

    private final int DATA_XML_PAGE_MENU = R.xml.data_landing_page_sample_menu; // "xml/data_landing_page_sample_menu.xml";

    private ShipmentStatus statusFilters[] = new ShipmentStatus[] {
            ShipmentStatus.NOT_SHIPPED, ShipmentStatus.SHIPPED,
            ShipmentStatus.RECEIVED, ShipmentStatus.REFERRED_OTHER_LAB
    };

    private ShipmentStatus filterStatus = null;
    private SearchBy searchBy = null;
    private BaseListFragment activeFragment = null;
    private String recordUuid = null;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveFilterStatusState(outState, filterStatus);
        saveSearchStrategyState(outState, searchBy);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {
        filterStatus = (ShipmentStatus) getFilterStatusArg(arguments);
        searchBy = (SearchBy) getSearchStrategyArg(arguments);
    }

    @Override
    public BaseListFragment getActiveListFragment() {
        if (activeFragment == null) {
            IListNavigationCapsule dataCapsule = new ListNavigationCapsule(SampleListActivity.this, filterStatus, searchBy);
            activeFragment = SampleListFragment.newInstance(dataCapsule);
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
    protected BaseListFragment getListFragment(LandingPageMenuItem menuItem) {
        ShipmentStatus status = statusFilters[menuItem.getKey()];

        if (status == null)
            return null;

        filterStatus = status;
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(SampleListActivity.this, filterStatus, searchBy);

        activeFragment = SampleListFragment.newInstance(dataCapsule);
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
        getNewMenu().setTitle(R.string.action_new_sample);

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
        return R.string.heading_level2_samples_list;
    }

    public static void goToActivity(Context fromActivity, IListNavigationCapsule dataCapsule) {
        BaseListActivity.goToActivity(fromActivity, SampleListActivity.class, dataCapsule);
    }
}
