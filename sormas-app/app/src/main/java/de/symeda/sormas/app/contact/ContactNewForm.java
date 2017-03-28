package de.symeda.sormas.app.contact;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Date;

import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.databinding.ContactNewFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 02.11.2016.
 */
public class ContactNewForm extends FormTab {

    private Contact contact;
    private Person person;
    private ContactNewFragmentLayoutBinding binding;

    private Person selectedPersonFromDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {
            person = DataUtils.createNew(Person.class);
            contact = DataUtils.createNew(Contact.class);
            contact.setPerson(person);
            contact.setReportDateTime(new Date());

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.contact_new_fragment_layout, container, false);
        return binding.getRoot();

    }

    @Override
    public void onResume() {
        super.onResume();

        binding.setContact(contact);

        binding.contactLastContactDate.initialize(this);
        binding.contactContactProximity.initialize(ContactProximity.class);

        FieldHelper.initSpinnerField(binding.contactRelationToCase, ContactRelation.class);
    }

    @Override
    public Contact getData() {
        return binding.getContact();
    }

}