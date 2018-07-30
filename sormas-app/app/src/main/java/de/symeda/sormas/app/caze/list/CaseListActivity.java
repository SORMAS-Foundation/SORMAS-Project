package de.symeda.sormas.app.caze.list;

import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

import java.util.Date;
import java.util.Random;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.report.MissingWeeklyReportDialog;
import de.symeda.sormas.app.util.Bundler;

public class CaseListActivity extends BaseListActivity {

    private InvestigationStatus statusFilters[] = new InvestigationStatus[]{InvestigationStatus.PENDING, InvestigationStatus.DONE, InvestigationStatus.DISCARDED};

    public static void startActivity(Context context, InvestigationStatus listFilter) {
        BaseListActivity.startActivity(context, CaseListActivity.class, buildBundle(listFilter));
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_landing_page_case_menu;
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, PageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return new Random().nextInt(100);
        //return (int)(new Random(DateTime.now().getMillis() * 1000).nextInt()/10000000);
    }

    @Override
    protected BaseListFragment buildListFragment(PageMenuItem menuItem) {
        InvestigationStatus listFilter = statusFilters[menuItem.getKey()];
        return CaseListFragment.newInstance(listFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getNewMenu().setTitle(R.string.action_new_case);
        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level2_cases_list;
    }

    @Override
    public void goToNewView() {
        EpiWeek lastEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
        User user = ConfigProvider.getUser();
        if (user.hasUserRole(UserRole.INFORMANT)
                && DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(lastEpiWeek, ConfigProvider.getUser()) == null) {

            // TODO reactivate reports
            MissingWeeklyReportDialog confirmationDialog = new MissingWeeklyReportDialog(this);
            confirmationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                @Override
                public void onOkClick(View v, Object item, View viewRoot) {
                    /*Intent intent = new Intent(CaseListActivity.this, ReportsActivity.class);
                    startActivity(intent);*/
                }
            });

            confirmationDialog.show(null);
        } else {
            CaseNewActivity.startActivity(getContext());
        }
    }

    @Override
    public boolean isEntryCreateAllowed() {
        User user = ConfigProvider.getUser();
        return user.hasUserRight(UserRight.CASE_CREATE);
    }
}
