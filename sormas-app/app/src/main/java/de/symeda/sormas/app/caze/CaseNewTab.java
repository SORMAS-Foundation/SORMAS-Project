package de.symeda.sormas.app.caze;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class CaseNewTab extends FormTab {

    private Case caze;
    private Person person;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initModel();

        try {
            caze = DataUtils.createNew(Case.class);

            getModel().put(R.id.form_cn_disease, null);
            getModel().put(R.id.form_cn_date_of_report, new Date());
            getModel().put(R.id.form_cn_person, person);
            getModel().put(R.id.form_cn_health_facility, null);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return getActivity().getLayoutInflater().inflate(R.layout.case_new_fragment_layout, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        addSpinnerField(R.id.form_cn_disease, Disease.class);
        addDateField(R.id.form_cn_date_of_report);
        addPersonSpinnerField(R.id.form_cn_person);
        addFacilitySpinnerField(R.id.form_cn_health_facility);

        ImageButton newPersonButton = (ImageButton) getView().findViewById(R.id.form_cn_btn_add_person);
        newPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewPersonDialog(R.id.form_cn_btn_add_person);
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
                    addPersonSpinnerField(R.id.form_cn_person);
                    Spinner spinner = (Spinner) getView().findViewById(R.id.form_cn_person);
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