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
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.databinding.VisitDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.validation.VisitValidator;

public class VisitEditDataForm extends FormTab {

    public static final String KEY_CONTACT_UUID = "contactUuid";
    public static final String NEW_VISIT = "newVisit";

    private VisitDataFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.visit_data_fragment_layout, container, false);

        Visit visit;

        // build a new visit from contact data
        if(getArguments().getBoolean(NEW_VISIT)) {
            String keyContactUuid = getArguments().getString(KEY_CONTACT_UUID);
            visit = DatabaseHelper.getVisitDao().build(keyContactUuid);
        }
        // open the given visit
        else {
            final String visitUuid = getArguments().getString(Visit.UUID);
            visit = DatabaseHelper.getVisitDao().queryUuid(visitUuid);
        }

        binding.setVisit(visit);

        binding.visitVisitDateTime.initialize(this);
        binding.visitVisitStatus.initialize(VisitStatus.class);
        binding.visitVisitStatus.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                ((VisitEditActivity) VisitEditDataForm.this.getActivity()).notifyVisitStatusChange(field.getValue() == VisitStatus.COOPERATIVE);
            }
        });

        VisitValidator.setRequiredHintsForVisitData(binding);

        return binding.getRoot();
    }

    @Override
    public AbstractDomainObject getData() {
        return binding == null ? null : binding.getVisit();
    }

    public VisitDataFragmentLayoutBinding getBinding() {
        return binding;
    }

}