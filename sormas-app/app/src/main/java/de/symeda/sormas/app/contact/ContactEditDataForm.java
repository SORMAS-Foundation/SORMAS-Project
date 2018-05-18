package de.symeda.sormas.app.contact;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.caze.CaseNewActivity;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.LabelField;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.databinding.ContactDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.validation.ContactValidator;

/**
 * Created by Stefan Szczesny on 02.11.2016.
 */
public class ContactEditDataForm extends FormTab {

    private ContactDataFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.contact_data_fragment_layout, container, false);

        editOrCreateUserRight = (UserRight) getArguments().get(EDIT_OR_CREATE_USER_RIGHT);

        final String contactUuid = getArguments().getString(Contact.UUID);
        Contact contact = DatabaseHelper.getContactDao().queryUuid(contactUuid);
        binding.setContact(contact);

        binding.contactLastContactDate.initialize(this);
        binding.contactContactProximity.initialize(ContactProximity.class);

        FieldHelper.initSpinnerField(binding.contactContactClassification, ContactClassification.class);
        FieldHelper.initSpinnerField(binding.contactRelationToCase, ContactRelation.class);

        if (contact.getResultingCaseUuid() == null) {
            Button createCaseButton = binding.contactCreateCase;
            createCaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), CaseNewActivity.class);
                    intent.putExtra(CaseNewActivity.CONTACT, contactUuid);
                    startActivity(intent);
                }
            });
            binding.contactLayoutAssociatedCase.setVisibility(View.GONE);
        } else {
            LabelField associatedCaseLabel = binding.contactAssociatedCase;
            associatedCaseLabel.setValue(DataHelper.getShortUuid(contact.getResultingCaseUuid()));
            final Case resultingCase = DatabaseHelper.getCaseDao().queryUuidReference(contact.getResultingCaseUuid());
            if (resultingCase != null) {
                associatedCaseLabel.makeLink(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCaseEditView(resultingCase);
                    }
                });
            }
            binding.contactCreateCase.setVisibility(View.GONE);
        }

        setVisibilityByDisease(ContactDto.class, contact.getCaze().getDisease(), (ViewGroup)binding.getRoot());

        binding.contactLastContactDate.makeFieldSoftRequired();
        binding.contactContactProximity.makeFieldSoftRequired();
        binding.contactRelationToCase.makeFieldSoftRequired();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.contactCazeUuid.makeLink(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.getContact().getCaze() != null) {
                    final CaseDao caseDao = DatabaseHelper.getCaseDao();
                    final Case caze = caseDao.queryUuidReference(binding.getContact().getCaze().getUuid());
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
        return binding == null ? null : binding.getContact();
    }

    public ContactDataFragmentLayoutBinding getBinding() {
        return binding;
    }

}