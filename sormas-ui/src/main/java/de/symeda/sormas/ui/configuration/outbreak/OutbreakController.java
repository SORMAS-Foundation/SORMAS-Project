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
package de.symeda.sormas.ui.configuration.outbreak;

import java.util.Date;
import java.util.Set;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class OutbreakController {
	
	public void openOutbreakConfigurationWindow(Disease disease, OutbreakRegionConfiguration diseaseOutbreakInformation) {
		OutbreakRegionConfigurationForm configurationForm = new OutbreakRegionConfigurationForm(diseaseOutbreakInformation);
		final CommitDiscardWrapperComponent<OutbreakRegionConfigurationForm> configurationComponent = new CommitDiscardWrapperComponent<OutbreakRegionConfigurationForm>(configurationForm);
		Window popupWindow = VaadinUiUtil.showModalPopupWindow(configurationComponent, disease.toShortString() + " Outbreak in " + diseaseOutbreakInformation.getRegion().toString());

		configurationComponent.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				Set<DistrictReferenceDto> updatedAffectedDistricts = configurationForm.getAffectedDistricts();
								
				// Add an outbreak for every newly affected district
				for (DistrictReferenceDto affectedDistrict : updatedAffectedDistricts) {
					if (!diseaseOutbreakInformation.getAffectedDistricts().contains(affectedDistrict)) {
						OutbreakDto outbreak = createOutbreakDto(affectedDistrict, disease);
						FacadeProvider.getOutbreakFacade().saveOutbreak(outbreak);
					}
				}
				
				// Remove outbreaks for districts that are not affected anymore
				for (DistrictReferenceDto prevAffectedDistrict : diseaseOutbreakInformation.getAffectedDistricts()) {
					if (!updatedAffectedDistricts.contains(prevAffectedDistrict)) {
						FacadeProvider.getOutbreakFacade().deleteOutbreak(FacadeProvider.getOutbreakFacade().getByDistrictAndDisease(prevAffectedDistrict, disease));
					}
				}
				
				popupWindow.close();
				Notification.show("Outbreak information saved", Type.WARNING_MESSAGE);
				SormasUI.get().getNavigator().navigateTo(OutbreaksView.VIEW_NAME);	
			}
		});

		configurationComponent.addDiscardListener(new DiscardListener() {
			@Override
			public void onDiscard() {
				popupWindow.close();
			}
		});
	}
	
	private OutbreakDto createOutbreakDto(DistrictReferenceDto district, Disease disease) {
		OutbreakDto outbreak = new OutbreakDto();
		outbreak.setDistrict(district);
		outbreak.setDisease(disease);
		outbreak.setReportingUser(CurrentUser.getCurrent().getUserReference());
		outbreak.setReportDate(new Date());
		
		return outbreak;
	}

}
