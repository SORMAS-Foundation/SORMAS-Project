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

import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.caze.CaseDataView;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public class AbstractSampleView extends AbstractSubNavigationView {

	public static final String ROOT_VIEW_NAME = SamplesView.VIEW_NAME;
	
	private SampleReferenceDto sampleRef;
	
	protected AbstractSampleView(String viewName) {
		super(viewName);
	}
	
	@Override
	public void refreshMenu(SubNavigationMenu menu, Label infoLabel, Label infoLabelSub, String params) {
		sampleRef = FacadeProvider.getSampleFacade().getReferenceByUuid(params);
		CaseReferenceDto caseRef = FacadeProvider.getSampleFacade().getSampleByUuid(params).getAssociatedCase();
		
		menu.removeAllViews();
		menu.addView(SamplesView.VIEW_NAME, "Samples list");
		if(caseRef != null) {
			menu.addView(CaseDataView.VIEW_NAME, "Case", caseRef.getUuid(), true);
		}
		menu.addView(SampleDataView.VIEW_NAME, I18nProperties.getFieldCaption(SampleDto.I18N_PREFIX), params);
		infoLabel.setValue(sampleRef.getCaption());
		infoLabelSub.setValue(DataHelper.getShortUuid(sampleRef.getUuid()));
	}
	
	public SampleReferenceDto getSampleRef() {
		return sampleRef;
	}

}
