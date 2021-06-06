package de.symeda.sormas.ui.contact.components.linelisting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.contact.components.linelisting.contactfield.ContactFieldDto;
import de.symeda.sormas.ui.contact.components.linelisting.contactfield.ContactLineField;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.components.linelisting.line.DeleteLineEvent;
import de.symeda.sormas.ui.utils.components.linelisting.line.LineLayout;

public class LineListingLayout extends VerticalLayout {

	public static final float DEFAULT_WIDTH = 1696;

	private final CaseSelector caseSelector;
	private final ComboBox<Disease> disease;
	private final ComboBox<RegionReferenceDto> region;
	private final ComboBox<DistrictReferenceDto> district;

	private final List<ContactLineLayout> contactLines;

	private final Window window;
	private Consumer<List<ContactLineDto>> saveCallback;

	public LineListingLayout(Window window) {

		this.window = window;

		setSpacing(false);

		VerticalLayout sharedInformationComponent = new VerticalLayout();
		sharedInformationComponent.setMargin(false);
		sharedInformationComponent.setSpacing(false);
		Label sharedInformationLabel = new Label();
		sharedInformationLabel.setValue(I18nProperties.getCaption(Captions.lineListingSharedInformation));
		sharedInformationLabel.addStyleName(CssStyles.H3);
		sharedInformationComponent.addComponent(sharedInformationLabel);

		caseSelector = new CaseSelector();
		caseSelector.setId("lineListingCase");
		sharedInformationComponent.addComponent(caseSelector);

		HorizontalLayout sharedInformationBar = new HorizontalLayout();
		sharedInformationBar.addStyleName(CssStyles.SPACING_SMALL);

		disease = new ComboBox<>(I18nProperties.getCaption(Captions.disease));
		disease.setId("lineListingDisease");
		disease.setItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		sharedInformationBar.addComponent(disease);

		region = new ComboBox<>(I18nProperties.getCaption(Captions.region));
		region.setId("lineListingRegion");
		sharedInformationBar.addComponent(region);

		district = new ComboBox<>(I18nProperties.getCaption(Captions.district));
		district.setId("lineListingDistrict");
		sharedInformationBar.addComponent(district);

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = e.getValue();
			updateDistricts(regionDto);
		});

		sharedInformationComponent.addComponent(sharedInformationBar);

		addComponent(sharedInformationComponent);

		contactLines = new ArrayList<>();
		VerticalLayout lineComponent = new VerticalLayout();
		lineComponent.setMargin(false);

		Label lineComponentLabel = new Label();
		lineComponentLabel.setValue(I18nProperties.getCaption(Captions.lineListingNewCasesList));
		lineComponentLabel.addStyleName(CssStyles.H3);
		lineComponent.addComponent(lineComponentLabel);

		ContactLineLayout line = buildNewLine(lineComponent);
		contactLines.add(line);
		lineComponent.addComponent(line);
		lineComponent.setSpacing(false);
		addComponent(lineComponent);

		UserProvider currentUserProvider = UserProvider.getCurrent();
		if (currentUserProvider != null && UserRole.isSupervisor(currentUserProvider.getUserRoles())) {
			RegionReferenceDto userRegion = currentUserProvider.getUser().getRegion();
			region.setValue(userRegion);
			region.setVisible(false);
			updateDistricts(userRegion);
		} else {
			region.setItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		}

		HorizontalLayout actionBar = new HorizontalLayout();
		Button addLine = ButtonHelper.createIconButton(Captions.lineListingAddLine, VaadinIcons.PLUS, e -> {
			ContactLineLayout newLine = buildNewLine(lineComponent);
			contactLines.add(newLine);
			lineComponent.addComponent(newLine);
			contactLines.get(0).enableDelete(true);
		}, ValoTheme.BUTTON_PRIMARY);

		actionBar.addComponent(addLine);
		actionBar.setComponentAlignment(addLine, Alignment.MIDDLE_LEFT);

		addComponent(actionBar);

		HorizontalLayout buttonsPanel = new HorizontalLayout();
		buttonsPanel.setMargin(false);
		buttonsPanel.setSpacing(true);
		buttonsPanel.setWidth(100, Unit.PERCENTAGE);

		Button cancelButton = ButtonHelper.createButton(Captions.actionDiscard, event -> closeWindow());

		buttonsPanel.addComponent(cancelButton);
		buttonsPanel.setComponentAlignment(cancelButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(cancelButton, 1);

		Button saveButton =
			ButtonHelper.createButton(Captions.actionSave, event -> saveCallback.accept(getContactLineDtos()), ValoTheme.BUTTON_PRIMARY);

		buttonsPanel.addComponent(saveButton);
		buttonsPanel.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(saveButton, 0);

		addComponent(buttonsPanel);
		setComponentAlignment(buttonsPanel, Alignment.BOTTOM_RIGHT);
	}

	private void updateDistricts(RegionReferenceDto regionDto) {
		FieldHelper.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
	}

	public void closeWindow() {
		window.close();
	}

	public void validate() throws ValidationRuntimeException {
		boolean validationFailed = false;
		for (ContactLineLayout line : contactLines) {
			if (line.hasErrors()) {
				validationFailed = true;
			}
		}
		if (validationFailed) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.errorFieldValidationFailed));
		}
	}

	public List<ContactLineDto> getContactLineDtos() {
		return contactLines.stream().map(ContactLineLayout::getBean).collect(Collectors.toList());
	}

	public void setSaveCallback(Consumer<List<ContactLineDto>> saveCallback) {
		this.saveCallback = saveCallback;
	}

	private ContactLineLayout buildNewLine(VerticalLayout lineComponent) {
		ContactLineLayout newLine = new ContactLineLayout(contactLines.size());
		ContactLineDto newLineDto = new ContactLineDto();

		if (!contactLines.isEmpty()) {
			ContactLineDto lastLineDto = contactLines.get(contactLines.size() - 1).getBean();
			newLineDto.setCaze(lastLineDto.getCaze());
			newLineDto.setDisease(lastLineDto.getDisease());
			newLineDto.setRegion(lastLineDto.getRegion());
			newLineDto.setDistrict(lastLineDto.getDistrict());
			newLineDto.setLineField(lastLineDto.getLineField());
		} else {
			newLine.enableDelete(false);
		}

		newLine.setBean(newLineDto);
		newLine.addDeleteLineListener(e -> {
			ContactLineLayout selectedLine = (ContactLineLayout) e.getComponent();
			lineComponent.removeComponent(selectedLine);
			contactLines.remove(selectedLine);
			contactLines.get(0).enableDelete(contactLines.size() > 1);
		});

		return newLine;
	}

	class ContactLineLayout extends LineLayout {

		private final Binder<ContactLineDto> binder = new Binder<>(ContactLineDto.class);

		private final ContactLineField contactLineField;
		private final Button delete;

		public ContactLineLayout(int lineIndex) {

			addStyleName(CssStyles.SPACING_SMALL);
			setMargin(false);

			binder.forField(caseSelector).bind(ContactLineDto.CAZE);
			binder.forField(disease).asRequired().bind(ContactLineDto.DISEASE);
			binder.forField(region).asRequired().bind(ContactLineDto.REGION);
			binder.forField(district).asRequired().bind(ContactLineDto.DISTRICT);

			contactLineField = new ContactLineField();
			contactLineField.setId("lineListingContactLineField_" + lineIndex);
			binder.forField(contactLineField).bind(ContactLineDto.LINE_FIELD);

			delete = ButtonHelper
				.createIconButtonWithCaption("delete_" + lineIndex, null, VaadinIcons.TRASH, event -> fireEvent(new DeleteLineEvent(this)));
			delete.setStyleName(CssStyles.VSPACE_3);

			addComponents(contactLineField, delete);

			setComponentAlignment(contactLineField, Alignment.BOTTOM_LEFT);
			setComponentAlignment(delete, Alignment.BOTTOM_LEFT);

			contactLineField.showCaptions();
		}

		public void setBean(ContactLineDto bean) {
			binder.setBean(bean);
		}

		public ContactLineDto getBean() {
			return binder.getBean();
		}

		public boolean hasErrors() {
			return contactLineField.hasErrors();
		}

		public void enableDelete(boolean shouldEnable) {
			delete.setEnabled(shouldEnable);
		}
	}

	public static class ContactLineDto implements Serializable {

		public static final String CAZE = "caze";
		public static final String DISEASE = "disease";
		public static final String REGION = "region";
		public static final String DISTRICT = "district";
		public static final String LINE_FIELD = "lineField";

		private CaseReferenceDto caze;
		private Disease disease;
		private RegionReferenceDto region;
		private DistrictReferenceDto district;
		private ContactFieldDto lineField;

		public CaseReferenceDto getCaze() {
			return caze;
		}

		public void setCaze(CaseReferenceDto caze) {
			this.caze = caze;
		}

		public Disease getDisease() {
			return disease;
		}

		public void setDisease(Disease disease) {
			this.disease = disease;
		}

		public RegionReferenceDto getRegion() {
			return region;
		}

		public void setRegion(RegionReferenceDto region) {
			this.region = region;
		}

		public DistrictReferenceDto getDistrict() {
			return district;
		}

		public void setDistrict(DistrictReferenceDto district) {
			this.district = district;
		}

		public ContactFieldDto getLineField() {
			return lineField;
		}

		public void setLineField(ContactFieldDto lineField) {
			this.lineField = lineField;
		}
	}
}
