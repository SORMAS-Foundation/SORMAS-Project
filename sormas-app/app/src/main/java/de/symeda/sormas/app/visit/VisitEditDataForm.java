package de.symeda.sormas.app.visit;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.databinding.VisitDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.FormTab;

public class VisitEditDataForm extends FormTab {

    public static final String KEY_CONTACT_UUID = "contactUuid";
    public static final String NEW_VISIT = "newVisit";

    private VisitDataFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.visit_data_fragment_layout, container, false);

        Visit visit;

        // create a new visit from contact data
        if(getArguments().getBoolean(NEW_VISIT)) {
            String keyContactUuid = getArguments().getString(KEY_CONTACT_UUID);
            visit = DatabaseHelper.getVisitDao().create(keyContactUuid);
        }
        // open the given visit
        else {
            final String visitUuid = getArguments().getString(Visit.UUID);
            visit = DatabaseHelper.getVisitDao().queryUuid(visitUuid);
        }

        binding.setVisit(visit);

        binding.visitVisitDateTime.initialize(this);
        binding.visitVisitStatus.initialize(VisitStatus.class);

        return binding.getRoot();
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getVisit();
    }

    public VisitDataFragmentLayoutBinding getBinding() {
        return binding;
    }

}