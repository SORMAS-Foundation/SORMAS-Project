package de.symeda.sormas.app;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;
import de.symeda.sormas.app.util.Bundler;

public abstract class BaseReadActivity<ActivityRootEntity extends AbstractDomainObject> extends BaseActivity implements IUpdateSubHeadingTitle {

    private AsyncTask getRootEntityTask;
    private ActivityRootEntity storedRootEntity = null;
    private TextView subHeadingListActivityTitle;

    private String rootUuid;

    private BaseReadFragment activeFragment = null;
    private MenuItem editMenu = null;

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

    @Override
    protected boolean isSubActivitiy() {
        return true;
    }

    protected void onCreateInner(Bundle savedInstanceState) {

        subHeadingListActivityTitle = (TextView) findViewById(R.id.subHeadingActivityTitle);

        rootUuid = new Bundler(savedInstanceState).getRootUuid();
    }

    protected void requestRootData(final Callback.IAction<ActivityRootEntity> callback) {

        getRootEntityTask = new DefaultAsyncTask(getContext()) {
            @Override
            public void onPreExecute() {
                showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) {
                ActivityRootEntity result;
                if (rootUuid != null && !rootUuid.isEmpty()) {
                    result = queryRootData(rootUuid);
                } else {
                    result = null;
                }
                resultHolder.forItem().add(result);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                hidePreloader();

                if (taskResult.getResultStatus().isSuccess()) {
                    ITaskResultHolderIterator itemIterator = taskResult.getResult().forItem().iterator();

                    if (itemIterator.hasNext())
                        storedRootEntity = itemIterator.next();
                    else
                        storedRootEntity = null;

                    callback.call(storedRootEntity);
                }
            }
        }.executeOnThreadPool();
    }

    protected abstract ActivityRootEntity queryRootData(String recordUuid);

    protected ActivityRootEntity getStoredRootEntity() {
        return storedRootEntity;
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestRootData(new Callback.IAction<ActivityRootEntity>() {
            @Override
            public void call(ActivityRootEntity result) {
                replaceFragment(buildReadFragment(getActivePage(), result));
            }
        });
    }

    public void setSubHeadingTitle(String title) {
        String t = (title == null) ? "" : title;

        if (subHeadingListActivityTitle != null)
            subHeadingListActivityTitle.setText(t);
    }

    @Override
    public void updateSubHeadingTitle() {
        String subHeadingTitle = "";
        PageMenuItem activeMenu = getActivePage();

        if (activeFragment != null) {
            subHeadingTitle = (activeMenu == null) ? activeFragment.getSubHeadingTitle() : activeMenu.getTitle();
        }

        setSubHeadingTitle(subHeadingTitle);
    }

    @Override
    public void updateSubHeadingTitle(int titleResId) {
        setSubHeadingTitle(getApplicationContext().getResources().getString(titleResId));
    }

    @Override
    public void updateSubHeadingTitle(String title) {
        setSubHeadingTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                goToEditView();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getRootActivityLayout() {
        return R.layout.activity_root_with_title_layout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_action_menu, menu);

        editMenu = menu.findItem(R.id.action_edit);
        editMenu.setTitle(R.string.action_edit);

        processActionbarMenu();

        return true;
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (editMenu != null)
            editMenu.setVisible(activeFragment.showEditAction());
    }

    public MenuItem getEditMenu() {
        return editMenu;
    }

    public String getStatusName(Context context) {
        Enum pageStatus = getPageStatus();

        if (pageStatus != null) {
            IStatusElaborator elaborator = StatusElaboratorFactory.getElaborator(context, pageStatus);
            if (elaborator != null)
                return elaborator.getFriendlyName();
        }

        return "";
    }

    public int getStatusColorResource(Context context) {
        Enum pageStatus = getPageStatus();

        if (pageStatus != null) {
            IStatusElaborator elaborator = StatusElaboratorFactory.getElaborator(context, pageStatus);
            if (elaborator != null)
                return elaborator.getColorIndicatorResource();
        }

        return R.color.noColor;
    }

    public void replaceFragment(BaseReadFragment f) {
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
    }

    protected String getRootUuid() {
        if (storedRootEntity != null) {
            return storedRootEntity.getUuid();
        } else {
            return rootUuid;
        }
    }

    @Override
    protected boolean openPage(PageMenuItem menuItem) {
        BaseReadFragment newActiveFragment = buildReadFragment(menuItem, storedRootEntity);
        if (newActiveFragment == null)
            return false;
        replaceFragment(newActiveFragment);
        return true;
    }

    public abstract void goToEditView();

    protected abstract BaseReadFragment buildReadFragment(PageMenuItem menuItem, ActivityRootEntity activityRootData);

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (getRootEntityTask != null && !getRootEntityTask.isCancelled())
            getRootEntityTask.cancel(true);
    }
}
