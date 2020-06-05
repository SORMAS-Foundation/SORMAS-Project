/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseDataView;
import de.symeda.sormas.ui.contact.ContactDataView;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public class AbstractSampleView extends AbstractSubNavigationView {

	public static final String ROOT_VIEW_NAME = SamplesView.VIEW_NAME;
	
	private SampleReferenceDto sampleRef;
	
	protected AbstractSampleView(String viewName) {
		super(viewName);
	}
	
	@Override
	public void refreshMenu(SubMenu menu, Label infoLabel, Label infoLabelSub, String params) {
		sampleRef = FacadeProvider.getSampleFacade().getReferenceByUuid(params);

		menu.removeAllViews();
		menu.addView(SamplesView.VIEW_NAME, I18nProperties.getCaption(Captions.sampleSamplesList));

		final SampleDto sampleByUuid = FacadeProvider.getSampleFacade().getSampleByUuid(params);
		final CaseReferenceDto caseRef = sampleByUuid.getAssociatedCase();
		if (caseRef != null && UserProvider.getCurrent().hasUserRight(UserRight.CASE_VIEW)) {
			menu.addView(CaseDataView.VIEW_NAME, I18nProperties.getString(Strings.entityCase), caseRef.getUuid(), true);
		}

		final ContactReferenceDto contactRef = sampleByUuid.getAssociatedContact();
		if (contactRef != null && UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW)) {
			menu.addView(ContactDataView.VIEW_NAME, I18nProperties.getString(Strings.entityContact), contactRef.getUuid(), true);
		}
		menu.addView(SampleDataView.VIEW_NAME, I18nProperties.getCaption(SampleDto.I18N_PREFIX), params);
		infoLabel.setValue(sampleRef.getCaption());
		infoLabelSub.setValue(DataHelper.getShortUuid(sampleRef.getUuid()));
	}
	
	@Override
	protected void setSubComponent(Component newComponent) {
		super.setSubComponent(newComponent);
		
		if (FacadeProvider.getSampleFacade().isDeleted(sampleRef.getUuid())) {
			newComponent.setEnabled(false);
		}
	}
	
	public SampleReferenceDto getSampleRef() {
		return sampleRef;
	}

}
