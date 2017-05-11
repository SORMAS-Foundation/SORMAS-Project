package de.symeda.sormas.app.person;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.LocationDialog;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.component.SpinnerField;
import de.symeda.sormas.app.component.TextField;
import de.symeda.sormas.app.databinding.PersonEditFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Item;
import de.symeda.sormas.app.util.Consumer;


/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class PersonEditForm extends FormTab {

    PersonEditFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.person_edit_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        final String personUuid = getArguments().getString(Person.UUID);
        PersonDao personDao = DatabaseHelper.getPersonDao();
        Person person = personDao.queryUuid(personUuid);
        binding.setPerson(person);

        // date of birth
        FieldHelper.initSpinnerField(binding.personBirthdateDD, DataUtils.toItems(DateHelper.getDaysInMonth(),true), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        FieldHelper.initMonthSpinnerField(binding.personBirthdateMM, DataUtils.toItems(DateHelper.getMonthsInYear(),true), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        FieldHelper.initSpinnerField(binding.personBirthdateYYYY, DataUtils.toItems(DateHelper.getYearsToNow(),true), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        binding.personBirthdateYYYY.setSelectionOnOpen(2000);

        // age type
        FieldHelper.initSpinnerField(binding.personApproximateAgeType, ApproximateAgeType.class);

        // gender
        FieldHelper.initSpinnerField(binding.personSex, Sex.class);

        // date of death
        binding.personDeathDate.initialize(this);
        binding.personDeathDate.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                updateApproximateAgeField();
            }
        });


        // status of patient
        FieldHelper.initSpinnerField(binding.personPresentCondition, PresentCondition.class, new AdapterView.OnItemSelectedListener() {
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
        LocationDialog.addLocationField(getActivity(), person.getAddress(), binding.personAddress, binding.formCpBtnAddress, new Consumer() {
            @Override
            public void accept(Object parameter) {
                if(parameter instanceof Location) {
                    binding.personAddress.setValue(parameter.toString());
                    binding.getPerson().setAddress(((Location)parameter));
                }
            }
        });

        // ================ Occupation ================

        final TextField occupationDetails = binding.personOccupationDetails;
        FieldHelper.initSpinnerField(binding.personOccupationType1, OccupationType.class, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item)parent.getItemAtPosition(position);
                updateVisibilityOccupationFields(item, occupationDetails);
                updateHeadlineOccupationDetailsFields(item, occupationDetails);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                deactivateField(occupationDetails);
            }
        });

        initFacilityFields(person.getOccupationFacility());

        // ================ Additional settings ================

        binding.personApproximate1Age.setInputType(InputType.TYPE_CLASS_NUMBER);
        binding.personPhone.setInputType(InputType.TYPE_CLASS_PHONE);

        // @TODO: Workaround, find a better solution. Remove autofocus on first field.
        getView().requestFocus();
    }

    private void updateHeadlineOccupationDetailsFields(Item item, TextField occupationDetails) {
        if(item.getValue()!=null) {
            switch ((OccupationType) item.getValue()) {
                case BUSINESSMAN_WOMAN:
                    occupationDetails.updateCaption(getResources().getString(R.string.headline_form_cp_occupation_details_business));
                    break;
                case TRANSPORTER:
                    occupationDetails.updateCaption(getResources().getString(R.string.headline_form_cp_occupation_details_transport));
                    break;
                case OTHER:
                    occupationDetails.updateCaption(getResources().getString(R.string.headline_form_cp_occupation_details_other));
                    break;
                case HEALTHCARE_WORKER:
                    occupationDetails.updateCaption(getResources().getString(R.string.headline_form_cp_occupation_details_healthcare));
                    break;
                default:
                    occupationDetails.updateCaption(getResources().getString(R.string.headline_form_cp_occupation_details));
                    break;
            }
        }
        else {
            occupationDetails.updateCaption(getResources().getString(R.string.headline_form_cp_occupation_details));
        }
    }

    private void updateVisibilityOccupationFields(Item item, View occupationDetails) {
        if(item.getValue()!=null) {
            switch ((OccupationType) item.getValue()) {
                case BUSINESSMAN_WOMAN:
                case TRANSPORTER:
                case OTHER:
                    occupationDetails.setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.person_facility_layout).setVisibility(View.GONE);
                    break;
                case HEALTHCARE_WORKER:
                    occupationDetails.setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.person_facility_layout).setVisibility(View.VISIBLE);
                    break;
                default:
                    occupationDetails.setVisibility(View.GONE);
                    getActivity().findViewById(R.id.person_facility_layout).setVisibility(View.GONE);
                    break;
            }
        }
        else {
            occupationDetails.setVisibility(View.GONE);
            getActivity().findViewById(R.id.person_facility_layout).setVisibility(View.GONE);
        }
    }

    private void initFacilityFields(Facility facility) {

        final List emptyList = new ArrayList<>();

        FieldHelper.initRegionSpinnerField(binding.personFacilityRegion, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selectedValue = binding.personFacilityRegion.getValue();
                if(binding.personFacilityDistrict != null) {
                    List<District> districtList = emptyList;
                    if(selectedValue != null) {
                        districtList = DatabaseHelper.getDistrictDao().getByRegion((Region)selectedValue);
                    }
                    binding.personFacilityDistrict.setAdapterAndValue(binding.personFacilityDistrict.getValue(), DataUtils.toItems(districtList));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        List districtList = new ArrayList<>();
        if (facility != null) {
            binding.personFacilityRegion.setValue(facility.getRegion());
            districtList = DataUtils.toItems(DatabaseHelper.getDistrictDao().getByRegion(facility.getRegion()));
        }

        FieldHelper.initSpinnerField(binding.personFacilityDistrict, districtList, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selectedValue = binding.personFacilityDistrict.getValue();
                if(binding.personFacilityCommunity != null) {
                    List<Community> communityList = emptyList;
                    if(selectedValue != null) {
                        communityList = DatabaseHelper.getCommunityDao().getByDistrict((District)selectedValue);
                    }
                    binding.personFacilityCommunity.setAdapterAndValue(binding.personFacilityCommunity.getValue(), DataUtils.toItems(communityList));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        List communityList = new ArrayList<>();
        if(facility != null) {
            binding.personFacilityDistrict.setValue(facility.getDistrict());
            communityList = DataUtils.toItems(DatabaseHelper.getCommunityDao().getByDistrict(facility.getDistrict()));
        }

        FieldHelper.initSpinnerField(binding.personFacilityCommunity, communityList, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SpinnerField spinnerField = binding.personOccupationFacility;
                Object selectedValue = binding.personFacilityCommunity.getValue();
                if (spinnerField != null) {
                    List<Facility> facilityList = emptyList;
                    if (selectedValue != null) {
                        facilityList = DatabaseHelper.getFacilityDao().getByCommunity((Community) selectedValue);
                    }
                    spinnerField.setAdapterAndValue(binding.personOccupationFacility.getValue(), DataUtils.toItems(facilityList));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        List facilityList = new ArrayList<>();
        if (facility != null) {
            binding.personFacilityCommunity.setValue(facility.getCommunity());
            facilityList = DataUtils.toItems(DatabaseHelper.getFacilityDao().getByCommunity(facility.getCommunity()));
        }

        FieldHelper.initSpinnerField(binding.personOccupationFacility, facilityList);

        if (facility != null) {
            binding.personOccupationFacility.setValue(facility);
        }
    }

    private void updateDateOfDeathField() {
        PresentCondition condition = (PresentCondition)binding.personPresentCondition.getValue();
        setFieldVisible(binding.cpDateOfDeathLayout, condition != null
                && (PresentCondition.DEAD.equals(condition) || PresentCondition.BURIED.equals(condition)));
        binding.personDeathDate.clearFocus();
    }

    private void updateApproximateAgeField() {
        Integer birthyear = (Integer)binding.personBirthdateYYYY.getValue();
        TextField approximateAgeTextField = binding.personApproximate1Age;
        SpinnerField approximateAgeTypeField = binding.personApproximateAgeType;

        if(birthyear!=null) {
            deactivateField(approximateAgeTextField);
            deactivateField(approximateAgeTypeField);

            Integer birthday = (Integer)binding.personBirthdateDD.getValue();
            Integer birthmonth = (Integer)binding.personBirthdateMM.getValue();

            Calendar birthDate = new GregorianCalendar();
            birthDate.set(birthyear, birthmonth!=null?birthmonth-1:0, birthday!=null?birthday:1);

            Date to = new Date();
            if(binding.personDeathDate.getValue() != null) {
                to = binding.personDeathDate.getValue();
            }
            DataHelper.Pair<Integer, ApproximateAgeType> approximateAge = ApproximateAgeHelper.getApproximateAge(birthDate.getTime(),to);
            ApproximateAgeType ageType = approximateAge.getElement1();
            approximateAgeTextField.setValue(String.valueOf(approximateAge.getElement0()));
            for (int i=0; i<approximateAgeTypeField.getCount(); i++) {
                Item item = (Item)approximateAgeTypeField.getItemAtPosition(i);
                if (item != null && item.getValue() != null && item.getValue().equals(ageType)) {
                    approximateAgeTypeField.setValue(i);
                    break;
                }
            }
        } else {
            approximateAgeTextField.setEnabled(true);
            approximateAgeTypeField.setEnabled(true);
        }
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getPerson();
    }

}