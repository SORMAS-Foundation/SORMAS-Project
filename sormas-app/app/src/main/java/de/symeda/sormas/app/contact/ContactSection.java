package de.symeda.sormas.app.contact;

public enum ContactSection {

    CONTACT_INFO,
    PERSON_INFO,
    VISITS,
    TASKS;

    public static ContactSection fromMenuKey(int key) {
        return ContactSection.values()[key];
    }
}
