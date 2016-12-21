package de.symeda.sormas.app.component;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.LocationDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDao;
import de.symeda.sormas.app.caze.CaseEditPagerAdapter;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.util.SlidingTabLayout;


/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public abstract class AbstractEditActivity extends AppCompatActivity {

    protected ViewPager pager;
    protected SlidingTabLayout tabs;
    protected int currentTab;

    // TODO #4 use android ID's for parameters
    protected void updateActionBarGroups(Menu menu, boolean help, boolean addNewEntry, boolean save) {
        // TODO #4 all groups invisible first
        menu.setGroupVisible(R.id.group_action_help,help);
        menu.setGroupVisible(R.id.group_action_add,addNewEntry);
        menu.setGroupVisible(R.id.group_action_save,save);
    }

    protected void createTabViews(FragmentStatePagerAdapter adapter) {
        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageSelected(int position) {
                currentTab = position;
                invalidateOptionsMenu();
            }
        });
    }

    protected boolean isAnySymptomSetToYes(Symptoms symptoms) {
        List<SymptomState> symptomStates = Arrays.asList(symptoms.getFever(), symptoms.getVomiting(),
                symptoms.getDiarrhea(), symptoms.getBloodInStool(), symptoms.getNausea(), symptoms.getAbdominalPain(),
                symptoms.getHeadache(), symptoms.getMusclePain(), symptoms.getFatigueWeakness(), symptoms.getUnexplainedBleeding(),
                symptoms.getSkinRash(), symptoms.getNeckStiffness(), symptoms.getSoreThroat(), symptoms.getCough(),
                symptoms.getRunnyNose(), symptoms.getDifficultyBreathing(), symptoms.getChestPain(), symptoms.getConfusedDisoriented(),
                symptoms.getSeizures(), symptoms.getAlteredConsciousness(), symptoms.getConjunctivitis(),
                symptoms.getEyePainLightSensitive(), symptoms.getKopliksSpots(), symptoms.getThrobocytopenia(),
                symptoms.getOtitisMedia(), symptoms.getHearingloss(), symptoms.getDehydration(), symptoms.getAnorexiaAppetiteLoss(),
                symptoms.getRefusalFeedorDrink(), symptoms.getJointPain(), symptoms.getShock(), symptoms.getHiccups());

        boolean symptomSetToYes = symptoms.getOtherNonHemorrhagicSymptomsText()!=null && !symptoms.getOtherNonHemorrhagicSymptomsText().isEmpty();
        for(SymptomState symptomState : symptomStates) {
            if(symptomState == SymptomState.YES) {
                symptomSetToYes = true;
                break;
            }
        }

        return symptomSetToYes;
    }

}
