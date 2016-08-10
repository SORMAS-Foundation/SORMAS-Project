package de.symeda.sormas.app.caze;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class CaseNewTab extends FormTab {

    private Case caze;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initModel();
        return getActivity().getLayoutInflater().inflate(R.layout.case_new_fragment_layout, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            caze = DataUtils.createNew(Case.class);

            getModel().put(R.id.form_cn_disease, null);
            getModel().put(R.id.form_cn_date_of_report, new Date());
            getModel().put(R.id.form_cn_person, null);
            getModel().put(R.id.form_cn_health_facility, null);


            addSpinnerField(R.id.form_cn_disease, Disease.class);
            addDateField(R.id.form_cn_date_of_report, R.id.form_cn_btn_date_of_report);
            addPersonSpinnerField(R.id.form_cn_person);
            addFacilitySpinnerField(R.id.form_cn_health_facility);


        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    protected AbstractDomainObject commit(AbstractDomainObject ado) {
        caze.setDisease((Disease)getModel().get(R.id.form_cn_disease));
        caze.setReportDate((Date)getModel().get(R.id.form_cn_date_of_report));
        caze.setPerson((Person)getModel().get(R.id.form_cn_person));
        caze.setHealthFacility((Facility)getModel().get(R.id.form_cn_health_facility));

        return caze;
    }

    @Override
    public Case getData() {
        return (Case) commit(null);
    }

}