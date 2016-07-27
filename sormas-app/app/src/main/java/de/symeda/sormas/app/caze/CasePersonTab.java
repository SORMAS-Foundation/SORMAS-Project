package de.symeda.sormas.app.caze;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class CasePersonTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.case_person_layout, container, false);

        String caseUuid = (String) getArguments().getString(Case.UUID);

        CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = caseDao.queryUuid(caseUuid);

        EditText firstNameField = (EditText) v.findViewById(R.id.firstname_field);
        firstNameField.setText(caze.getPerson().getFirstName());

        EditText lastNameField = (EditText) v.findViewById(R.id.lastname_field);
        lastNameField.setText(caze.getPerson().getLastName());

        return v;
    }

}