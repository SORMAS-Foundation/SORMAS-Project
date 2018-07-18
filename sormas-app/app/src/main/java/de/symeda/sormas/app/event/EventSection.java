package de.symeda.sormas.app.event;

public enum EventSection {

    EVENT_INFO,
    EVENT_PERSONS,
    TASKS;

    public static EventSection fromMenuKey(int key) {
        return EventSection.values()[key];
    }
}
