package de.symeda.sormas.app.symptoms;

import android.view.View;

import org.joda.time.DateTimeComparator;

import java.util.Date;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.databinding.FragmentSymptomsEditLayoutBinding;
import de.symeda.sormas.app.util.Callback;

final class SymptomsValidator {

    static void initializeSymptomsValidation(final FragmentSymptomsEditLayoutBinding contentBinding, final AbstractDomainObject ado) {
        Callback temperatureCallback = new Callback() {
            @Override
            public void call() {
                if (contentBinding.symptomsFever.getVisibility() == View.VISIBLE) {
                    if (contentBinding.symptomsTemperature.getValue() != null
                            && (Float) contentBinding.symptomsTemperature.getValue() >= 38.0f) {
                        if (contentBinding.symptomsFever.getValue() != SymptomState.YES) {
                            contentBinding.symptomsFever.enableErrorState(
                                    R.string.validation_symptoms_fever);
                            return;
                        }
                    }
                }

                contentBinding.symptomsFever.disableErrorState();
            }
        };

        contentBinding.symptomsTemperature.setValidationCallback(temperatureCallback);
        contentBinding.symptomsFever.setValidationCallback(temperatureCallback);

        if (ado instanceof Case) {
            contentBinding.symptomsOnsetDate.addValueChangedListener(new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    Date value = (Date) field.getValue();
                    if (((Case) ado).getHospitalization().getAdmissionDate() != null
                            && DateTimeComparator.getDateOnlyInstance().compare(value, ((Case) ado).getHospitalization().getAdmissionDate()) >= 0) {
                        contentBinding.symptomsOnsetDate.enableWarningState(
                                I18nProperties.getValidationError("beforeDateSoft", contentBinding.symptomsOnsetDate.getCaption(),
                                        I18nProperties.getPrefixFieldCaption(HospitalizationDto.I18N_PREFIX, HospitalizationDto.ADMISSION_DATE)));
                    } else {
                        contentBinding.symptomsOnsetDate.disableWarningState();
                    }
                }
            });
        }
    }

}
