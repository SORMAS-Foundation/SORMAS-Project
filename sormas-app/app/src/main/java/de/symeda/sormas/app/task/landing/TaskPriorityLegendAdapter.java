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

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.visualization.data.BaseLegendEntry;

/**
 * Created by Orson on 29/11/2017.
 */

public class TaskPriorityLegendAdapter extends BaseAdapter {

	private Context context;
	private List<BaseLegendEntry> data;
	private int rowLayout;

	public TaskPriorityLegendAdapter(Context context, List<BaseLegendEntry> data, int rowLayout) {
		this.context = context;
		this.data = data;
		this.rowLayout = rowLayout;
	}

	@Override
	public int getCount() {
		if (this.data == null)
			return 0;

		return this.data.size();
	}

	@Override
	public Object getItem(int position) {
		return this.data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return ((BaseLegendEntry) this.data.get(position)).getKey();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row;
		ViewHolder holder;

		if (convertView == null) {
			// if it's not recycled, initializeDialog some attributes
			LayoutInflater inflater = LayoutInflater.from(getContext());
			row = inflater.inflate(this.rowLayout, parent, false);
			holder = new ViewHolder(row);
		} else {
			row = (View) convertView;
			holder = new ViewHolder(row);
		}

		holder.txtLegendShape.setBackground(getContext().getDrawable(data.get(position).getLengendShape()));
		holder.txtLegendLabel.setText(data.get(position).getPriorityName());
		holder.txtLengendValue.setText(String.valueOf(data.get(position).getValue()));
		holder.txtLengendPercentage.setText(String.valueOf(data.get(position).getPercentage()) + "%");

		return row;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public List<BaseLegendEntry> getData() {
		return data;
	}

	public void setData(List<BaseLegendEntry> data) {
		this.data = data;
	}

	public int getRowLayout() {
		return rowLayout;
	}

	public void setRowLayout(int rowLayout) {
		this.rowLayout = rowLayout;
	}

	static class ViewHolder {

		View layout;
		TextView txtLegendShape;
		TextView txtLegendLabel;
		TextView txtLengendValue;
		TextView txtLengendPercentage;

		public ViewHolder(View layout) {
			this.layout = layout;

			txtLegendShape = (TextView) this.layout.findViewById(R.id.legend_entry_color_indicator);
			txtLegendLabel = (TextView) this.layout.findViewById(R.id.legend_entry_label);
			txtLengendValue = (TextView) this.layout.findViewById(R.id.legend_entry_value);
			txtLengendPercentage = (TextView) this.layout.findViewById(R.id.legend_entry_percentage);
		}
	}
}
