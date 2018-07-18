package de.symeda.sormas.app.sample.list;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.widget.AdapterView;

import org.joda.time.DateTime;

import java.util.Random;

import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.ListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.shared.ShipmentStatus;

public class SampleListActivity extends BaseListActivity {

    private ShipmentStatus statusFilters[] = new ShipmentStatus[]{
            ShipmentStatus.NOT_SHIPPED, ShipmentStatus.SHIPPED,
            ShipmentStatus.RECEIVED, ShipmentStatus.REFERRED_OTHER_LAB
    };

    private SearchBy searchBy = null;
    private String recordUuid = null;

    @Override
    protected void onCreateInner(Bundle savedInstanceState) {
        searchBy = (SearchBy) getSearchStrategyArg(savedInstanceState);
        recordUuid = getRecordUuidArg(savedInstanceState);
        super.onCreateInner(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveSearchStrategyState(outState, searchBy);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_landing_page_sample_menu;
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, PageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return (int) (new Random(DateTime.now().getMillis() * 1000).nextInt() / 10000000);
    }

    @Override
    protected BaseListFragment buildListFragment(PageMenuItem menuItem) {
        ShipmentStatus status = statusFilters[menuItem.getKey()];
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(SampleListActivity.this, status, searchBy);
        return SampleListFragment.newInstance(dataCapsule);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getNewMenu().setTitle(R.string.action_new_sample);
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
