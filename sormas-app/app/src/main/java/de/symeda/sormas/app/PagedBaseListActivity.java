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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.component.menu.PageMenuControl;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.adapter.databinding.BindingPagedListAdapter;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.util.Consumer;

public abstract class PagedBaseListActivity<T extends AbstractDomainObject> extends BaseActivity
	implements IUpdateSubHeadingTitle, PageMenuControl.NotificationCountChangingListener {

	private TextView subHeadingListActivityTitle;
	private MenuItem newMenu = null;
	private PagedBaseListFragment activeFragment = null;
	private Consumer<PageMenuItem> openPageCallback;

	protected BindingPagedListAdapter<T, ?> adapter;

	public static Bundler buildBundle(int pageMenuPosition) {
		return BaseActivity.buildBundle(pageMenuPosition);
	}

	@Override
	protected boolean isSubActivity() {
		return false;
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	protected void onCreateInner(Bundle savedInstanceState) {
		subHeadingListActivityTitle = (TextView) findViewById(R.id.subHeadingActivityTitle);

		if (pageMenu != null) {
			addFiltersToPageMenu();
		}
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		replaceFragment(buildListFragment(getActivePage()));
		updatePageMenu();
	}

	public PagedBaseListFragment getActiveFragment() {
		return activeFragment;
	}

	public void replaceFragment(PagedBaseListFragment f) {
		activeFragment = f;

		if (activeFragment != null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
			ft.replace(R.id.fragment_frame, activeFragment);
			ft.commit();
		}

		updateStatusFrame();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_action_menu, menu);

		newMenu = menu.findItem(R.id.action_new);

		processActionbarMenu();

		return true;
	}

	protected boolean isEntryCreateAllowed() {
		return false;
	}

	private void processActionbarMenu() {
		if (activeFragment == null)
			return;

		if (newMenu != null)
			newMenu.setVisible(isEntryCreateAllowed());
	}

	public MenuItem getNewMenu() {
		return newMenu;
	}

	public void setSubHeadingTitle(String title) {
		String t = (title == null) ? "" : title;

		if (subHeadingListActivityTitle != null)
			subHeadingListActivityTitle.setText(t);
	}

	public void setOpenPageCallback(Consumer<PageMenuItem> callback) {
		this.openPageCallback = callback;
	}

	@Override
	public void updateSubHeadingTitle() {
		if (getActiveFragment() != null) {
			if (getActiveFragment().getListFilter() != null) {
				setSubHeadingTitle(getActiveFragment().getListFilter().toString());
			} else {
				setSubHeadingTitle(getResources().getString(R.string.all));
			}
		} else {
			setSubHeadingTitle("");
		}
	}

	@Override
	public void updateSubHeadingTitle(String title) {
		setSubHeadingTitle(title);
	}

	@Override
	protected int getRootActivityLayout() {
		return R.layout.activity_root_list_layout;
	}

	public abstract int onNotificationCountChangingAsync(AdapterView<?> parent, PageMenuItem menuItem, int position);

	public abstract void addFiltersToPageMenu();

	@Override
	protected boolean openPage(PageMenuItem menuItem) {
		PagedBaseListFragment newActiveFragment = buildListFragment(menuItem); //, storedListData
		if (newActiveFragment == null)
			return false;
		replaceFragment(newActiveFragment);
		if (openPageCallback != null) {
			openPageCallback.accept(menuItem);
		}
		return true;
	}

	protected abstract PagedBaseListFragment buildListFragment(PageMenuItem menuItem);

	public BindingPagedListAdapter<?, ?> getAdapter() {
		return adapter;
	}

	protected static int getStatusFilterPosition(Enum[] statusFilters, Enum filter) {
		for (int i = 0; i < statusFilters.length; i++) {
			if (statusFilters[i] == filter) {
				return i;
			}
		}

		return 0;
	}
}
