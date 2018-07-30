package de.symeda.sormas.app.sample.list;

import android.content.Context;
import android.view.Menu;
import android.widget.AdapterView;

import org.joda.time.DateTime;

import java.util.Random;

import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.event.list.EventListActivity;
import de.symeda.sormas.app.sample.ShipmentStatus;

public class SampleListActivity extends BaseListActivity {

    private ShipmentStatus statusFilters[] = new ShipmentStatus[]{
            ShipmentStatus.NOT_SHIPPED, ShipmentStatus.SHIPPED,
            ShipmentStatus.RECEIVED, ShipmentStatus.REFERRED_OTHER_LAB
    };

    public static void startActivity(Context context, ShipmentStatus listFilter) {
        BaseListActivity.startActivity(context, SampleListActivity.class, buildBundle(listFilter));
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
        ShipmentStatus listFilter = statusFilters[menuItem.getKey()];
        return SampleListFragment.newInstance(listFilter);
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
}
