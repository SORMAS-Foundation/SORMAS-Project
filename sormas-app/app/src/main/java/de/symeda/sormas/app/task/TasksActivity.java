package de.symeda.sormas.app.task;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.AbstractRootTabActivity;

public class TasksActivity extends AbstractRootTabActivity {

    private TasksListFilterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.tasks_activity_layout);
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.main_menu_tasks));
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new TasksListFilterAdapter(getSupportFragmentManager());
        createTabViews(adapter);
        pager.setCurrentItem(currentTab);

        SyncTasksTask.syncTasks(getSupportFragmentManager(), this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tasks_action_bar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_reload:
                SyncTasksTask.syncTasks(getSupportFragmentManager(), this);
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
