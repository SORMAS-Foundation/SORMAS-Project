package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.databinding.CasePersonLayoutBinding;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class CasePersonTab extends Fragment {

    CasePersonLayoutBinding binding;
    Person person;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String caseUuid = (String) getArguments().getString(Case.UUID);
        CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = caseDao.queryUuid(caseUuid);


        binding = DataBindingUtil.inflate(inflater, R.layout.case_person_layout, container, false);
        View view = binding.getRoot();
        person = new Person();
        person.setFirstName(caze.getPerson().getFirstName());
        person.setLastName(caze.getPerson().getLastName());
        binding.setPerson(person);

        return view;
    }


    public Person getPerson() {
        return person;
    }
}