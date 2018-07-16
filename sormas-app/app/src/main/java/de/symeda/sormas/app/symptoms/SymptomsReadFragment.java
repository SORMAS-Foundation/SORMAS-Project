package de.symeda.sormas.app.symptoms;

import android.content.res.Resources;
import android.os.Bundle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.databinding.FragmentSymptomsReadLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.VisitFormNavigationCapsule;

public class SymptomsReadFragment extends BaseReadFragment<FragmentSymptomsReadLayoutBinding, Symptoms, Case> {

    public static final String TAG = SymptomsReadFragment.class.getSimpleName();

    private Symptoms record;

    private List<String> yesResult;
    private List<String> unknownResult;

    // TODO can probably be removed
    private OnLinkClickListener onLinkClickListener;

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        AbstractDomainObject ado = getActivityRootData();
        if (ado instanceof Case) {
            record = ((Case) ado).getSymptoms();
        } else if (ado instanceof Visit) {
            record = ((Visit) ado).getSymptoms();
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
        contentBinding.setLocationClickCallback(onLinkClickListener);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_symptom_information);
    }

    @Override
    public Symptoms getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_symptoms_read_layout;
    }

    public static SymptomsReadFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(SymptomsReadFragment.class, capsule, activityRootData);
    }

    public static SymptomsReadFragment newInstance(VisitFormNavigationCapsule capsule, Visit activityRootData) {
        return newInstance(SymptomsReadFragment.class, capsule, activityRootData);
    }

    private void extractSymptoms() {

        yesResult = new ArrayList<>();
        unknownResult = new ArrayList<>();

        for (String symptomPropertyId : SymptomsHelper.getSymptomPropertyIds()) {
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
                            yesResult.add(I18nProperties.getPrefixFieldCaption(SymptomsDto.I18N_PREFIX, symptomPropertyId));
                            break;
                        default:
                            throw new IllegalArgumentException(symptomState.toString());
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
