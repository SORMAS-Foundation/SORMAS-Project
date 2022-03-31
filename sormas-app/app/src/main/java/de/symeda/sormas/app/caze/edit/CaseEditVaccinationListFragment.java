package de.symeda.sormas.app.caze.edit;

import java.util.Date;
import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.vaccination.Vaccination;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.immunization.reducedVaccination.VaccinationReducedListAdapter;
import de.symeda.sormas.app.immunization.vaccination.VaccinationEditActivity;
import de.symeda.sormas.app.immunization.vaccination.VaccinationListViewModel;

public class CaseEditVaccinationListFragment extends BaseEditFragment<FragmentFormListLayoutBinding, List<Vaccination>, Case>
	implements OnListItemClickListener {

	private VaccinationReducedListAdapter adapter;

	public static CaseEditVaccinationListFragment newInstance(Case activityRootData) {
		return newInstance(CaseEditVaccinationListFragment.class, null, activityRootData);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((CaseEditActivity) getActivity()).showPreloader();

		Date onsetDate = getActivityRootData().getSymptoms().getOnsetDate();
		Date vaccinationListGrayoutDate = onsetDate != null ? onsetDate : getActivityRootData().getReportDate();
		adapter = new VaccinationReducedListAdapter(vaccinationListGrayoutDate);

		VaccinationListViewModel model = ViewModelProviders.of(this).get(VaccinationListViewModel.class);
	//	model.initializeViewModel(getActivityRootData());
		model.getVaccinations().observe(this, vaccinations -> {
			adapter.submitList(vaccinations);
			((CaseEditActivity) getActivity()).hidePreloader();
			updateEmptyListHint(vaccinations);
		});
	}

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		adapter.setOnListItemClickListener(this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.caption_immunization_vaccinations);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_form_list_layout;
	}

	@Override
	public int getRootEditLayout() {
		return R.layout.fragment_root_list_form_layout;
	}

	@Override
	public List<Vaccination> getPrimaryData() {
		throw new UnsupportedOperationException("Sub list fragments don't hold their data");
	}

	@Override
	protected void prepareFragmentData() {

	}

	@Override
	public boolean isShowSaveAction() {
		return false;
	}

	@Override
	public boolean isShowNewAction() {
		return true;
	}

	@Override
	protected void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
		contentBinding.recyclerViewForList.setAdapter(adapter);
	}

	@Override
	public void onListItemClick(View view, int position, Object item) {
		Vaccination vaccination = (Vaccination) item;
		VaccinationEditActivity.startActivity(getActivity(), vaccination.getUuid());
	}
}
