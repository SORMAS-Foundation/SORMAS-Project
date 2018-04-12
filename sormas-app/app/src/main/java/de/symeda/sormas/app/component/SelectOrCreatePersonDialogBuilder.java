package de.symeda.sormas.app.component;

import android.app.Activity;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonName;
import de.symeda.sormas.app.databinding.EventParticipantNewFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.PersonSelectOrCreateFragmentLayoutBinding;
import de.symeda.sormas.app.person.PersonSelectVO;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.Item;
import de.symeda.sormas.app.util.Consumer;


public class SelectOrCreatePersonDialogBuilder extends AlertDialog.Builder {

    private final PersonSelectOrCreateFragmentLayoutBinding bindingPersonSelect;
    final Consumer positiveCallback;
    final Person person;

    public SelectOrCreatePersonDialogBuilder(final FragmentActivity activity, final Person person, final List<PersonName> existingPersons, final List<Person> similarPersons, final Consumer positiveCallback ) {
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
                List<Person> newSimilarPersons = new ArrayList<>();
                for (PersonName existingPerson : existingPersons) {
                    if (PersonHelper.areNamesSimilar(bindingPersonSelect.personFirstName.getValue() + " " + bindingPersonSelect.personLastName.getValue(),
                            existingPerson.getFirstName() + " " + existingPerson.getLastName())) {
                        Person person = DatabaseHelper.getPersonDao().queryForId(existingPerson.getId());
                        newSimilarPersons.add(person);
                    }
                }
                updatePersonSelectRadioGroupField(person, newSimilarPersons, dialogView);
            }
        });

        this.setPositiveButton(activity.getResources().getString(R.string.action_select_person), null);
        this.setNegativeButton(activity.getResources().getString(R.string.action_create), null);
        this.setNeutralButton(activity.getResources().getString(R.string.action_cancel), null);

        updatePersonSelectRadioGroupField(person, similarPersons, dialogView);
    }

    private void updatePersonSelectRadioGroupField(Person person, List<Person> similarPersons, View dialogView) {
        List<Item> items = new ArrayList<Item>();
        for (Person similarPerson : similarPersons) {
            StringBuilder sb = new StringBuilder();
            sb.append(similarPerson.toString());
            sb.append(similarPerson.getSex()!=null || similarPerson.getApproximateAge()!=null || similarPerson.getPresentCondition()!=null
                    || similarPerson.getBirthdateDD()!=null || similarPerson.getBirthdateMM()!=null || similarPerson.getBirthdateYYYY()!=null ? "<br />":"");
            sb.append(similarPerson.getSex()!=null ? similarPerson.getSex():"");
            sb.append(similarPerson.getBirthdateDD()!=null&&similarPerson.getBirthdateMM()!=null&&similarPerson.getBirthdateYYYY()!=null ? " | " +
                    similarPerson.getBirthdateDD() + "/" + similarPerson.getBirthdateMM() + "/" + similarPerson.getBirthdateYYYY() : "");
            sb.append(similarPerson.getApproximateAge()!=null ? " | " + similarPerson.getApproximateAge() + " " +similarPerson.getApproximateAgeType():"");
            sb.append(similarPerson.getPresentCondition()!=null ? " | " +  similarPerson.getPresentCondition():"");
            if (similarPerson.getAddress() != null) {
                sb.append(similarPerson.getAddress().getDistrict() != null || similarPerson.getAddress().getCity() != null ? "<br />" : "");
                sb.append(similarPerson.getAddress().getDistrict() != null ? similarPerson.getAddress().getCity() != null ? similarPerson.getAddress().getDistrict() + ", " +
                        similarPerson.getAddress().getCity() : similarPerson.getAddress().getDistrict() :
                        similarPerson.getAddress().getCity() != null ? similarPerson.getAddress().getCity() : "");
            }
            items.add(new Item<>(Html.fromHtml(sb.toString()).toString(),similarPerson));
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
