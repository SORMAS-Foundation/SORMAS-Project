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

package de.symeda.sormas.app.task.landing;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.visualization.data.SummaryPieData;
import de.symeda.sormas.app.component.visualization.data.SummaryPieEntry;
import de.symeda.sormas.app.core.adapter.multiview.DataBinder;
import de.symeda.sormas.app.core.adapter.multiview.RecyclerViewDataBinderAdapter;

/**
 * Created by Orson on 27/11/2017.
 */

public class SummaryPieChartWithLegendBinder extends DataBinder<SummaryPieChartWithLegendBinder.ViewHolder, SummaryPieData> {

	private List<SummaryPieData> data = new ArrayList<>();

	public SummaryPieChartWithLegendBinder() {
		super();
	}

	public SummaryPieChartWithLegendBinder(RecyclerViewDataBinderAdapter dataBindAdapter) {
		super(dataBindAdapter);
	}

	@Override
	public SummaryPieChartWithLegendBinder.ViewHolder createViewHolder(ViewGroup parent) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_pie_chart_layout, parent, false);
		return new SummaryPieChartWithLegendBinder.ViewHolder(view);
	}

	@Override
	public void bindToViewHolder(SummaryPieChartWithLegendBinder.ViewHolder holder, int position) {
		List<PieEntry> entries = new ArrayList<PieEntry>();

		if (position == PositionHelper.TASK_PRIORITY)
			holder.layout.setBackground(this.getContext().getResources().getDrawable(R.drawable.background_summary_cell_last));

		holder.txtTitle.setText(data.get(position).getTitle());

		for (SummaryPieEntry data : data.get(position).getEntries()) {
			entries.add(new PieEntry(data.getValue(), data.getLabel()));
		}

		//Add entries to data set
		PieDataSet dataSet = new PieDataSet(entries, "");
		dataSet.setValueTextSize(10);
		dataSet.setDrawValues(false);
		dataSet.setSelectionShift(0);

		//Set colors
		dataSet.setColors(data.get(position).getColors());

		holder.pieChart.getDescription().setEnabled(false);
		holder.pieChart.setTouchEnabled(true);
		holder.pieChart.setRotationEnabled(true);
		holder.pieChart.setDrawHoleEnabled(false);
		holder.pieChart.setHoleRadius(0);
		holder.pieChart.setDrawEntryLabels(false);
		//holder.pieChart.setDrawSliceText(false);
		//holder.pieChart.setTex

		Legend legend = holder.pieChart.getLegend();
		legend.setForm(Legend.LegendForm.CIRCLE);
		legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
		legend.setEnabled(false);

		//dataSet.setColor();
		//dataSet.setValueTextColor();

		PieData pieData = new PieData(dataSet);
		holder.pieChart.setData(pieData);
		holder.pieChart.invalidate();

		TaskPriorityLegendAdapter adapter =
			new TaskPriorityLegendAdapter(getContext(), data.get(position).getLegendEntries(), R.layout.summary_pie_chart_legend_entry_layout);
		holder.legendListView.setAdapter(adapter);
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	@Override
	public void addAll(List<SummaryPieData> data) {
		this.data.addAll(data);
		notifyBinderDataSetChanged();
	}

	public void clear() {
		data.clear();
		notifyBinderDataSetChanged();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		View layout;
		PieChart pieChart;
		TextView txtTitle;
		ListView legendListView;

		public ViewHolder(View view) {
			super(view);
			layout = (View) itemView.findViewById(R.id.cell_root_layout);

			legendListView = (ListView) view.findViewById(R.id.legend);
			pieChart = (PieChart) view.findViewById(R.id.pie_chart);
			txtTitle = (TextView) view.findViewById(R.id.title);

			legendListView.setDividerHeight(0);
			legendListView.setClickable(false);
		}
	}

	static class PositionHelper {

		static final int TASK_PRIORITY = 0;
	}
}
