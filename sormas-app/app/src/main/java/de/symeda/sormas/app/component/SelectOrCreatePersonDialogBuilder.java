package de.symeda.sormas.app.component;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.databinding.PersonSelectOrCreateFragmentLayoutBinding;
import de.symeda.sormas.app.person.PersonSelectVO;
import de.symeda.sormas.app.util.Consumer;


public class SelectOrCreatePersonDialogBuilder extends AlertDialog.Builder {

    private final PersonSelectOrCreateFragmentLayoutBinding bindingPersonSelect;
    final Consumer positiveCallback;
    final Person person;

    public SelectOrCreatePersonDialogBuilder(final FragmentActivity activity, final Person person, final List<Person> existingPersons, final Consumer positiveCallback ) {
        super(activity);

        SormasApplication application = (SormasApplication) activity.getApplication();
        final Tracker tracker = application.getDefaultTracker();

        this.positiveCallback = positiveCallback;
        this.person = person;

        final PersonSelectVO personSelectVO = new PersonSelectVO(person);

        this.setTitle(activity.getResources().getString(R.string.headline_pick_person));

        bindingPersonSelect = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.person_select_or_create_fragment_layout, null,false);
        bindingPersonSelect.setVo(personSelectVO);
        final View dialogView = bindingPersonSelect.getRoot();
        this.setView(dialogView);

        Button searchBtn = (Button) dialogView.findViewById(R.id.personSelect_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Person> existingPersons = null;
                // update person name from the fields
                person.setFirstName(bindingPersonSelect.personFirstName.getValue());
                person.setLastName(bindingPersonSelect.personLastName.getValue());

                // search for existing person with this name
                existingPersons = DatabaseHelper.getPersonDao().getAllByName(person.getFirstName(), person.getLastName());
                updatePersonSelectRadioGroupField(person, existingPersons, dialogView);
            }
        });

        this.setPositiveButton(activity.getResources().getString(R.string.action_select_person), null);
        this.setNegativeButton(activity.getResources().getString(R.string.action_create), null);
        this.setNeutralButton(activity.getResources().getString(R.string.action_cancel), null);

        updatePersonSelectRadioGroupField(person, existingPersons, dialogView);
    }

    private void updatePersonSelectRadioGroupField(Person person, List<Person> existingPersons, View dialogView) {
        List<Item> items = new ArrayList<Item>();
        for (Person existingPerson: existingPersons) {
            StringBuilder sb = new StringBuilder();
            sb.append(existingPerson.toString());
            sb.append(existingPerson.getSex()!=null || existingPerson.getApproximateAge()!=null || existingPerson.getPresentCondition()!=null
                    || existingPerson.getBirthdateDD()!=null || existingPerson.getBirthdateMM()!=null || existingPerson.getBirthdateYYYY()!=null ? "<br />":"");
            sb.append(existingPerson.getSex()!=null ? existingPerson.getSex():"");
            sb.append(existingPerson.getBirthdateDD()!=null&&existingPerson.getBirthdateMM()!=null&&existingPerson.getBirthdateYYYY()!=null ? " | " +
                    existingPerson.getBirthdateDD() + "/" + existingPerson.getBirthdateMM() + "/" + existingPerson.getBirthdateYYYY() : "");
            sb.append(existingPerson.getApproximateAge()!=null ? " | " + existingPerson.getApproximateAge() + " " +existingPerson.getApproximateAgeType():"");
            sb.append(existingPerson.getPresentCondition()!=null ? " | " +  existingPerson.getPresentCondition():"");
            if (existingPerson.getAddress() != null) {
                sb.append(existingPerson.getAddress().getDistrict() != null || existingPerson.getAddress().getCity() != null ? "<br />" : "");
                sb.append(existingPerson.getAddress().getDistrict() != null ? existingPerson.getAddress().getCity() != null ? existingPerson.getAddress().getDistrict() + ", " +
                        existingPerson.getAddress().getCity() : existingPerson.getAddress().getDistrict() :
                        existingPerson.getAddress().getCity() != null ? existingPerson.getAddress().getCity() : "");
            }
            items.add(new Item<Person>(Html.fromHtml(sb.toString()).toString(),existingPerson));
        }

        final RadioGroupField radioGroupField = (RadioGroupField) dialogView.findViewById(R.id.personSelect_selectedPerson);
        radioGroupField.removeAllItems();
        for(Item item : items) {
            radioGroupField.addItem(item);
        }

        // default select new person
        radioGroupField.setValue(person);
    }

    public void setButtonListeners(final AlertDialog dialog, final Activity activity) {
        // use the selected person (displays an error if no person is selected)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bindingPersonSelect.personSelectSelectedPerson.getValue() != null) {
                    positiveCallback.accept(bindingPersonSelect.personSelectSelectedPerson.getValue());
                    dialog.dismiss();
                } else {
                    Snackbar.make(dialog.getWindow().getDecorView(), activity.getString(R.string.snackbar_select_create_person), Snackbar.LENGTH_LONG).show();
                }
            }
        });

        // dismiss the dialog
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positiveCallback.accept(null);
                dialog.dismiss();
            }
        });

        // build a new person with the entered details (displays an error if the details are incomplete)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bindingPersonSelect.personFirstName.getValue().isEmpty() || bindingPersonSelect.personLastName.getValue().isEmpty()) {
                    Snackbar.make(dialog.getWindow().getDecorView(), activity.getString(R.string.snackbar_person_first_last_name), Snackbar.LENGTH_LONG).show();
                } else {
                    person.setFirstName(bindingPersonSelect.personFirstName.getValue());
                    person.setLastName(bindingPersonSelect.personLastName.getValue());
                    positiveCallback.accept(person);
                    dialog.dismiss();
                }
            }
        });
    }
}
