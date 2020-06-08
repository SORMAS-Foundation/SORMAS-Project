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

package de.symeda.sormas.app.component.menu;

import static android.view.View.GONE;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.symeda.sormas.app.R;

public class PageMenuAdapter extends BaseAdapter {

	private Context context;
	private List<PageMenuItem> data;

	private int cellLayout;
	// TODO: Enable once notification counters are implemented
//    private int counterBackgroundColor;
//    private int counterBackgroundActiveColor;
	private int iconColor;
	private int iconActiveColor;
	private int titleColor;
	private int titleActiveColor;

	PageMenuAdapter(Context context, int cellLayout, int iconColor, int iconActiveColor, int titleColor, int titleActiveColor) {
		this.context = context;
		this.cellLayout = cellLayout;
//        this.counterBackgroundColor = counterBackgroundColor;
//        this.counterBackgroundActiveColor = counterBackgroundActiveColor;
		this.iconColor = iconColor;
		this.iconActiveColor = iconActiveColor;
		this.titleColor = titleColor;
		this.titleActiveColor = titleActiveColor;
	}

	public void setData(List<PageMenuItem> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (this.data == null) {
			return 0;
		}

		return this.data.size();
	}

	@Override
	public Object getItem(int position) {
		return this.data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View layout;

		if (convertView == null) {
			// If the view is not recycled, initialize attributes
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(this.cellLayout, parent, false);
		} else {
			layout = convertView;
		}

		PageMenuItem pageMenuItem = data.get(position);

		if (pageMenuItem != null) {
			TextView counterView = layout.findViewById(R.id.counter);
			ImageView iconView = layout.findViewById(R.id.icon);
			TextView titleView = layout.findViewById(R.id.title);

			if (counterView != null) {
//            counterView.setText(String.valueOf(pageMenuItem.getNotificationCount()));
				counterView.setVisibility(GONE);
//
//            Drawable counterDrawable = counterView.getBackground();
//            if (pageMenuItem.isActive()) {
//                counterDrawable.setTint(context.getResources().getColor(this.counterBackgroundActiveColor));
//            } else {
//                counterDrawable.setTint(context.getResources().getColor(this.counterBackgroundColor));
//            }
			}

			if (pageMenuItem.getIconResourceId() > 0) {
				Drawable icon = context.getResources().getDrawable(pageMenuItem.getIconResourceId());
				if (pageMenuItem.isActive()) {
					icon.setTint(context.getResources().getColor(this.iconActiveColor));
					icon.setAlpha(255);
				} else {
					icon.setTint(context.getResources().getColor(this.iconColor));
					icon.setAlpha(128);
				}
				iconView.setImageDrawable(icon);
			}

			titleView.setText(pageMenuItem.getTitle());
			if (pageMenuItem.isActive()) {
				titleView.setTextColor(context.getResources().getColor(this.titleActiveColor));
			} else {
				titleView.setTextColor(context.getResources().getColor(this.titleColor));
			}
		}

		return layout;
	}
}
