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

package de.symeda.sormas.app.event.list;

import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.widget.AdapterView;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.PagedBaseListActivity;
import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.event.edit.EventNewActivity;
import de.symeda.sormas.app.util.Callback;

public class EventListActivity extends PagedBaseListActivity {

	private static EventStatus[] statusFilters = new EventStatus[] {
		null,
		EventStatus.SIGNAL,
		EventStatus.EVENT,
		EventStatus.SCREENING,
		EventStatus.CLUSTER,
		EventStatus.DROPPED };
	private EventListViewModel model;

	public static void startActivity(Context context, EventStatus listFilter) {
		BaseListActivity.startActivity(context, EventListActivity.class, buildBundle(getStatusFilterPosition(statusFilters, listFilter)));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		showPreloader();
		adapter = new EventListAdapter();
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				// Scroll to the topmost position after cases have been inserted
				if (positionStart == 0) {
					RecyclerView recyclerView = findViewById(R.id.recyclerViewForList);
					if (recyclerView != null) {
						recyclerView.scrollToPosition(0);
					}
				}
			}

			@Override
			public void onItemRangeMoved(int positionStart, int toPosition, int itemCount) {
				RecyclerView recyclerView = findViewById(R.id.recyclerViewForList);
				if (recyclerView != null) {
					recyclerView.scrollToPosition(0);
				}
			}
		});
		model = ViewModelProviders.of(this).get(EventListViewModel.class);
		model.getEvents().observe(this, events -> {
			adapter.submitList(events);
			hidePreloader();
		});
		setOpenPageCallback(p -> {
			showPreloader();
			model.getEventCriteria().eventStatus(statusFilters[((PageMenuItem) p).getPosition()]);
			model.notifyCriteriaUpdated();
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		getIntent().putExtra("refreshOnResume", true);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getIntent().getBooleanExtra("refreshOnResume", false)) {
			showPreloader();
			model.getEvents().getValue().getDataSource().invalidate();
		}
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		return PageMenuItem.fromEnum(statusFilters, getContext());
	}

	@Override
	protected Callback getSynchronizeResultCallback() {
		// Reload the list after a synchronization has been done
		return () -> {
			showPreloader();
			model.getEvents().getValue().getDataSource().invalidate();
		};
	}

	@Override
	public int onNotificationCountChangingAsync(AdapterView parent, PageMenuItem menuItem, int position) {
		//TODO: Call database and retrieve notification count
		return (int) (new Random(DateTime.now().getMillis() * 1000).nextInt() / 10000000);
	}

	@Override
	protected PagedBaseListFragment buildListFragment(PageMenuItem menuItem) {
		if (menuItem != null) {
			EventStatus listFilter = statusFilters[menuItem.getPosition()];
			return EventListFragment.newInstance(listFilter);
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getNewMenu().setTitle(R.string.action_new_event);
		return true;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_events_list;
	}

	@Override
	public boolean isEntryCreateAllowed() {
		return ConfigProvider.hasUserRight(UserRight.EVENT_CREATE);
	}

	@Override
	public void goToNewView() {
		EventNewActivity.startActivity(this);
		finish();
	}

	@Override
	public void addFiltersToPageMenu() {
		// Not supported yet
	}
}
