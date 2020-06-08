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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;
import de.symeda.sormas.app.dashboard.SummaryRegisterItem;

public abstract class BaseDashboardActivity extends BaseActivity {

	private View fragmentFrame = null;
	private View statusFrame = null;
	private View applicationTitleBar = null;
	private TextView subHeadingListActivityTitle;

	private Map<String, SummaryRegisterItem> activeFragments = new HashMap<String, SummaryRegisterItem>();

	@Override
	protected boolean isSubActivity() {
		return false;
	}

	@Override
	protected boolean openPage(PageMenuItem menuItem) {
		throw new UnsupportedOperationException();
	}

	protected void onCreateInner(Bundle savedInstanceState) {
		subHeadingListActivityTitle = (TextView) findViewById(R.id.subHeadingActivityTitle);

		if (showTitleBar()) {
			applicationTitleBar = findViewById(R.id.applicationTitleBar);
			statusFrame = findViewById(R.id.statusFrame);
		}

		fragmentFrame = findViewById(R.id.fragment_frame);
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		replaceFragments(buildSummaryFragments());
		updatePageMenu();
	}

	public boolean showTitleBar() {
		return false;
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

	public String getStatusName() {
		Enum pageStatus = getPageStatus();

		if (pageStatus != null) {
			StatusElaborator elaborator = StatusElaboratorFactory.getElaborator(pageStatus);
			if (elaborator != null)
				return elaborator.getFriendlyName(getContext());
		}

		return "";
	}

	protected abstract List<BaseSummaryFragment> buildSummaryFragments();

	private void replaceFragments(List<BaseSummaryFragment> fragments) {

		boolean hadFragments = activeFragments != null && !activeFragments.isEmpty();
		this.activeFragments.clear();

		if (fragments != null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			for (BaseSummaryFragment f : fragments) {
				if (f == null)
					continue;

				FrameLayout frame = (FrameLayout) findViewById(f.getContainerResId());

				if (frame == null)
					continue;

				ft.replace(f.getContainerResId(), f);
				activeFragments.put(f.getIdentifier(), new SummaryRegisterItem(f));

				//frame.setMinimumHeight(getResources().getDimensionPixelSize(f.getMinHeightResId()));
				frame.setVisibility(View.VISIBLE);
			}

			if (hadFragments) {
				ft.addToBackStack(null);
			}
			ft.commit();
		}

		updateStatusFrame();
	}

	@Override
	protected int getRootActivityLayout() {
		return R.layout.activity_root_dashboard_layout;
	}
}
