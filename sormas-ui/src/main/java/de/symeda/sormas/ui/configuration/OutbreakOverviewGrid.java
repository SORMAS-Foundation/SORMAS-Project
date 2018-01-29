package de.symeda.sormas.ui.configuration;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class OutbreakOverviewGrid extends Grid implements ItemClickListener {

	private static final String REGION = "State";

	private UserDto user;

	public OutbreakOverviewGrid() {
		super();
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);		

		user = LoginHelper.getCurrentUser();

		addColumn(REGION, RegionReferenceDto.class);
		for (Disease disease : Disease.values()) {
			addColumn(disease, DiseaseOutbreakInformation.class);
			getColumn(disease).setHeaderCaption(disease.toShortString());
			getColumn(disease).setConverter(new StringToDiseaseOutbreakInformationConverter());
			getColumn(disease).setRenderer(new HtmlRenderer());
		}

		addItemClickListener(this);
	}

	public void reload() {
		Container.Indexed container = getContainerDataSource();
		container.removeAllItems();

		// Initially set all columns to their default value
		for (RegionReferenceDto region : FacadeProvider.getRegionFacade().getAllAsReference()) {
			addItem(region);
		}

		// Alter cells with regions and diseases that actually have an outbreak
		List<OutbreakDto> activeOutbreaks = FacadeProvider.getOutbreakFacade().getAllAfter(null);

		for (OutbreakDto outbreak : activeOutbreaks) {
			DistrictReferenceDto outbreakDistrict = outbreak.getDistrict();
			RegionReferenceDto outbreakRegion = FacadeProvider.getDistrictFacade().getDistrictByUuid(outbreakDistrict.getUuid()).getRegion();
			Disease outbreakDisease = outbreak.getDisease();
			
			((DiseaseOutbreakInformation) container.getItem(outbreakRegion).getItemProperty(outbreakDisease).getValue()).getAffectedDistricts().add(outbreakDistrict);
		}
	}

	@SuppressWarnings("unchecked")
	private void addItem(RegionReferenceDto region) {
		int totalDistricts = FacadeProvider.getDistrictFacade().getCountByRegion(region.getUuid());
		Item item = getContainerDataSource().addItem(region);
		item.getItemProperty(REGION).setValue(region);
		for (Disease disease : Disease.values()) {
			item.getItemProperty(disease).setValue(new DiseaseOutbreakInformation(totalDistricts, region, new HashSet<>()));
		}
	}

	@Override
	public void itemClick(ItemClickEvent event) {
		Item clickedItem = event.getItem();
		
		if (event.getPropertyId() == REGION) {
			return;
		}

		// Open the outbreak configuration window for the clicked row when
		// a) the user is allowed to configure all existing outbreaks or
		// b) the user is allowed to configure outbreaks in his assigned region and has clicked the respective row
		if (LoginHelper.hasUserRight(UserRight.OUTBREAK_CONFIGURE_ALL)) {
			openOutbreakConfigurationWindow((Disease) event.getPropertyId(), (DiseaseOutbreakInformation) clickedItem.getItemProperty((Disease) event.getPropertyId()).getValue());
		} else if (LoginHelper.hasUserRight(UserRight.OUTBREAK_CONFIGURE_RESTRICTED)) {
			if (user.getRegion().equals(clickedItem.getItemProperty(REGION).getValue())) {
				openOutbreakConfigurationWindow((Disease) event.getPropertyId(), (DiseaseOutbreakInformation) clickedItem.getItemProperty((Disease) event.getPropertyId()).getValue());
			}
		} else {
			return;
		}
	}

	private void openOutbreakConfigurationWindow(Disease disease, DiseaseOutbreakInformation diseaseOutbreakInformation) {
		OutbreakConfigurationForm configurationForm = new OutbreakConfigurationForm(disease, diseaseOutbreakInformation);
		final CommitDiscardWrapperComponent<OutbreakConfigurationForm> configurationComponent = new CommitDiscardWrapperComponent<OutbreakConfigurationForm>(configurationForm, null, null);
		Window popupWindow = VaadinUiUtil.showModalPopupWindow(configurationComponent, "Outbreak Configuration");

		configurationComponent.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				Set<DistrictReferenceDto> updatedAffectedDistricts = configurationForm.getAffectedDistricts();
								
				// Add an outbreak for every newly affected district
				for (DistrictReferenceDto affectedDistrict : updatedAffectedDistricts) {
					if (!diseaseOutbreakInformation.getAffectedDistricts().contains(affectedDistrict)) {
						OutbreakDto outbreak = new OutbreakDto();
						outbreak.setDistrict(affectedDistrict);
						outbreak.setDisease(disease);
						outbreak.setReportingUser(LoginHelper.getCurrentUserAsReference());
						outbreak.setReportDate(new Date());
						
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
				reload();
			}
		});

		configurationComponent.addDiscardListener(new DiscardListener() {
			@Override
			public void onDiscard() {
				popupWindow.close();
			}
		});
	}

	private class StringToDiseaseOutbreakInformationConverter implements Converter<String, DiseaseOutbreakInformation> {
		@Override
		public DiseaseOutbreakInformation convertToModel(String value, Class<? extends DiseaseOutbreakInformation> targetType, Locale locale) throws ConversionException {
			throw new ConversionException("Can't convert a DiseaseOutbreakInformation object to a String.");
		}
		@Override
		public String convertToPresentation(DiseaseOutbreakInformation value, Class<? extends String> targetType, Locale locale) throws ConversionException {
			return value.toString();
		}
		public Class<DiseaseOutbreakInformation> getModelType() {
			return DiseaseOutbreakInformation.class;
		}
		public Class<String> getPresentationType() {
			return String.class;
		}
	}

}
