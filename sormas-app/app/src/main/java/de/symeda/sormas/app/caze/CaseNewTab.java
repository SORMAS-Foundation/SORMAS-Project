package de.symeda.sormas.app.caze;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initModel();

        try {
            caze = DataUtils.createNew(Case.class);
            user = ConfigProvider.getUser();
            region = DatabaseHelper.getRegionDao().queryUuid(user.getRegion().getUuid());

            getModel().put(R.id.case_disease, null);
            getModel().put(R.id.case_region, region);
            getModel().put(R.id.case_district, null);
            getModel().put(R.id.case_community, null);
            getModel().put(R.id.case_healthFacility, null);
            getModel().put(R.id.case_person, person);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return getActivity().getLayoutInflater().inflate(R.layout.case_new_fragment_layout, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        addSpinnerField(R.id.case_disease, Disease.class);
        addPersonSpinnerField(R.id.case_person);
        addFacilitySpinnerField(R.id.case_healthFacility);


        final List emptyList = new ArrayList<>();

        addRegionSpinnerField(getModel(), getView(), R.id.case_region, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) getView().findViewById(R.id.case_district);
                Object selectedValue = getModel().get(R.id.case_region);
                if(spinner != null) {
                    List<District> districtList = emptyList;
                    if(selectedValue != null) {
                        districtList = DatabaseHelper.getDistrictDao().getByRegion((Region)selectedValue);
                    }
                    spinner.setAdapter(makeSpinnerAdapter(DataUtils.getItems(districtList)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        addSpinnerField(R.id.case_district, DataUtils.getItems(emptyList), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) getView().findViewById(R.id.case_community);
                Object selectedValue = getModel().get(R.id.case_district);
                if(spinner != null) {
                    List<Community> communityList = emptyList;
                    if(selectedValue != null) {
                        communityList = DatabaseHelper.getCommunityDao().getByDistrict((District)selectedValue);
                    }
                    spinner.setAdapter(makeSpinnerAdapter(DataUtils.getItems(communityList)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        addSpinnerField(R.id.case_community, DataUtils.getItems(emptyList), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) getView().findViewById(R.id.case_healthFacility);
                Object selectedValue = getModel().get(R.id.case_community);
                if(spinner != null) {
                    List<Facility> facilityList = emptyList;
                    if(selectedValue != null) {
                        facilityList = DatabaseHelper.getFacilityDao().getByCommunity((Community)selectedValue);
                    }
                    spinner.setAdapter(makeSpinnerAdapter(DataUtils.getItems(facilityList)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        ImageButton newPersonButton = (ImageButton) getView().findViewById(R.id.case_addPerson_btn);
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

            final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.person_new_frament_layout, null);
            dialogBuilder.setView(dialogView);

            final EditText firstName = (EditText) dialogView.findViewById(R.id.form_p_first_name);
            final EditText lastName = (EditText) dialogView.findViewById(R.id.form_p_last_name);

//            EditText uuid = (EditText) dialogView.findViewById(R.id.form_p_person_id);
//            uuid.setText(personNew.getUuid());
//            uuid.setEnabled(false);

            dialogBuilder.setPositiveButton(getResources().getString(R.string.action_new_person), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    personNew.setFirstName(firstName.getText().toString());
                    personNew.setLastName(lastName.getText().toString());

                    // save person
                    PersonDao personDao = DatabaseHelper.getPersonDao();
                    personDao.save(personNew);

                    new SyncPersonsTask().execute();

                    // refresh reference for pre-selection in personField
                    person = personNew;
                    addPersonSpinnerField(R.id.case_person);
                    Spinner spinner = (Spinner) getView().findViewById(R.id.case_person);
                    spinner.setSelection(spinner.getAdapter().getCount() - 1);
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
        caze.setDisease((Disease)getModel().get(R.id.case_disease));
        caze.setRegion((Region)getModel().get(R.id.case_region));
        caze.setDistrict((District)getModel().get(R.id.case_district));
        caze.setCommunity((Community) getModel().get(R.id.case_community));
        caze.setHealthFacility((Facility)getModel().get(R.id.case_healthFacility));
        caze.setPerson((Person)getModel().get(R.id.case_person));

        return caze;
    }

    @Override
    public Case getData() {
        return (Case) commit(null);
    }

}