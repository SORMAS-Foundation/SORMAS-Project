package de.symeda.sormas.app.sample.list;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.sample.landing.SampleLandingToListCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.util.NavigationHelper;

/**
 * Created by Orson on 07/12/2017.
 */

public class SampleListActivity extends BaseListActivity {

    private ShipmentStatus filterStatus = null;
    private SearchBy searchBy = null;
    private BaseListActivityFragment activeFragment = null;
    private String recordUuid = null;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SaveSearchStrategyState(outState, searchBy);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {
        filterStatus = (ShipmentStatus) getFilterStatusArg(arguments);
        searchBy = (SearchBy) getSearchStrategyArg(arguments);
    }

    @Override
    public BaseListActivityFragment getActiveReadFragment() throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            SampleListCapsule dataCapsule = new SampleListCapsule(SampleListActivity.this, filterStatus, searchBy);
            activeFragment = SampleListFragment.newInstance(this, dataCapsule);
        }

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
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_action_bar, menu);

        MenuItem listMenu = menu.findItem(R.id.action_new);
        listMenu.setVisible(false);
        listMenu.setTitle(R.string.action_new_sample);

        return true;*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // TODO check parent activity intent as soon as the minimum API level has been increased to 16
                //Intent intent = new Intent(this, SampleLandingActivity.class);
                //startActivity(intent);
                if (activeFragment != null)
                    activeFragment.cancelTaskExec();

                NavigationHelper.navigateUpFrom(this);

                return true;
            case R.id.option_menu_action_sync:
                //synchronizeChangedData();
                return true;

            case R.id.option_menu_action_markAllAsRead:
                /*CaseDao caseDao = DatabaseHelper.getCaseDao();
                PersonDao personDao = DatabaseHelper.getPersonDao();
                List<Case> cases = caseDao.queryForAll();
                for (Case caseToMark : cases) {
                    caseDao.markAsRead(caseToMark);
                }

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof CasesListFragment) {
                        fragment.onResume();
                    }
                }*/
                return true;

            // Report problem button
            case R.id.action_report:
                /*UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                AlertDialog dialog = userReportDialog.create();
                dialog.show();*/

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getRootActivityLayout() {
        return R.layout.activity_root_with_title_layout;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level2_samples_list;
    }

    public static void goToActivity(Context fromActivity, SampleLandingToListCapsule dataCapsule) {
        BaseListActivity.goToActivity(fromActivity, SampleListActivity.class, dataCapsule);
    }
}
