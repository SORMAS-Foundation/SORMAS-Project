package de.symeda.sormas.app.component;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.databinding.EventParticipantNewFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.PersonSelectOrCreateFragmentLayoutBinding;
import de.symeda.sormas.app.person.PersonSelectVO;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.Item;
import de.symeda.sormas.app.util.ParamCallback;


public class SelectOrCreatePersonDialog extends AlertDialog.Builder {

    private EventParticipantNewFragmentLayoutBinding binding;
    private Person selectedPersonFromDialog;

    public SelectOrCreatePersonDialog(final FragmentActivity activity, final Person person, final List<Person> existingPersons, final ParamCallback positiveCallback ) {
        super(activity);

        final PersonSelectVO personSelectVO = new PersonSelectVO(person);

        this.setTitle(activity.getResources().getString(R.string.headline_pick_person));

        final PersonSelectOrCreateFragmentLayoutBinding bindingPersonSelect = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.person_select_or_create_fragment_layout, null,false);
        bindingPersonSelect.setVo(personSelectVO);
        final View dialogView = bindingPersonSelect.getRoot();
        this.setView(dialogView);


        Button searchBtn = (Button) dialogView.findViewById(R.id.personSelect_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Person> existingPersons = null;
                try {
                    // update person name from the fields
                    person.setFirstName(bindingPersonSelect.personFirstName.getValue());
                    person.setLastName(bindingPersonSelect.personLastName.getValue());

                    // search for existing person with this name
                    existingPersons = DatabaseHelper.getPersonDao().getAllByName(person.getFirstName(), person.getLastName());
                } catch (SQLException e) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                updatePersonSelectRadioGroupField(activity, person, existingPersons, dialogView);
            }
        });

        updatePersonSelectRadioGroupField(activity, person, existingPersons, dialogView);

        this.setPositiveButton(activity.getResources().getString(R.string.action_select_person), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                positiveCallback.call(bindingPersonSelect.personSelectSelectedPerson.getValue());
            }
        });

    }

    private void updatePersonSelectRadioGroupField(FragmentActivity activity, Person person, List<Person> existingPersons, View dialogView) {
        List<Item> items = new ArrayList<Item>();
        for (Person existingPerson: existingPersons) {
            StringBuilder sb = new StringBuilder();
            sb.append(existingPerson.toString());
            sb.append(existingPerson.getSex()!=null || existingPerson.getApproximateAge()!=null || existingPerson.getPresentCondition()!=null ? "<br />":"");
            sb.append(existingPerson.getSex()!=null ? existingPerson.getSex():"");
            sb.append(existingPerson.getApproximateAge()!=null ? " | " + existingPerson.getApproximateAge() + " " +existingPerson.getApproximateAgeType():"");
            sb.append(existingPerson.getPresentCondition()!=null ? " | " +  existingPerson.getPresentCondition():"");
            items.add(new Item<Person>(Html.fromHtml(sb.toString()).toString(),existingPerson));
        }
        Item newPerson = new Item<Person>(activity.getResources().getString(R.string.headline_new_person),person);
        items.add(0,newPerson);

        final RadioGroupField radioGroupField = (RadioGroupField) dialogView.findViewById(R.id.personSelect_selectedPerson);
        radioGroupField.removeAllItems();
        for(Item item : items) {
            radioGroupField.addItem(item);
        }

        // default select new person
        radioGroupField.setValue(person);
    }
}
