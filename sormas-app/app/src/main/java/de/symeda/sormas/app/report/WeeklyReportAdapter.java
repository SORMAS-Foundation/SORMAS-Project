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

package de.symeda.sormas.app.report;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.databinding.RowWeeklyReportListItemLayoutBinding;

public class WeeklyReportAdapter extends DataBoundAdapter<RowWeeklyReportListItemLayoutBinding> {

	private static final String TAG = WeeklyReportAdapter.class.getSimpleName();

	private final Context context;
	private List<WeeklyReportListItem> data;

	public WeeklyReportAdapter(Context context, List<WeeklyReportListItem> data) {
		super(R.layout.row_weekly_report_list_item_layout);
		this.context = context;

		if (data == null)
			this.data = new ArrayList<>();
		else
			this.data = new ArrayList<>(data);
	}

	@Override
	protected void bindItem(DataBoundViewHolder<RowWeeklyReportListItemLayoutBinding> holder, int position, List<Object> payloads) {
		WeeklyReportListItem record = data.get(position);
		holder.setData(record);
	}

	@Override
	public int getItemCount() {
		return data.size();
	}
}
