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

package de.symeda.sormas.app.visit.edit;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.databinding.FragmentVisitEditLayoutBinding;
import de.symeda.sormas.app.util.Bundler;

public class VisitEditFragment extends BaseEditFragment<FragmentVisitEditLayoutBinding, Visit, Visit> {

	private Visit record;
	private String contactUuid;
	private Contact contact;

	public static VisitEditFragment newInstance(Visit activityRootData, String contactUuid) {
		return newInstance(VisitEditFragment.class, new Bundler().setContactUuid(contactUuid).get(), activityRootData);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null)
			savedInstanceState = getArguments();
		contactUuid = new Bundler(savedInstanceState).getContactUuid();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		new Bundler(outState).setContactUuid(contactUuid);
	}

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.caption_visit_information);
	}

	@Override
	public Visit getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();
		contact = DatabaseHelper.getContactDao().queryUuid(contactUuid);
	}

	@Override
	public void onLayoutBinding(FragmentVisitEditLayoutBinding contentBinding) {
		contentBinding.setData(record);

		VisitValidator.initializeVisitValidation(contact, contentBinding);

		contentBinding.setVisitStatusClass(VisitStatus.class);
	}

	@Override
	public void onAfterLayoutBinding(FragmentVisitEditLayoutBinding contentBinding) {
		contentBinding.visitVisitDateTime.initializeDateTimeField(getFragmentManager());
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_visit_edit_layout;
	}
}
