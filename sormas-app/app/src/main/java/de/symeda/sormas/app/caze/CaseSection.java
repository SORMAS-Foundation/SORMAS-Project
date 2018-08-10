package de.symeda.sormas.app.caze;

import android.content.Context;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;

public enum CaseSection implements StatusElaborator {

    CASE_INFO,
    PERSON_INFO,
    HOSPITALIZATION,
    SYMPTOMS,
    EPIDEMIOLOGICAL_DATA,
    CONTACTS,
    SAMPLES,
    TASKS;

    public static CaseSection fromOrdinal(int ordinal) {
        return CaseSection.values()[ordinal];
    }

    @Override
    public String getFriendlyName(Context context) {
        switch(this) {
            case CASE_INFO:
                return context.getResources().getString(R.string.caption_case_information);
            case PERSON_INFO:
                return context.getResources().getString(R.string.caption_person_information);
            case HOSPITALIZATION:
                return context.getResources().getString(R.string.caption_case_hospitalization);
            case SYMPTOMS:
                return context.getResources().getString(R.string.caption_symptoms);
            case EPIDEMIOLOGICAL_DATA:
                return context.getResources().getString(R.string.caption_case_epidomiological_data);
            case CONTACTS:
                return context.getResources().getString(R.string.caption_case_contacts);
            case SAMPLES:
                return context.getResources().getString(R.string.caption_case_samples);
            case TASKS:
                return context.getResources().getString(R.string.caption_case_tasks);
            default:
                throw new IllegalArgumentException(this.toString());
        }
    }

    @Override
    public int getColorIndicatorResource() {
        return 0;
    }

    @Override
    public Enum getValue() {
        return this;
    }

    @Override
    public int getIconResourceId() {
        return 0;
    }
}
