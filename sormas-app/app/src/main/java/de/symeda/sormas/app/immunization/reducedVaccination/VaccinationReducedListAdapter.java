package de.symeda.sormas.app.immunization.reducedVaccination;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.vaccination.Vaccination;
import de.symeda.sormas.app.core.adapter.databinding.BindingPagedListAdapter;
import de.symeda.sormas.app.core.adapter.databinding.BindingViewHolder;
import de.symeda.sormas.app.databinding.RowVaccinationReducedListItemLayoutBinding;

public class VaccinationReducedListAdapter extends BindingPagedListAdapter<Vaccination, RowVaccinationReducedListItemLayoutBinding> {

	Date vaccinationGrayoutDate;

	public VaccinationReducedListAdapter(Date vaccinationGrayoutDate) {
		super(R.layout.row_vaccination_reduced_list_item_layout);
		this.vaccinationGrayoutDate = vaccinationGrayoutDate;
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		if (getItemViewType(position) == TYPE_ITEM) {
			BindingViewHolder<Vaccination, RowVaccinationReducedListItemLayoutBinding> pageHolder = (BindingViewHolder) holder;
			pageHolder.setOnListItemClickListener(this.mOnListItemClickListener);

			Date vaccinationDate = pageHolder.binding.getData().getVaccinationDate();
			pageHolder.binding.setVariable(BR.fontColorGrey, vaccinationDate == null || vaccinationGrayoutDate.before(vaccinationDate));
		}
	}
}
