package de.symeda.sormas.app.caze;

import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.CasePersonDto;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.databinding.CasePersonLayoutBinding;
import de.symeda.sormas.app.util.DateUtils;
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

        // ================ Person information ================
        getModel().put(R.id.form_cp_date_of_birth,caze.getPerson().getBirthDate());
        getModel().put(R.id.form_cp_gender,caze.getPerson().getSex());
        getModel().put(R.id.form_cp_date_of_death,caze.getPerson().getDeathDate());
        getModel().put(R.id.form_cp_status_of_patient,caze.getPerson().getPresentCondition());
        getModel().put(R.id.form_cp_approximate_age,caze.getPerson().getApproximateAge());


        // date of birth
        addDateField(R.id.form_cp_date_of_birth, R.id.form_cp_btn_birth_date, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                updateApproximateAgeField();
            }
        });

        // gender
        addSpinnerField(R.id.form_cp_gender, Sex.class);

        // date of death
        addDateField(R.id.form_cp_date_of_death, R.id.form_cp_btn_date_of_death, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                updateApproximateAgeField();
            }
        });


        // status of patient
        final TextView dateOfDeathField = (TextView) getView().findViewById(R.id.form_cp_date_of_death);
        addSpinnerField(R.id.form_cp_status_of_patient, PresentCondition.class, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Item item = (Item)parent.getItemAtPosition(position);
                if(item.getValue()!=null && !PresentCondition.ALIVE.equals(item.getValue())) {
                    dateOfDeathField.setEnabled(true);
                }
                else {
                    deactivateField(dateOfDeathField);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                deactivateField(dateOfDeathField);
            }
        });


        // ================ Occupation ================
        getModel().put(R.id.form_cp_occupation,caze.getPerson().getOccupationType());
        getModel().put(R.id.form_cp_occupation_facility,caze.getPerson().getOccupationFacility());

        final LinearLayout occupationDetailsLayout = (LinearLayout) getView().findViewById(R.id.form_cp_occupation_details_view);
        final EditText occupationDetailsField = (EditText) getView().findViewById(R.id.form_cp_occupation_details);
        final LinearLayout occupationFacilityLayout = (LinearLayout) getView().findViewById(R.id.form_cp_occupation_facility_view);
        addSpinnerField(R.id.form_cp_occupation, OccupationType.class, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Item item = (Item)parent.getItemAtPosition(position);
                switch((OccupationType)item.getValue()) {
                    case BUSINESSMAN_WOMAN:
                    case TRANSPORTER:
                    case OTHER:
                        occupationDetailsLayout.setVisibility(View.VISIBLE);
                        occupationFacilityLayout.setVisibility(View.INVISIBLE);
                        break;
                    case HEALTHCARE_WORKER:
                        occupationDetailsLayout.setVisibility(View.VISIBLE);
                        occupationFacilityLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        occupationDetailsLayout.setVisibility(View.INVISIBLE);
                        occupationFacilityLayout.setVisibility(View.INVISIBLE);
                        break;
                }

                switch((OccupationType)item.getValue()) {
                    case BUSINESSMAN_WOMAN:
                        occupationDetailsField.setHint(getResources().getString(R.string.headline_form_cp_occupation_details_business));
                        break;
                    case TRANSPORTER:
                        occupationDetailsField.setHint(getResources().getString(R.string.headline_form_cp_occupation_details_transport));
                        break;
                    case OTHER:
                        occupationDetailsField.setHint(getResources().getString(R.string.headline_form_cp_occupation_details_other));
                        break;
                    case HEALTHCARE_WORKER:
                        occupationDetailsField.setHint(getResources().getString(R.string.headline_form_cp_occupation_details_healthcare));
                        break;
                    default:
                        occupationDetailsField.setHint(getResources().getString(R.string.headline_form_cp_occupation_details));
                        break;
                }


                if(item.getValue()!=null && !PresentCondition.ALIVE.equals(item.getValue())) {
                    dateOfDeathField.setEnabled(true);
                }
                else {
                    deactivateField(dateOfDeathField);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                deactivateField(dateOfDeathField);
            }
        });


    }

    private void updateApproximateAgeField() {
        Date birthDate = (Date)getModel().get(R.id.form_cp_date_of_birth);
        TextView approximateAgeTextField = (TextView) getView().findViewById(R.id.form_cp_approximate_age);
        if(birthDate!=null) {
            deactivateField(approximateAgeTextField);
            Date to = new Date();
            if((Date)getModel().get(R.id.form_cp_date_of_death)!= null){
                to = (Date)getModel().get(R.id.form_cp_date_of_death);
            }
            DataHelper.Pair<Integer, ApproximateAgeType> approximateAge = DateUtils.getApproximateAgeYears(birthDate,to);
            String age = String.valueOf(approximateAge.getElement0());
            if(ApproximateAgeType.MONTHS.equals(approximateAge.getElement1())) {
                 age = String.format("%.2f", ((float)approximateAge.getElement0()/12));
            }
            approximateAgeTextField.setText(age);
            getModel().put(R.id.form_cp_approximate_age, approximateAge.getElement0());
            getModel().put(R.id.form_cp_approximate_age_type, approximateAge.getElement0());
        }
        else {
            approximateAgeTextField.setEnabled(true);
        }
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
        ((Person)ado).setDeathDate((Date)getModel().get(R.id.form_cp_date_of_death));
        ((Person)ado).setApproximateAge((Integer)getModel().get(R.id.form_cp_approximate_age));
        ((Person)ado).setApproximateAgeType((ApproximateAgeType)getModel().get(R.id.form_cp_approximate_age_type));

        ((Person)ado).setOccupationType((OccupationType) getModel().get(R.id.form_cp_occupation));

        return ado;
    }
}