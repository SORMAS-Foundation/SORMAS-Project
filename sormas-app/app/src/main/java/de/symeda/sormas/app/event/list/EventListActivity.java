package de.symeda.sormas.app.event.list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.event.landing.EventLandingToListCapsule;
import de.symeda.sormas.app.util.NavigationHelper;

/**
 * Created by Orson on 07/12/2017.
 */

public class EventListActivity extends BaseListActivity {

    private EventStatus filterStatus = null;
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

        /*if (findViewById(R.id.fragment_frame) != null) {
            // setting the fragment_frame
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            fragment = new EventListFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantHelper.ARG_FILTER_STATUS, status);

            fragment.setArguments(bundle); //getIntent().getExtras()
            ft.replace(R.id.fragment_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }*/
    }

    @Override
    protected void initializeActivity(Bundle arguments) {
        filterStatus = (EventStatus) getFilterStatusArg(arguments);
        searchBy = (SearchBy) getSearchStrategyArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public BaseListActivityFragment getActiveReadFragment() throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            EventListCapsule dataCapsule = new EventListCapsule(EventListActivity.this, filterStatus, searchBy);
            activeFragment = EventListFragment.newInstance(this, dataCapsule);
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
        getNewMenu().setTitle(R.string.action_new_event);

        return true;
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_action_bar, menu);

        MenuItem listMenu = menu.findItem(R.id.action_new);
        listMenu.setVisible(false);
        listMenu.setTitle(R.string.action_new_event);

        return true;*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // TODO check parent activity intent as soon as the minimum API level has been increased to 16
                //Intent intent = new Intent(this, EventsLandingActivity.class);
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

            case R.id.action_new:
                /*EpiWeek lastEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
                User user = ConfigProvider.getUser();
                if (user.getUserRole() == UserRole.INFORMANT && DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(lastEpiWeek, ConfigProvider.getUser()) == null) {
                    AlertDialog noLastWeeklyReportDialog = buildNoLastWeeklyReportDialog();
                    noLastWeeklyReportDialog.show();
                } else {
                    showCaseNewView();
                }*/

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
        return R.string.heading_level2_events_list;
    }

    public static void goToActivity(Context fromActivity, EventLandingToListCapsule dataCapsule) {
        BaseListActivity.goToActivity(fromActivity, EventListActivity.class, dataCapsule);
    }
}
