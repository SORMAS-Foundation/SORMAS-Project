/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.util;

import android.os.Bundle;

import de.symeda.sormas.api.utils.EpiWeek;

public class Bundler {

	private static final String FINISH_INSTEAD_OF_UP_NAV = "finishInsteadOfUpNav";
	private static final String ACTIVE_PAGE_POSITION = "activePagePosition";
	private static final String ROOT_UUID = "rootUuid";
	private static final String LIST_FILTER = "listFilter";
	private static final String CASE_UUID = "caseUuid";
	private static final String CONTACT_UUID = "contactUuid";
	private static final String EVENT_UUID = "eventUuid";
	private static final String EVENT_PARTICIPANT_UUID = "eventParticipantUuid";
	private static final String EPI_WEEK = "epiWeek";
	private static final String EMPTY_REPORT_DATE = "emptyReportDate";
	private static final String PRESCRIPTION_UUID = "prescriptionUuid";

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

	public Bundler setActivePagePosition(int activePagePosition) {
		bundle.putInt(ACTIVE_PAGE_POSITION, activePagePosition);
		return this;
	}

	public int getActivePagePosition() {
		if (bundle.containsKey(ACTIVE_PAGE_POSITION)) {
			return bundle.getInt(ACTIVE_PAGE_POSITION);
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

	public Bundler setEmptyReportDate(boolean emptyReportDate) {
		bundle.putBoolean(EMPTY_REPORT_DATE, emptyReportDate);
		return this;
	}

	/**
	 * Default value is 'false'
	 */
	public boolean getEmptyReportDate() {
		if (bundle.containsKey(EMPTY_REPORT_DATE)) {
			return bundle.getBoolean(EMPTY_REPORT_DATE);
		}
		return false;
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

	public Bundler setPrescriptionUuid(String prescriptionUuid) {
		bundle.putString(PRESCRIPTION_UUID, prescriptionUuid);
		return this;
	}

	public String getPrescriptionUuid() {
		if (bundle.containsKey(PRESCRIPTION_UUID)) {
			return bundle.getString(PRESCRIPTION_UUID);
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

	public Bundler setEventParticipantUuid(String eventParticipantUuid) {
		bundle.putString(EVENT_PARTICIPANT_UUID, eventParticipantUuid);
		return this;
	}

	public String getEventParticipantUuid() {
		if (bundle.containsKey(EVENT_PARTICIPANT_UUID)) {
			return bundle.getString(EVENT_PARTICIPANT_UUID);
		}
		return null;
	}

	public Bundler setEpiWeek(EpiWeek epiWeek) {
		bundle.putSerializable(EPI_WEEK, epiWeek);
		return this;
	}

	public EpiWeek getEpiWeek() {
		if (bundle.containsKey(EPI_WEEK)) {
			return (EpiWeek) bundle.getSerializable(EPI_WEEK);
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
