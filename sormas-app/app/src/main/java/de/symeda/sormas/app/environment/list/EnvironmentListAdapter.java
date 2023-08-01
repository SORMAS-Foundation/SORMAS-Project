package de.symeda.sormas.app.environment.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.core.adapter.databinding.BindingPagedListAdapter;
import de.symeda.sormas.app.core.adapter.databinding.BindingViewHolder;
import de.symeda.sormas.app.databinding.RowEnvironmentListItemLayoutBinding;

public class EnvironmentListAdapter extends BindingPagedListAdapter<Environment, RowEnvironmentListItemLayoutBinding> {

	public EnvironmentListAdapter() {
		super(R.layout.row_environment_list_item_layout);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		if (getItemViewType(position) == TYPE_ITEM) {
			BindingViewHolder<Environment, RowEnvironmentListItemLayoutBinding> pageHolder = (BindingViewHolder) holder;
			pageHolder.setOnListItemClickListener(this.mOnListItemClickListener);
		}
	}
}
