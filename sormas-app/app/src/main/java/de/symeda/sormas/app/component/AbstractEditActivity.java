package de.symeda.sormas.app.component;

import android.view.Menu;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.symptoms.Symptoms;

public abstract class AbstractEditActivity extends AbstractTabActivity {

    // TODO #4 use android ID's for parameters
    protected void updateActionBarGroups(Menu menu, boolean help, boolean report, boolean addNewEntry, boolean save) {
        // TODO #4 all groups invisible first
        menu.findItem(R.id.action_options).getSubMenu().setGroupVisible(R.id.group_action_help,help);
        menu.findItem(R.id.action_options).getSubMenu().setGroupVisible(R.id.group_action_report,report);
        menu.setGroupVisible(R.id.group_action_add,addNewEntry);
        menu.setGroupVisible(R.id.group_action_save,save);
    }

}
