package de.symeda.sormas.app.caze;

public enum CaseSection {

    CASE_INFO,
    PERSON_INFO,
    HOSPITALIZATION,
    SYMPTOMS,
    EPIDEMIOLOGICAL_DATA,
    CONTACTS,
    SAMPLES,
    TASKS;

    public static CaseSection fromMenuKey(int key) {
        return CaseSection.values()[key];
    }
}
