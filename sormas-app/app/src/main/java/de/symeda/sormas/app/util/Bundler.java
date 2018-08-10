package de.symeda.sormas.app.util;

import android.os.Bundle;

public class Bundler {

    private static final String FINISH_INSTEAD_OF_UP_NAV = "finishInsteadOfUpNav";
    private static final String ACTIVE_PAGE_KEY = "activePageKey";
    private static final String ROOT_UUID = "rootUuid";
    private static final String LIST_FILTER = "listFilter";
    private static final String CASE_UUID = "caseUuid";
    private static final String CONTACT_UUID = "contactUuid";
    private static final String EVENT_UUID = "eventUuid";

    private final Bundle bundle;

    public Bundler() {
        this(null);
    }

    public Bundler(Bundle bundle) {
        if (bundle == null) {
            this.bundle = new Bundle();
        } else {
            this.bundle = bundle;
        }
    }

    public Bundle get() {
        return bundle;
    }


    public Bundler setActivePageKey(int activePageKey) {
        bundle.putInt(ACTIVE_PAGE_KEY, activePageKey);
        return this;
    }

    public int getActivePageKey() {
        if (bundle.containsKey(ACTIVE_PAGE_KEY)) {
            return bundle.getInt(ACTIVE_PAGE_KEY);
        }
        return 0;
    }

    public Bundler setRootUuid(String rootUuid) {
        bundle.putString(ROOT_UUID, rootUuid);
        return this;
    }

    public String getRootUuid() {
        if (bundle.containsKey(ROOT_UUID)) {
            return bundle.getString(ROOT_UUID);
        }
        return null;
    }

    public Bundler setListFilter(Enum listFilter) {
        bundle.putSerializable(LIST_FILTER, listFilter);
        return this;
    }

    public Enum getListFilter() {
        if (bundle.containsKey(LIST_FILTER)) {
            return (Enum) bundle.getSerializable(LIST_FILTER);
        }
        return null;
    }

    public Bundler setCaseUuid(String recordUuid) {
        bundle.putString(CASE_UUID, recordUuid);
        return this;
    }

    public String getCaseUuid() {
        if (bundle.containsKey(CASE_UUID)) {
            return bundle.getString(CASE_UUID);
        }
        return null;
    }

    public Bundler setContactUuid(String contactUUid) {
        bundle.putString(CONTACT_UUID, contactUUid);
        return this;
    }

    public String getContactUuid() {
        if (bundle.containsKey(CONTACT_UUID)) {
            return bundle.getString(CONTACT_UUID);
        }
        return null;
    }

    public Bundler setEventUuid(String eventUuid) {
        bundle.putString(EVENT_UUID, eventUuid);
        return this;
    }

    public String getEventUuid() {
        if (bundle.containsKey(EVENT_UUID)) {
            return bundle.getString(EVENT_UUID);
        }
        return null;
    }

    public Bundler setFinishInsteadOfUpNav(boolean finishInsteadOfUpNav) {
        bundle.putBoolean(FINISH_INSTEAD_OF_UP_NAV, finishInsteadOfUpNav);
        return this;
    }

    public boolean isFinishInsteadOfUpNav() {
        if (bundle.containsKey(FINISH_INSTEAD_OF_UP_NAV)) {
            return bundle.getBoolean(FINISH_INSTEAD_OF_UP_NAV);
        }
        return false;
    }
}
