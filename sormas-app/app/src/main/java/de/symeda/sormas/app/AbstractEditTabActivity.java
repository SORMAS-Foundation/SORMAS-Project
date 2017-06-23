package de.symeda.sormas.app;

import android.view.Menu;

public abstract class AbstractEditTabActivity extends AbstractTabActivity {

    // TODO #4 use android ID's for parameters
    protected void updateActionBarGroups(Menu menu, boolean help, boolean markAllAsRead, boolean report, boolean addNewEntry, boolean save) {
        // TODO #4 all groups invisible first
        menu.findItem(R.id.action_options).getSubMenu().setGroupVisible(R.id.group_action_help,help);
        menu.findItem(R.id.action_options).getSubMenu().setGroupVisible(R.id.group_action_markAllAsRead,markAllAsRead);
        menu.findItem(R.id.action_options).getSubMenu().setGroupVisible(R.id.group_action_report,report);
        menu.setGroupVisible(R.id.group_action_add,addNewEntry);
        menu.setGroupVisible(R.id.group_action_save,save);
    }

}
