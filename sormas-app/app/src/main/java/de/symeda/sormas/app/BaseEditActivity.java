/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
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
	private MenuItem newMenu = null;

	@Override
	protected boolean isSubActivity() {
		return true;
	}

	@Override
	public boolean isEditing() {
		return true;
	}

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

	protected void onCreateInner(Bundle savedInstanceState) {
		subHeadingListActivityTitle = (TextView) findViewById(R.id.subHeadingActivityTitle);
		notificationFrame = (LinearLayout) findViewById(R.id.notification_frame);

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
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();

		requestRootData(new Consumer<ActivityRootEntity>() {

			@Override
			public void accept(ActivityRootEntity result) {
				replaceFragment(buildEditFragment(getActivePage(), result), false);
			}
		});

		updatePageMenu();
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

		ActivityRootEntity previousRootEntity = storedRootEntity;

		if (rootUuid != null && !rootUuid.isEmpty()) {
			storedRootEntity = queryRootEntity(rootUuid);
		} else {
			storedRootEntity = buildRootEntity();
		}

		// This should not happen; however, it still might under certain circumstances
		// (user clicking a notification for a task they have no access to anymore); in
		// this case, the activity should be closed.
		if (storedRootEntity == null) {
			finish();
		} else if (previousRootEntity != null) {
			if (DataHelper.equal(previousRootEntity.getLocalChangeDate(), storedRootEntity.getLocalChangeDate())) {
				// not changed -> keep existing that possibly has unsaved changes
				storedRootEntity = previousRootEntity;
			} else {
				NotificationHelper.showNotification(
					BaseEditActivity.this,
					NotificationType.WARNING,
					String.format(getResources().getString(R.string.message_entity_overridden), storedRootEntity.getEntityName()));
			}
		}

		// TODO #704 do in background and retrieve entity again
		// DatabaseHelper.getAdoDao(storedRootEntity.getClass()).markAsReadWithCast(storedRootEntity);

		callback.accept(storedRootEntity);
	}

	protected abstract ActivityRootEntity queryRootEntity(String recordUuid);

	protected abstract ActivityRootEntity buildRootEntity();

	protected ActivityRootEntity getStoredRootEntity() {
		return storedRootEntity;
	}

	protected void setStoredRootEntity(ActivityRootEntity entity) {
		this.storedRootEntity = entity;
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

		if (getActiveFragment() != null) {
			subHeadingTitle = (getActivePage() == null) ? getActiveFragment().getSubHeadingTitle() : getActivePage().getTitle();
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
		newMenu = menu.findItem(R.id.action_new);

		processActionbarMenu();

		return true;
	}

	public void processActionbarMenu() {
		boolean hasFragementView = activeFragment != null;

		if (saveMenu != null)
			saveMenu.setVisible(hasFragementView && activeFragment.isShowSaveAction());

		if (newMenu != null)
			newMenu.setVisible(hasFragementView && activeFragment.isShowNewAction());
	}

	public MenuItem getSaveMenu() {
		return saveMenu;
	}

	public MenuItem getNewMenu() {
		return newMenu;
	}

	public void replaceFragment(BaseEditFragment f, boolean allowBackNavigation) {
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

	protected abstract BaseEditFragment buildEditFragment(PageMenuItem menuItem, ActivityRootEntity activityRootData);

	@Override
	protected boolean openPage(PageMenuItem menuItem) {
		BaseEditFragment newActiveFragment = buildEditFragment(menuItem, storedRootEntity);
		if (newActiveFragment == null)
			return false;
		replaceFragment(newActiveFragment, true);
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
