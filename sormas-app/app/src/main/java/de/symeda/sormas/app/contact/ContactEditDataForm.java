package de.symeda.sormas.app.contact;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.databinding.ContactDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 02.11.2016.
 */
public class ContactEditDataForm extends FormTab {

    private ContactDataFragmentLayoutBinding binding;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        binding.contactLastContactDate.initialize(this);
        binding.contactContactProximity.initialize(ContactProximity.class);

        FieldHelper.initSpinnerField(binding.contactRelationToCase, ContactRelation.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.contactCazeUuid.makeLink(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.getContact().getCaze() != null) {
                    final CaseDao caseDao = DatabaseHelper.getCaseDao();
                    final Case caze = caseDao.queryUuid(binding.getContact().getCaze().getUuid());
                    showCaseEditView(caze);
                }
            }
        });
    }

    public void showCaseEditView(Case caze) {
        Intent intent = new Intent(getActivity(), CaseEditActivity.class);
        intent.putExtra(CaseEditActivity.KEY_CASE_UUID, caze.getUuid());
        startActivity(intent);
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getContact();
    }

}