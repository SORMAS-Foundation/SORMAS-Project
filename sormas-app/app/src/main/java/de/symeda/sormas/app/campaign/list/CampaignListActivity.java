package de.symeda.sormas.app.campaign.list;

import android.content.Context;
import android.widget.AdapterView;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.PagedBaseListActivity;
import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.menu.PageMenuItem;

public class CampaignListActivity extends PagedBaseListActivity {

    public static void startActivity(Context context, InvestigationStatus listFilter) {
        BaseListActivity.startActivity(context, CampaignListActivity.class, buildBundle(0));
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, PageMenuItem menuItem, int position) {
        return 0;
    }

    @Override
    public void addFiltersToPageMenu() {

    }

    @Override
    protected PagedBaseListFragment buildListFragment(PageMenuItem menuItem) {
        return null;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_campaigns_list;
    }
}
