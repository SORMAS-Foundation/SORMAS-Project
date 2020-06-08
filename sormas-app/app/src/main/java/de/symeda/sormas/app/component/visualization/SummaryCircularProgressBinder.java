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

import com.github.lzyzsd.circleprogress.DonutProgress;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.visualization.data.SummaryCircularData;
import de.symeda.sormas.app.core.adapter.multiview.DataBinder;
import de.symeda.sormas.app.core.adapter.multiview.RecyclerViewDataBinderAdapter;

/**
 * Created by Orson on 27/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SummaryCircularProgressBinder extends DataBinder<SummaryCircularProgressBinder.ViewHolder, SummaryCircularData> {

	private List<SummaryCircularData> data = new ArrayList<>();

	public SummaryCircularProgressBinder() {
		super();
	}

	public SummaryCircularProgressBinder(RecyclerViewDataBinderAdapter dataBindAdapter) {
		super(dataBindAdapter);
	}

	@Override
	public SummaryCircularProgressBinder.ViewHolder createViewHolder(ViewGroup parent) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_circular_progress_layout, parent, false);
		return new SummaryCircularProgressBinder.ViewHolder(view);
	}

	@Override
	public void bindToViewHolder(SummaryCircularProgressBinder.ViewHolder holder, int position) {
		if (position == PositionHelper.REMOVED_TASKS)
			holder.layout.setBackground(this.getContext().getResources().getDrawable(R.drawable.background_summary_cell_last));

		double percentage = data.get(position).getPercentage();

		holder.circularProgress.setDonut_progress(String.valueOf(Math.round(percentage)));
		holder.circularProgress.setShowText(false);
		holder.circularProgress.setUnfinishedStrokeColor(getContext().getResources().getColor(data.get(position).getUnfinishedColor()));
		holder.circularProgress.setFinishedStrokeColor(getContext().getResources().getColor(data.get(position).getFinishedColor()));
		holder.txtTitle.setText(data.get(position).getTitle());
		holder.txtValue.setText(String.valueOf((int) Math.round(data.get(position).getValue())));
		holder.txtPercentage.setText(String.valueOf(percentage) + "%");
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	@Override
	public void addAll(List<SummaryCircularData> data) {
		this.data.addAll(data);
		notifyBinderDataSetChanged();
	}

	public void clear() {
		data.clear();
		notifyBinderDataSetChanged();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		View layout;
		DonutProgress circularProgress;
		TextView txtTitle;
		TextView txtValue;
		TextView txtPercentage;

		public ViewHolder(View view) {
			super(view);
			layout = (View) itemView.findViewById(R.id.cell_root_layout);
			circularProgress = (DonutProgress) view.findViewById(R.id.circular_progress);
			txtTitle = (TextView) view.findViewById(R.id.title);
			txtValue = (TextView) view.findViewById(R.id.value);
			txtPercentage = (TextView) view.findViewById(R.id.percentage);
		}
	}

	static class PositionHelper {

		static final int PENDING_TAKS = 0;
		static final int DONE_TASKS = 1;
		static final int REMOVED_TASKS = 2;
		static final int NOT_EXECUTABLE_TASKS = 3;
	}
}
