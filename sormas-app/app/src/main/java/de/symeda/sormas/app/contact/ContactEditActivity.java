package de.symeda.sormas.app.contact;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.component.AbstractEditActivity;
import de.symeda.sormas.app.component.PropertyField;


/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class ContactEditActivity extends AbstractEditActivity {

    public static final String KEY_CASE_UUID = "caseUuid";
    public static final String KEY_CONTACT_UUID = "contactUuid";
    public static final String KEY_PAGE = "page";

    private ContactEditPagerAdapter adapter;
    private CharSequence titles[];
    private String caseUuid;
    private String contactUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.case_edit_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_contact));
        }

        // Creating titles for the tabs
        titles = new CharSequence[]{
                getResources().getText(R.string.headline_contact_data),
                getResources().getText(R.string.headline_visits),
                getResources().getText(R.string.headline_person_information)
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle params = getIntent().getExtras();
        caseUuid = params.getString(KEY_CASE_UUID);
        contactUuid = params.getString(KEY_CONTACT_UUID);
        adapter = new ContactEditPagerAdapter(getSupportFragmentManager(), titles, contactUuid);
        createTabViews(adapter);

        if (params.containsKey(KEY_PAGE)) {
            pager.setCurrentItem(params.getInt(KEY_PAGE));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_bar, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch(currentTab) {
            // contact data tab
            case 0:
                updateActionBarGroups(menu, false, false, true);
                break;

            // visits tab
            case 1:
                updateActionBarGroups(menu, false, false, true);
                break;

            // person tab
            case 2:
                updateActionBarGroups(menu, true, false, true);
                break;

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        currentTab = pager.getCurrentItem();




        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                Intent intentCaseContacts = new Intent(this, CaseEditActivity.class);
                intentCaseContacts.putExtra(CaseEditActivity.KEY_PAGE, 3);
                intentCaseContacts.putExtra(CaseEditActivity.KEY_CASE_UUID, caseUuid);
                startActivity(intentCaseContacts);

                //Home/back button
                return true;

            // Help button
            case R.id.action_help:
                switch(currentTab) {
                    // case data tab
                    case 0:

                        break;

                    // case person tab
                    case 1:
                        break;

                    // case symptoms tab
                    case 2:
                        StringBuilder sb = new StringBuilder();

                        LinearLayout caseSymptomsForm = (LinearLayout) this.findViewById(R.id.case_symptoms_form);

                        for (int i = 0; i < caseSymptomsForm.getChildCount(); i++) {
                            if (caseSymptomsForm.getChildAt(i) instanceof PropertyField) {
                                PropertyField propertyField = (PropertyField)caseSymptomsForm.getChildAt(i);
                                sb
                                        .append("<b>"+propertyField.getCaption()+"</b>").append("<br>")
                                        .append(propertyField.getDescription()).append("<br>").append("<br>");
                            }
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(Html.fromHtml(sb.toString())).setTitle(getResources().getText(R.string.headline_help));
                        builder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                        AlertDialog dialog = builder.create();
                        dialog.setCancelable(true);
                        dialog.show();

                        break;
                }


                return true;

            // Save button
            case R.id.action_save:
                ContactDao contactDao = DatabaseHelper.getContactDao();


                switch(currentTab) {
                    // contact data tab
                    case 0:

                        Contact contact = (Contact) adapter.getData(0);

                        contactDao.save(contact);
                        Toast.makeText(this, "contact "+ DataHelper.getShortUuid(contact.getUuid()) +" saved", Toast.LENGTH_SHORT).show();
                        break;

                }

                onResume();
                pager.setCurrentItem(currentTab);

                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
