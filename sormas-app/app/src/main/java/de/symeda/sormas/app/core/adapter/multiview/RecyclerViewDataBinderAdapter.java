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

package de.symeda.sormas.app.core.adapter.multiview;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Orson on 27/11/2017.
 */

public abstract class RecyclerViewDataBinderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return getDataBinder(viewType).createViewHolder(parent);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

		DataBinder dataBinder = getDataBinder(viewHolder.getItemViewType());
		//int binderPosition = getBinderPosition(position);
		//dataBinder.moveNextDataPosition();
		if (dataBinder.moveNextDataPosition()) {
			//int kkkk = dataBinder.getCurrentDataPosition();
			dataBinder.bindToViewHolder(viewHolder, dataBinder.getCurrentDataPosition());
		}
	}

	@Override
	public abstract int getItemCount();

	@Override
	public abstract int getItemViewType(int position);

	public abstract <T extends DataBinder> T getDataBinder(int viewType);

	public abstract int getPosition(DataBinder binder, int binderPosition);

	public abstract int getBinderPosition(int position);

	public abstract void notifyBinderItemRangeChanged(DataBinder binder, int positionStart, int itemCount);

	public abstract void notifyBinderItemRangeInserted(DataBinder binder, int positionStart, int itemCount);

	public abstract void notifyBinderItemRangeRemoved(DataBinder binder, int positionStart, int itemCount);

	public void notifyBinderItemChanged(DataBinder binder, int binderPosition) {
		notifyItemChanged(getPosition(binder, binderPosition));
	}

	public void notifyBinderItemInserted(DataBinder binder, int binderPosition) {
		notifyItemInserted(getPosition(binder, binderPosition));
	}

	public void notifyBinderItemRemoved(DataBinder binder, int binderPosition) {
		notifyItemRemoved(getPosition(binder, binderPosition));
	}

	public void notifyBinderItemMoved(DataBinder binder, int fromPosition, int toPosition) {
		notifyItemMoved(getPosition(binder, fromPosition), getPosition(binder, toPosition));
	}
}
