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

package de.symeda.sormas.app.pathogentest.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.core.adapter.databinding.BindingPagedListAdapter;
import de.symeda.sormas.app.core.adapter.databinding.BindingViewHolder;
import de.symeda.sormas.app.databinding.RowSampleTestLayoutBinding;

public class PathogenTestListAdapter extends BindingPagedListAdapter<PathogenTest, RowSampleTestLayoutBinding> {

	private FollowUpStatus currentListFilter;

	public PathogenTestListAdapter(FollowUpStatus initialListFilter) {
		super(R.layout.row_sample_test_layout);
		this.currentListFilter = initialListFilter;
	}

	public PathogenTestListAdapter() {
		super(R.layout.row_sample_test_layout);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		if (getItemViewType(position) == TYPE_ITEM) {
			BindingViewHolder<PathogenTest, RowSampleTestLayoutBinding> pagedHolder = (BindingViewHolder) holder;
			PathogenTest item = getItem(position);

			pagedHolder.setOnListItemClickListener(this.mOnListItemClickListener);

		}
	}
}
