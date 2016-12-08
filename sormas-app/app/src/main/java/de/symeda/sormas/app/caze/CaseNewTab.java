package de.symeda.sormas.app.caze;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.SpinnerField;
import de.symeda.sormas.app.component.TextField;
import de.symeda.sormas.app.databinding.CaseNewFragmentLayoutBinding;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class CaseNewTab extends FormTab {

    private Case caze;
    private Person person;
    private User user;
    private Region region;
    private CaseNewFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            caze = DataUtils.createNew(Case.class);
            user = ConfigProvider.getUser();
            region = DatabaseHelper.getRegionDao().queryUuid(user.getRegion().getUuid());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.case_new_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.setCaze(caze);

        addSpinnerField(R.id.caseData_disease, Disease.class);
        addPersonSpinnerField(R.id.caseData_person);
        addFacilitySpinnerField(R.id.caseData_healthFacility);

        final List emptyList = new ArrayList<>();

        addRegionSpinnerField(getView(), R.id.caseData_region, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerField spinnerField = binding.caseDataDistrict;
                Object selectedValue = binding.caseDataRegion.getValue();
                if(spinnerField != null) {
                    List<District> districtList = emptyList;
                    if(selectedValue != null) {
                        districtList = DatabaseHelper.getDistrictDao().getByRegion((Region)selectedValue);
                    }
                    spinnerField.setSpinnerAdapter(DataUtils.getItems(districtList));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        addSpinnerField(R.id.caseData_district, DataUtils.getItems(emptyList), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerField spinnerField = binding.caseDataCommunity;
                Object selectedValue = binding.caseDataDistrict.getValue();
                if(spinnerField != null) {
                    List<Community> communityList = emptyList;
                    if(selectedValue != null) {
                        communityList = DatabaseHelper.getCommunityDao().getByDistrict((District)selectedValue);
                    }
                    spinnerField.setSpinnerAdapter(DataUtils.getItems(communityList));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        addSpinnerField(R.id.caseData_community, DataUtils.getItems(emptyList), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerField spinnerField = binding.caseDataHealthFacility;
                Object selectedValue = binding.caseDataCommunity.getValue();
                if(spinnerField != null) {
                    List<Facility> facilityList = emptyList;
                    if(selectedValue != null) {
                        facilityList = DatabaseHelper.getFacilityDao().getByCommunity((Community)selectedValue);
                    }
                    spinnerField.setSpinnerAdapter(DataUtils.getItems(facilityList));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        ImageButton newPersonButton = binding.caseAddPersonBtn;
        newPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewPersonDialog(R.id.case_addPerson_btn);
            }
        });
    }

    /**
     * Open the dialog for adding a new person.
     * Saving brings a refreshed person-reference.
     * @param newPersonFieldId
     */
    public void showNewPersonDialog(final int newPersonFieldId) {
        try {
            final Person personNew = DataUtils.createNew(Person.class);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle(getResources().getString(R.string.headline_new_Person));

            final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.person_new_fragment_layout, null);
            dialogBuilder.setView(dialogView);

            final TextField firstName = (TextField) dialogView.findViewById(R.id.person_firstName);
            final TextField lastName = (TextField) dialogView.findViewById(R.id.person_lastName);

//            EditText uuid = (EditText) dialogView.findViewById(R.id.form_p_person_id);
//            uuid.setText(personNew.getUuid());
//            uuid.setEnabled(false);

            dialogBuilder.setPositiveButton(getResources().getString(R.string.action_new_person), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    personNew.setFirstName(firstName.getValue().toString());
                    personNew.setLastName(lastName.getValue().toString());

                    // save person
                    PersonDao personDao = DatabaseHelper.getPersonDao();
                    personDao.save(personNew);

                    new SyncPersonsTask().execute();

                    // refresh reference for pre-selection in personField
                    // DOES WORK NOW because we've got our own awesome custom fields :D
                    person = personNew;
                    addPersonSpinnerField(R.id.caseData_person);
                    SpinnerField spinner = binding.caseDataPerson;
                    spinner.setValue(person);
                    //reloadFragment();
                }
            });
            dialogBuilder.setNegativeButton(getResources().getString(R.string.action_dimiss), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog newPersonDialog = dialogBuilder.create();
            newPersonDialog.show();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected AbstractDomainObject commit(AbstractDomainObject ado) {
        return null;
    }

    @Override
    public Case getData() {
        return binding.getCaze();
    }

}