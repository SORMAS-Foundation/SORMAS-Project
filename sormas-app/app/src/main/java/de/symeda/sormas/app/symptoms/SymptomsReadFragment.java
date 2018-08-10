package de.symeda.sormas.app.symptoms;

import android.content.res.Resources;
import android.os.Bundle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.databinding.FragmentSymptomsReadLayoutBinding;

import static android.view.View.GONE;

public class SymptomsReadFragment extends BaseReadFragment<FragmentSymptomsReadLayoutBinding, Symptoms, Case> {

    public static final String TAG = SymptomsReadFragment.class.getSimpleName();

    private Symptoms record;
    private Disease disease;

    private List<String> yesResult;
    private List<String> unknownResult;

    public static SymptomsReadFragment newInstance(Case activityRootData) {
        return newInstance(SymptomsReadFragment.class, null, activityRootData);
    }

    public static SymptomsReadFragment newInstance(Visit activityRootData) {
        return newInstance(SymptomsReadFragment.class, null, activityRootData);
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        AbstractDomainObject ado = getActivityRootData();
        if (ado instanceof Case) {
            record = ((Case) ado).getSymptoms();
            disease = ((Case) ado).getDisease();
        } else if (ado instanceof Visit) {
            record = ((Visit) ado).getSymptoms();
            disease = ((Visit) ado).getDisease();
        } else {
            throw new UnsupportedOperationException("ActivityRootData of class " + ado.getClass().getSimpleName()
                    + " does not support PersonReadFragment");
        }

        extractSymptoms();
    }

    @Override
    public void onLayoutBinding(FragmentSymptomsReadLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.symptomsSymptomsOccurred.setTags(yesResult);
        contentBinding.symptomsSymptomsUnknownOccurred.setTags(unknownResult);

        if (!Diseases.DiseasesConfiguration.isDefined(SymptomsDto.class, SymptomsDto.LESIONS, disease)) {
            contentBinding.symptomsLesionsLayout.setVisibility(GONE);
        }
    }

    @Override
    public void onAfterLayoutBinding(FragmentSymptomsReadLayoutBinding contentBinding) {
        setVisibilityByDisease(SymptomsDto.class, disease, contentBinding.mainContent);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_symptoms);
    }

    @Override
    public Symptoms getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_symptoms_read_layout;
    }

    private void extractSymptoms() {
        yesResult = new ArrayList<>();
        unknownResult = new ArrayList<>();

        for (String symptomPropertyId : SymptomsHelper.getSymptomPropertyIds()) {
            // Skip fields that don't belong in this list
            if (SymptomsHelper.isSpecialSymptom(symptomPropertyId)) {
                continue;
            }

            try {
                Method getter = Symptoms.class.getDeclaredMethod("get" + DataHelper.capitalize(symptomPropertyId));
                SymptomState symptomState = (SymptomState) getter.invoke(record);
                if (symptomState != null) {
                    switch (symptomState) {
                        case YES:
                            yesResult.add(I18nProperties.getPrefixFieldCaption(SymptomsDto.I18N_PREFIX, symptomPropertyId));
                            break;
                        case NO:
                            // ignore this
                            break;
                        case UNKNOWN:
                            unknownResult.add(I18nProperties.getPrefixFieldCaption(SymptomsDto.I18N_PREFIX, symptomPropertyId));
                            break;
                        default:
                            throw new IllegalArgumentException(symptomState.toString());
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        Collections.sort(yesResult, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(unknownResult, String.CASE_INSENSITIVE_ORDER);
    }
}
