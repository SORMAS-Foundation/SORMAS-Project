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

package de.symeda.sormas.app.component.visualization;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.visualization.data.SummaryTotalData;
import de.symeda.sormas.app.core.adapter.multiview.DataBinder;
import de.symeda.sormas.app.core.adapter.multiview.RecyclerViewDataBinderAdapter;

/**
 * Created by Orson on 27/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SummaryTotalBinder extends DataBinder<SummaryTotalBinder.ViewHolder, SummaryTotalData> {

	private List<SummaryTotalData> data = new ArrayList<>();

	public SummaryTotalBinder() {
		super();
	}

	public SummaryTotalBinder(RecyclerViewDataBinderAdapter dataBindAdapter) {
		super(dataBindAdapter);
	}

	@Override
	public ViewHolder createViewHolder(ViewGroup parent) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_total_layout, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void bindToViewHolder(ViewHolder holder, int position) {
		holder.txtTitle.setText(data.get(position).dataTitle);
		holder.txtValue.setText(data.get(position).dataValue);
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	@Override
	public void addAll(List<SummaryTotalData> data) {
		this.data.addAll(data);
		notifyBinderDataSetChanged();
	}

	public void clear() {
		data.clear();
		notifyBinderDataSetChanged();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		View layout;
		TextView txtTitle;
		TextView txtValue;

		public ViewHolder(View view) {
			super(view);
			layout = (View) itemView.findViewById(R.id.cell_root_layout);
			txtTitle = (TextView) view.findViewById(R.id.title);
			txtValue = (TextView) view.findViewById(R.id.value);
		}
	}

	static class PositionHelper {

		static final int TOTAL_TASKS = 0;
	}
}
