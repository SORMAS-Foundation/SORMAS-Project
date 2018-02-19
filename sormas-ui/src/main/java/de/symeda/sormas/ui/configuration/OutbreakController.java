package de.symeda.sormas.ui.configuration;

import java.util.Date;
import java.util.Set;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class OutbreakController {
	
	public void openOutbreakConfigurationWindow(Disease disease, OutbreakRegionConfiguration diseaseOutbreakInformation) {
		OutbreakRegionConfigurationForm configurationForm = new OutbreakRegionConfigurationForm(diseaseOutbreakInformation);
		final CommitDiscardWrapperComponent<OutbreakRegionConfigurationForm> configurationComponent = new CommitDiscardWrapperComponent<OutbreakRegionConfigurationForm>(configurationForm, null);
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
				SormasUI.get().getNavigator().navigateTo(ConfigurationView.VIEW_NAME);	
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
		outbreak.setReportingUser(LoginHelper.getCurrentUserAsReference());
		outbreak.setReportDate(new Date());
		
		return outbreak;
	}

}
