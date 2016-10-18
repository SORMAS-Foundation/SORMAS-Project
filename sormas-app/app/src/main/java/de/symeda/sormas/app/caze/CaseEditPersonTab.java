package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
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
        getModel().put(R.id.form_p_date_of_birth_dd,caze.getPerson().getBirthdateDD());
        getModel().put(R.id.form_p_date_of_birth_mm,caze.getPerson().getBirthdateMM());
        getModel().put(R.id.form_p_date_of_birth_yyyy,caze.getPerson().getBirthdateYYYY());
        getModel().put(R.id.form_p_gender,caze.getPerson().getSex());
        getModel().put(R.id.form_p_date_of_death,caze.getPerson().getDeathDate());
        getModel().put(R.id.form_p_status_of_patient,caze.getPerson().getPresentCondition());
        getModel().put(R.id.form_p_approximate_age,caze.getPerson().getApproximateAge());
        getModel().put(R.id.form_p_approximate_age_type,caze.getPerson().getApproximateAgeType());


        // date of birth
        addSpinnerField(R.id.form_p_date_of_birth_dd, toItems(DateHelper.getDaysInMonth(),true), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        addSpinnerField(R.id.form_p_date_of_birth_mm, toItems(DateHelper.getMonthsInYear(),true), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        addSpinnerField(R.id.form_p_date_of_birth_yyyy, toItems(DateHelper.getYearsToNow(),true), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // age type
        addSpinnerField(R.id.form_p_approximate_age_type, ApproximateAgeType.class);

        // gender
        addSpinnerField(R.id.form_p_gender, Sex.class);

        // date of death
        addDateField(R.id.form_p_date_of_death).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                updateApproximateAgeField();
            }
        });


        // status of patient
        addSpinnerField(R.id.form_p_status_of_patient, PresentCondition.class, new AdapterView.OnItemSelectedListener() {
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


        // @TODO: Workaround, find a better solution. Remove autofocus on first field.
        getView().requestFocus();
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

        PresentCondition condition = (PresentCondition)getModel().get(R.id.form_p_status_of_patient);

        setFieldVisible(getView().findViewById(R.id.cp_date_of_death_layout), condition != null
                && (PresentCondition.DEAD.equals(condition) || PresentCondition.BURIED.equals(condition)));
    }

    private void updateApproximateAgeField() {
        Integer birthyear = (Integer)getModel().get(R.id.form_p_date_of_birth_yyyy);
        TextView approximateAgeTextField = (TextView) getView().findViewById(R.id.form_p_approximate_age);
        Spinner approximateAgeTypeField = (Spinner) getView().findViewById(R.id.form_p_approximate_age_type);


        if(birthyear!=null) {
            deactivateField(approximateAgeTextField);
            deactivateField(approximateAgeTypeField);

            Integer birthday = (Integer)getModel().get(R.id.form_p_date_of_birth_dd);
            Integer birthmonth = (Integer)getModel().get(R.id.form_p_date_of_birth_mm);

            Calendar birthDate = new GregorianCalendar();
            birthDate.set(birthyear, birthmonth!=null?birthmonth-1:0, birthday!=null?birthday:1);

            Date to = new Date();
            if((Date)getModel().get(R.id.form_p_date_of_death)!= null){
                to = (Date)getModel().get(R.id.form_p_date_of_death);
            }
            DataHelper.Pair<Integer, ApproximateAgeType> approximateAge = DateUtils.getApproximateAgeYears(birthDate.getTime(),to);
            ApproximateAgeType ageType = approximateAge.getElement1();
            approximateAgeTextField.setText(String.valueOf(approximateAge.getElement0()));
            for (int i=0; i<approximateAgeTypeField.getCount(); i++) {
                Item item = (Item)approximateAgeTypeField.getItemAtPosition(i);
                if (item != null && item.getValue() != null && item.getValue().equals(ageType)) {
                    approximateAgeTypeField.setSelection(i);
                    break;
                }
            }

            getModel().put(R.id.form_p_approximate_age, approximateAge.getElement0());
            getModel().put(R.id.form_p_approximate_age_type, ageType);
        }
        else {
            approximateAgeTextField.setEnabled(true);
            approximateAgeTypeField.setEnabled(true);
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
        person.setBirthdateDD((Integer) getModel().get(R.id.form_p_date_of_birth_dd));
        person.setBirthdateMM((Integer) getModel().get(R.id.form_p_date_of_birth_mm));
        person.setBirthdateYYYY((Integer) getModel().get(R.id.form_p_date_of_birth_yyyy));
        person.setSex((Sex)getModel().get(R.id.form_p_gender));
        person.setPresentCondition((PresentCondition)getModel().get(R.id.form_p_status_of_patient));
        person.setDeathDate((Date)getModel().get(R.id.form_p_date_of_death));
        person.setApproximateAge((Integer)getModel().get(R.id.form_p_approximate_age));
        person.setApproximateAgeType((ApproximateAgeType)getModel().get(R.id.form_p_approximate_age_type));

        person.setAddress((Location)getModel().get(R.id.form_cp_address));

        person.setOccupationType((OccupationType) getModel().get(R.id.form_cp_occupation));
        person.setOccupationFacility((Facility) getModel().get(R.id.form_cp_occupation_facility));

        return person;
    }
}