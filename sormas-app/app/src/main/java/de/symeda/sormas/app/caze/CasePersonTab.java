package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.databinding.CasePersonLayoutBinding;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Item;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class CasePersonTab extends FormTab {

    private CasePersonLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initModel();
        binding = DataBindingUtil.inflate(inflater, R.layout.case_person_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        final String caseUuid = (String) getArguments().getString(Case.UUID);
        CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = caseDao.queryUuid(caseUuid);

        final Person person = caze.getPerson();
        binding.setPerson(person);

        // binding non-string-fields to ui
        getModel().put(R.id.form_cp_date_of_birth,caze.getPerson().getBirthDate());
        addDateField(R.id.form_cp_date_of_birth, R.id.form_cp_btn_birth_date);

        getModel().put(R.id.form_cp_gender,caze.getPerson().getSex());
        addSpinnerField(R.id.form_cp_gender, Sex.class);


        getModel().put(R.id.form_cp_date_of_death,caze.getPerson().getDeathDate());
        addDateField(R.id.form_cp_date_of_death, R.id.form_cp_btn_date_of_death);

        getModel().put(R.id.form_cp_status_of_patient,caze.getPerson().getPresentCondition());
        final TextView dateOfDeathField = (TextView) getView().findViewById(R.id.form_cp_date_of_death);
        addSpinnerField(R.id.form_cp_status_of_patient, PresentCondition.class, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Item item = (Item)parent.getItemAtPosition(position);
                if(item.getValue()!=null && !PresentCondition.ALIVE.equals(item.getValue())) {
                    dateOfDeathField.setEnabled(true);
                }
                else {
                    dateOfDeathField.setEnabled(false);
                    dateOfDeathField.clearFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dateOfDeathField.setEnabled(false);
                dateOfDeathField.clearFocus();
            }
        });

    }

    @Override
    public AbstractDomainObject getData() {
        return commit(binding.getPerson());
    }

    /**
     * Commit all values from model to ado.
     * @param ado
     * @return
     */
    @Override
    protected AbstractDomainObject commit(AbstractDomainObject ado) {
        // Set value to model
        ((Person)ado).setBirthDate((Date)getModel().get(R.id.form_cp_date_of_birth));
        ((Person)ado).setSex((Sex)getModel().get(R.id.form_cp_gender));
        ((Person)ado).setPresentCondition((PresentCondition)getModel().get(R.id.form_cp_status_of_patient));
        return ado;
    }
}