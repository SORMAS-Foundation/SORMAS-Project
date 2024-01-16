package de.symeda.sormas.ui.contact.components.linelisting.sharedinfo;

import java.util.function.Consumer;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.contact.components.linelisting.CaseSelector;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;

public class SharedInfoField extends CustomField<SharedInfoFieldDto> {

	private static final long serialVersionUID = 1869000092813141681L;

	private final Binder<SharedInfoFieldDto> binder = new Binder<>(SharedInfoFieldDto.class);

	private final CaseSelector caseSelector;
	private final ComboBox<Disease> disease;
	private final ComboBox<RegionReferenceDto> region;
	private final ComboBox<DistrictReferenceDto> district;

	private final Disease initialDiseaseValue;

	public SharedInfoField(CaseReferenceDto caseReferenceDto, Disease initialDiseaseValue) {
		caseSelector = caseReferenceDto != null
			? new CaseSelector(caseReferenceDto)
			: new CaseSelector(I18nProperties.getString(Strings.infoNoSourceCaseSelectedLineListing));
		disease = new ComboBox<>(I18nProperties.getCaption(Captions.lineListingDiseaseOfSourceCase));
		region = new ComboBox<>(I18nProperties.getCaption(Captions.Region));
		district = new ComboBox<>(I18nProperties.getCaption(Captions.District));
		region.setItemCaptionGenerator(item -> item.buildCaption());
		district.setItemCaptionGenerator(item -> item.buildCaption());

		this.initialDiseaseValue = initialDiseaseValue;
	}

	public SharedInfoField(CaseDataDto caseDataDto) {
		this(
			caseDataDto == null ? null : caseDataDto.toReference(),
			caseDataDto == null ? FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease() : caseDataDto.getDisease());
	}

	public SharedInfoField(EventDto eventDto) {
		this(null, eventDto == null ? FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease() : eventDto.getDisease());
	}

	@Override
	protected Component initContent() {
		setValue(new SharedInfoFieldDto());

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(false);
		layout.setSpacing(false);

		caseSelector.setId("caseSelector");
		layout.addComponent(caseSelector);

		HorizontalLayout sharedInformationBar = new HorizontalLayout();
		sharedInformationBar.addStyleName(CssStyles.SPACING_SMALL);

		disease.setId("disease");
		disease.setItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		sharedInformationBar.addComponent(disease);

		region.setId("region");
		sharedInformationBar.addComponent(region);

		district.setId("district");
		sharedInformationBar.addComponent(district);

		binder.forField(caseSelector).bind(SharedInfoFieldDto.CAZE);
		binder.forField(disease).asRequired().bind(SharedInfoFieldDto.DISEASE);
		binder.forField(region).asRequired().bind(SharedInfoFieldDto.REGION);
		binder.forField(district).asRequired().bind(SharedInfoFieldDto.DISTRICT);

		caseSelector.addValueChangeListener(e -> {
			CaseReferenceDto caseReferenceDto = e.getValue();
			if (caseReferenceDto != null) {
				CaseDataDto selectedCase = FacadeProvider.getCaseFacade().getCaseDataByUuid(e.getValue().getUuid());
				disease.setSelectedItem(selectedCase.getDisease());
			} else {
				disease.setSelectedItem(null);
			}
			disease.setEnabled(caseReferenceDto == null);

			binder.getBinding(SharedInfoFieldDto.DISEASE).get().setAsRequiredEnabled(caseReferenceDto == null);
			binder.getBinding(SharedInfoFieldDto.REGION).get().setAsRequiredEnabled(caseReferenceDto == null);
			binder.getBinding(SharedInfoFieldDto.DISTRICT).get().setAsRequiredEnabled(caseReferenceDto == null);
		});

		if (initialDiseaseValue != null) {
			disease.setSelectedItem(initialDiseaseValue);
			disease.setEnabled(false);
		}

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = e.getValue();
			updateDistricts(regionDto);
		});

		layout.addComponent(sharedInformationBar);

		UserProvider currentUserProvider = UserProvider.getCurrent();
		if (currentUserProvider != null && currentUserProvider.hasRegionJurisdictionLevel()) {
			RegionReferenceDto userRegion = currentUserProvider.getUser().getRegion();
			region.setValue(userRegion);
			region.setVisible(false);
			updateDistricts(userRegion);
		} else {
			region.setItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		}

		return layout;
	}

	@Override
	protected void doSetValue(SharedInfoFieldDto sharedInfoFieldDto) {
		binder.setBean(sharedInfoFieldDto);
	}

	@Override
	public SharedInfoFieldDto getValue() {
		return binder.getBean();
	}

	public boolean hasErrors() {
		BinderValidationStatus<SharedInfoFieldDto> validationStatus = binder.validate();
		return validationStatus.hasErrors();
	}

	private void updateDistricts(RegionReferenceDto regionDto) {
		FieldHelper.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
	}

	public void addDiseaseChangeHandler(Consumer<Disease> diseaseChangeHandler) {
		disease.addValueChangeListener(e -> diseaseChangeHandler.accept(disease.getValue()));
	}

	public CaseSelector getCaseSelector() {
		return caseSelector;
	}
}
