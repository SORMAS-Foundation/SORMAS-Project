package de.symeda.sormas.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.util.Consumer;

public abstract class BaseEditActivity<ActivityRootEntity extends AbstractDomainObject> extends BaseActivity implements IUpdateSubHeadingTitle {

    private LinearLayout notificationFrame;
    private TextView subHeadingListActivityTitle;

    private BaseEditFragment activeFragment = null;

    private AsyncTask getRootEntityTask;
    private ActivityRootEntity storedRootEntity = null;

    private String rootUuid;

    private MenuItem saveMenu = null;
    private MenuItem addMenu = null;

    @Override
    protected boolean isSubActivitiy() {
        return true;
    }

    @Override
    public boolean isEditing() {
        return true;
    }

    protected static Bundler buildBundle(String rootUuid) {
        return buildBundle(rootUuid, 0);
    }

    protected static Bundler buildBundle(String rootUuid, int activePageKey) {
        return buildBundle(activePageKey).setRootUuid(rootUuid);
    }

    protected static Bundler buildBundle(String rootUuid, Enum section) {
        return buildBundle(rootUuid, section.ordinal());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        new Bundler(outState).setRootUuid(rootUuid);
    }

    protected void onCreateInner(Bundle savedInstanceState) {
        subHeadingListActivityTitle = (TextView) findViewById(R.id.subHeadingActivityTitle);
        notificationFrame = (LinearLayout) findViewById(R.id.notificationFrame);

        rootUuid = new Bundler(savedInstanceState).getRootUuid();

        if (notificationFrame != null) {
            notificationFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);

                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestRootData(new Consumer<ActivityRootEntity>() {
            @Override
            public void accept(ActivityRootEntity result) {
                replaceFragment(buildEditFragment(getActivePage(), result));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                saveData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void requestRootData(final Consumer<ActivityRootEntity> callback) {

        getRootEntityTask = new DefaultAsyncTask(getContext()) {
            @Override
            public void onPreExecute() {
                showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) {
                ActivityRootEntity result;
                if (rootUuid != null && !rootUuid.isEmpty()) {
                    result = queryRootEntity(rootUuid);

                    if (result == null) {
                        result = buildRootEntity();
                    }
                } else {
                    result = buildRootEntity();
                }
                resultHolder.forItem().add(result);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                hidePreloader();

                if (taskResult.getResultStatus().isSuccess()) {
                    ITaskResultHolderIterator itemIterator = taskResult.getResult().forItem().iterator();

                    boolean hadRootEntity = storedRootEntity != null;
                    if (itemIterator.hasNext())
                        storedRootEntity = itemIterator.next();
                    else
                        storedRootEntity = null;

                    if (storedRootEntity != null
                            && !storedRootEntity.isNew()
                            && storedRootEntity.isUnreadOrChildUnread()) {
                        // TODO #704 do in background and retrieve entity again
//                        DatabaseHelper.getAdoDao(storedRootEntity.getClass()).markAsReadWithCast(storedRootEntity);
                        if (hadRootEntity) {
                            NotificationHelper.showNotification(BaseEditActivity.this, NotificationType.WARNING,
                                    String.format(getResources().getString(R.string.snackbar_entity_overridden), storedRootEntity.getEntityName()));
                        }
                    }

                    callback.accept(storedRootEntity);
                }
            }
        }.executeOnThreadPool();
    }

    protected abstract ActivityRootEntity queryRootEntity(String recordUuid);

    protected abstract ActivityRootEntity buildRootEntity();

    protected ActivityRootEntity getStoredRootEntity() {
        return storedRootEntity;
    }

    protected String getRootUuid() {
        if (storedRootEntity != null) {
            return storedRootEntity.getUuid();
        } else {
            return rootUuid;
        }
    }

    protected BaseEditFragment getActiveFragment() {
        return activeFragment;
    }

    public void setSubHeadingTitle(String title) {
        String t = (title == null) ? "" : title;

        if (subHeadingListActivityTitle != null)
            subHeadingListActivityTitle.setText(t);
    }

    @Override
    public void updateSubHeadingTitle() {
        String subHeadingTitle = "";

        if (activeFragment != null) {
            subHeadingTitle = (getActivePage() == null) ? activeFragment.getSubHeadingTitle() : getActivePage().getTitle();
        }

        setSubHeadingTitle(subHeadingTitle);
    }

    @Override
    public void updateSubHeadingTitle(String title) {
        setSubHeadingTitle(title);
    }

    @Override
    protected int getRootActivityLayout() {
        return R.layout.activity_root_with_title_edit_layout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_menu, menu);

        saveMenu = menu.findItem(R.id.action_save);
        addMenu = menu.findItem(R.id.action_new);

        processActionbarMenu();

        return true;
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (saveMenu != null)
            saveMenu.setVisible(activeFragment.isShowSaveAction());

        if (addMenu != null)
            addMenu.setVisible(activeFragment.isShowAddAction());
    }

    public MenuItem getSaveMenu() {
        return saveMenu;
    }

    public MenuItem getAddMenu() {
        return addMenu;
    }

    public void replaceFragment(BaseEditFragment f) {
        BaseFragment previousFragment = activeFragment;
        activeFragment = f;

        if (activeFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
            ft.replace(R.id.fragment_frame, activeFragment);
            if (previousFragment != null) {
                ft.addToBackStack(null);
            }
            ft.commit();

            processActionbarMenu();
        }

        updateStatusFrame();
        updatePageMenu();
    }

    protected abstract BaseEditFragment buildEditFragment(PageMenuItem menuItem, ActivityRootEntity activityRootData);

    @Override
    protected boolean openPage(PageMenuItem menuItem) {
        BaseEditFragment newActiveFragment = buildEditFragment(menuItem, storedRootEntity);
        if (newActiveFragment == null)
            return false;
        replaceFragment(newActiveFragment);
        return true;
    }

    public abstract void saveData();

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (getRootEntityTask != null && !getRootEntityTask.isCancelled())
            getRootEntityTask.cancel(true);
    }
}