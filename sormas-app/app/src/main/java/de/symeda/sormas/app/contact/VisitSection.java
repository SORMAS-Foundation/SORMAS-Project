package de.symeda.sormas.app.contact;

public enum VisitSection {

    VISIT_INFO,
    SYMPTOMS;

    public static VisitSection fromMenuKey(int key) {
        return VisitSection.values()[key];
    }
}
