package de.symeda.sormas.app.sample.landing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.symeda.sormas.app.BaseLandingActivity;
import de.symeda.sormas.app.BaseLandingActivityFragment;
import de.symeda.sormas.app.R;

/**
 * Created by Orson on 21/11/2017.
 */

public class SampleLandingActivity extends BaseLandingActivity {

    private BaseLandingActivityFragment activeFragment = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {

    }

    @Override
    public BaseLandingActivityFragment getActiveLandingFragment() throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            activeFragment = SampleLandingFragment.newInstance();
        }

        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final Menu _menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.landing_action_bar, menu);

        MenuItem newMenu = menu.findItem(R.id.action_new);
        newMenu.setVisible(false);
        newMenu.setTitle(R.string.action_new_sample);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            /*case R.id.option_menu:
                //synchronizeChangedData();
                return true;*/

            case R.id.option_menu_action_markAllAsRead:
                /*TaskDao taskDao = DatabaseHelper.getTaskDao();
                List<Task> tasks = taskDao.queryForAll();
                for (Task taskToMark : tasks) {
                    taskDao.markAsRead(taskToMark);
                }

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof TasksListFragment) {
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
    protected int getActivityTitle() {
        return R.string.heading_level1_samples_landing;
    }

    @Override
    protected int getRootActivityLayout() {
        return R.layout.activity_root_layout;
    }
}