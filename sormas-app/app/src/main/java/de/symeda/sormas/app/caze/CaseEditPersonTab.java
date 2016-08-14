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

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.databinding.CasePersonFragmentLayoutBinding;
import de.symeda.sormas.app.util.DateUtils;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Item;


/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class CaseEditPersonTab extends FormTab {

    private CasePersonFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initModel();
        binding = DataBindingUtil.inflate(inflater, R.layout.case_person_fragment_layout, container, false);
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
        getModel().put(R.id.form_cp_approximate_age_type,caze.getPerson().getApproximateAgeType());


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
        addSpinnerField(R.id.form_cp_status_of_patient, PresentCondition.class, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDateOfDeathField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateDateOfDeathField();
            }
        });

        // evaluate the model and set the ui
        updateDateOfDeathField();
        updateApproximateAgeField();

        // ================ Address ================
        getModel().put(R.id.form_cp_address,caze.getPerson().getAddress());
        addLocationField(R.id.form_cp_address, R.id.form_cp_btn_address);

        // ================ Occupation ================
        getModel().put(R.id.form_cp_occupation,caze.getPerson().getOccupationType());
        getModel().put(R.id.form_cp_occupation_facility,caze.getPerson().getOccupationFacility());

        final LinearLayout occupationDetailsLayout = (LinearLayout) getView().findViewById(R.id.form_cp_occupation_details_view);
        final TextView occupationDetailsCaption = (TextView) getView().findViewById(R.id.headline_form_cp_occupation_details);
        final LinearLayout occupationFacilityLayout = (LinearLayout) getView().findViewById(R.id.form_cp_occupation_facility_view);
        addSpinnerField(R.id.form_cp_occupation, OccupationType.class, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item)parent.getItemAtPosition(position);
                updateVisibilityOccupationFields(item, occupationDetailsLayout, occupationFacilityLayout);
                updateHeadlineOccupationDetailsFields(item, occupationDetailsCaption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                deactivateField(occupationDetailsLayout);
            }
        });


        addFacilitySpinnerField(R.id.form_cp_occupation_facility);
    }

    private void updateHeadlineOccupationDetailsFields(Item item, TextView occupationDetailsCaption) {
        if(item.getValue()!=null) {
            switch ((OccupationType) item.getValue()) {
                case BUSINESSMAN_WOMAN:
                    occupationDetailsCaption.setText(getResources().getString(R.string.headline_form_cp_occupation_details_business));
                    break;
                case TRANSPORTER:
                    occupationDetailsCaption.setText(getResources().getString(R.string.headline_form_cp_occupation_details_transport));
                    break;
                case OTHER:
                    occupationDetailsCaption.setText(getResources().getString(R.string.headline_form_cp_occupation_details_other));
                    break;
                case HEALTHCARE_WORKER:
                    occupationDetailsCaption.setText(getResources().getString(R.string.headline_form_cp_occupation_details_healthcare));
                    break;
                default:
                    occupationDetailsCaption.setText(getResources().getString(R.string.headline_form_cp_occupation_details));
                    break;
            }
        }
        else {
            occupationDetailsCaption.setText(getResources().getString(R.string.headline_form_cp_occupation_details));
        }
    }

    private void updateVisibilityOccupationFields(Item item, LinearLayout occupationDetailsLayout, LinearLayout occupationFacilityLayout) {
        if(item.getValue()!=null) {
            switch ((OccupationType) item.getValue()) {
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
        }
        else {
            occupationDetailsLayout.setVisibility(View.INVISIBLE);
            occupationFacilityLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void updateDateOfDeathField() {

        PresentCondition condition = (PresentCondition)getModel().get(R.id.form_cp_status_of_patient);

        setFieldVisible(getView().findViewById(R.id.form_cp_date_of_death_wrap), condition != null
                && (PresentCondition.DEAD.equals(condition) || PresentCondition.BURIED.equals(condition)));
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
            getModel().put(R.id.form_cp_approximate_age_type, approximateAge.getElement1());
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
        Person person = (Person) ado;
        person.setBirthDate((Date)getModel().get(R.id.form_cp_date_of_birth));
        person.setSex((Sex)getModel().get(R.id.form_cp_gender));
        person.setPresentCondition((PresentCondition)getModel().get(R.id.form_cp_status_of_patient));
        person.setDeathDate((Date)getModel().get(R.id.form_cp_date_of_death));
        person.setApproximateAge((Integer)getModel().get(R.id.form_cp_approximate_age));
        person.setApproximateAgeType((ApproximateAgeType)getModel().get(R.id.form_cp_approximate_age_type));

        person.setAddress((Location)getModel().get(R.id.form_cp_address));

        person.setOccupationType((OccupationType) getModel().get(R.id.form_cp_occupation));
        person.setOccupationFacility((Facility) getModel().get(R.id.form_cp_occupation_facility));

        return person;
    }
}