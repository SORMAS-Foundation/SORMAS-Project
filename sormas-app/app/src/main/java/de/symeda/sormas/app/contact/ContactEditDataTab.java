package de.symeda.sormas.app.contact;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.databinding.ContactDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 02.11.2016.
 */
public class ContactEditDataTab extends FormTab {

    private ContactDataFragmentLayoutBinding binding;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initModel();
        binding = DataBindingUtil.inflate(inflater, R.layout.contact_data_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        final String contactUuid = (String) getArguments().getString(Contact.UUID);
        final ContactDao contactDao = DatabaseHelper.getContactDao();
        final Contact contact = contactDao.queryUuid(contactUuid);
        binding.setContact(contact);

    }

    @Override
    protected AbstractDomainObject commit(AbstractDomainObject ado) {
        Contact contact = (Contact) ado;

        return contact;
    }

    @Override
    public AbstractDomainObject getData() {
        return commit(binding.getContact());
    }

}