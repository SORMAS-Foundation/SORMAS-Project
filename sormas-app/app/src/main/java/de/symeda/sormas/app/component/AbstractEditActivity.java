package de.symeda.sormas.app.component;

import android.view.Menu;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.symptoms.Symptoms;

public abstract class AbstractEditActivity extends AbstractTabActivity {

    // TODO #4 use android ID's for parameters
    protected void updateActionBarGroups(Menu menu, boolean help, boolean addNewEntry, boolean save) {
        // TODO #4 all groups invisible first
        menu.setGroupVisible(R.id.group_action_help,help);
        menu.setGroupVisible(R.id.group_action_add,addNewEntry);
        menu.setGroupVisible(R.id.group_action_save,save);
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
                symptoms.getRefusalFeedorDrink(), symptoms.getJointPain(), symptoms.getShock(), symptoms.getHiccups(),
                symptoms.getOtherNonHemorrhagicSymptoms());

        for(SymptomState symptomState : symptomStates) {
            if(symptomState == SymptomState.YES) {
                return true;
            }
        }

        return false;
    }
}
