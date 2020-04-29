/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.util.Consumer;

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

    protected static Bundler buildBundle(String rootUuid, boolean finishInsteadOfUpNav) {
        return buildBundle(rootUuid, 0).setFinishInsteadOfUpNav(finishInsteadOfUpNav);
    }

    protected static Bundler buildBundle(String rootUuid, int activePageKey) {
        return buildBundle(activePageKey).setRootUuid(rootUuid);
    }

    protected static Bundler buildBundle(String rootUuid, Enum section) {
        return buildBundle(rootUuid, section.ordinal());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        new Bundler(outState).setRootUuid(rootUuid);
    }

    @Override
    protected boolean isSubActivity() {
        return true;
    }

    protected void onCreateInner(Bundle savedInstanceState) {

        subHeadingListActivityTitle = (TextView) findViewById(R.id.subHeadingActivityTitle);

        rootUuid = new Bundler(savedInstanceState).getRootUuid();
    }

    protected void requestRootData(final Consumer<ActivityRootEntity> callback) {

        if (rootUuid != null && !rootUuid.isEmpty()) {
            storedRootEntity = queryRootEntity(rootUuid);
        } else {
            storedRootEntity = null;
        }

        // This should not happen; however, it still might under certain circumstances
        // (user clicking a notification for a task they have no access to anymore); in
        // this case, the activity should be closed.
        if (storedRootEntity == null) {
            finish();
        }

        callback.accept(storedRootEntity);
    }

    protected abstract ActivityRootEntity queryRootEntity(String recordUuid);

    protected ActivityRootEntity getStoredRootEntity() {
        return storedRootEntity;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        requestRootData(new Consumer<ActivityRootEntity>() {
            @Override
            public void accept(ActivityRootEntity result) {
                replaceFragment(buildReadFragment(getActivePage(), result), false);
            }
        });

        updatePageMenu();
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
            PageMenuItem activeMenu = getActivePage();
            subHeadingTitle = (activeMenu == null) ? activeFragment.getSubHeadingTitle() : activeMenu.getTitle();
        }

        setSubHeadingTitle(subHeadingTitle);
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

    protected void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (editMenu != null)
            editMenu.setVisible(activeFragment.showEditAction());
    }

    public BaseReadFragment getActiveFragment() {
        return activeFragment;
    }

    public MenuItem getEditMenu() {
        return editMenu;
    }

    public String getStatusName() {
        Enum pageStatus = getPageStatus();

        if (pageStatus != null) {
            StatusElaborator elaborator = StatusElaboratorFactory.getElaborator(pageStatus);
            if (elaborator != null)
                return elaborator.getFriendlyName(getContext());
        }

        return "";
    }

    public int getStatusColorResource() {
        Enum pageStatus = getPageStatus();

        if (pageStatus != null) {
            StatusElaborator elaborator = StatusElaboratorFactory.getElaborator(pageStatus);
            if (elaborator != null)
                return elaborator.getColorIndicatorResource();
        }

        return R.color.noColor;
    }

    public void replaceFragment(BaseReadFragment f, boolean allowBackNavigation) {
        BaseFragment previousFragment = activeFragment;
        activeFragment = f;

        if (activeFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
            ft.replace(R.id.fragment_frame, activeFragment);
            if (allowBackNavigation && previousFragment != null) {
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
        replaceFragment(newActiveFragment, true);
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
