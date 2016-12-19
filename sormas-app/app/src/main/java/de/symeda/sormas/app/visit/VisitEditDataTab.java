package de.symeda.sormas.app.visit;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.databinding.VisitDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;

public class VisitEditDataTab extends FormTab {

    private VisitDataFragmentLayoutBinding binding;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.visit_data_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        final String visitUuid = getArguments().getString(Visit.UUID);
        final Visit visit = DatabaseHelper.getVisitDao().queryUuid(visitUuid);
        binding.setVisit(visit);

        binding.visitVisitDateTime.initialize(this);
        binding.visitVisitStatus.initialize(VisitStatus.class);
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getVisit();
    }

}