package de.symeda.sormas.app.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;


/**
 * Created by Stefan Szczesny on 26.10.2016.
 */
public class TaskEditActivity extends AppCompatActivity {

    private TaskForm taskForm;

    public TaskEditActivity() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        if(extras != null) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.sormas_root_activity_layout);
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_task) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
        }

        // setting the fragment_frame
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        taskForm = new TaskForm();
        taskForm.setArguments(getIntent().getExtras());
        ft.add(R.id.fragment_frame, taskForm).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskForm.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.group_action_help,false);
        menu.setGroupVisible(R.id.group_action_add,false);
        menu.setGroupVisible(R.id.group_action_save,true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

                return true;
            case R.id.action_save:
                Task task = (Task) taskForm.getData();

                TaskDao taskDao = DatabaseHelper.getTaskDao();
                taskDao.save(task);
                Toast.makeText(this, "task "+ DataHelper.getShortUuid(task.getUuid()) +" saved", Toast.LENGTH_SHORT).show();

                SyncTasksTask.syncTasks(getSupportFragmentManager(), null);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
