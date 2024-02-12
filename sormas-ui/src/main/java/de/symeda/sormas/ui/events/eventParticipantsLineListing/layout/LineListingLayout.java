package de.symeda.sormas.ui.events.eventParticipantsLineListing.layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.components.linelisting.line.DeleteLineEvent;
import de.symeda.sormas.ui.utils.components.linelisting.line.LineLayout;
import de.symeda.sormas.ui.utils.components.linelisting.model.LineDto;
import de.symeda.sormas.ui.utils.components.linelisting.person.PersonField;
import de.symeda.sormas.ui.utils.components.linelisting.person.PersonFieldDto;
import de.symeda.sormas.ui.utils.components.linelisting.section.LineListingSection;

public class LineListingLayout extends VerticalLayout {

	public static final float DEFAULT_WIDTH = 1024;

	private final ComboBox<RegionReferenceDto> region;
	private final ComboBox<DistrictReferenceDto> district;

	private final EventDto eventDto;
	private final List<EventParticipantLineLayout> eventParticipantLines;
	private final Window window;
	private Consumer<LinkedList<LineDto<EventParticipantDto>>> saveCallback;

	public LineListingLayout(Window window, EventDto eventDto) {
		this.window = window;
		this.eventDto = eventDto;

		setSpacing(false);

		LineListingSection sharedInformationComponent = new LineListingSection(Captions.lineListingSharedInformation);

		HorizontalLayout sharedInformationBar = new HorizontalLayout();
		sharedInformationBar.addStyleName(CssStyles.SPACING_SMALL);

		region = new ComboBox<>(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.RESPONSIBLE_REGION));
		region.setItemCaptionGenerator(item -> item.buildCaption());
		region.setId("lineListingRegion");
		sharedInformationBar.addComponent(region);

		region.addValueChangeListener(e -> {
			updateDistricts(e.getValue());
		});

		district = new ComboBox<>(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.RESPONSIBLE_DISTRICT));
		district.setItemCaptionGenerator(item -> item.buildCaption());
		district.setId("lineListingDistrict");
		sharedInformationBar.addComponent(district);

		sharedInformationComponent.addComponent(sharedInformationBar);
		addComponent(sharedInformationComponent);

		LineListingSection lineComponent = new LineListingSection(Captions.lineListingNewEventParticipantsList);

		eventParticipantLines = new ArrayList<>();

		EventParticipantLineLayout line = buildNewLine(lineComponent);
		eventParticipantLines.add(line);
		lineComponent.addComponent(line);
		lineComponent.setSpacing(false);
		addComponent(lineComponent);

		UserProvider currentUserProvider = UserProvider.getCurrent();

		if (currentUserProvider != null && currentUserProvider.hasRegionJurisdictionLevel()) {
			RegionReferenceDto userRegion = currentUserProvider.getUser().getRegion();
			region.setValue(userRegion);
			region.setVisible(false);
			updateDistricts(userRegion);
		} else {
			region.setItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		}

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			region.setValue(FacadeProvider.getRegionFacade().getDefaultInfrastructureReference());
			district.setValue(FacadeProvider.getDistrictFacade().getDefaultInfrastructureReference());
			sharedInformationComponent.setVisible(false);
		}

		HorizontalLayout actionBar = new HorizontalLayout();
		Button addLine = ButtonHelper.createIconButton(Captions.lineListingAddLine, VaadinIcons.PLUS, e -> {
			EventParticipantLineLayout newLine = buildNewLine(lineComponent);
			eventParticipantLines.add(newLine);
			lineComponent.addComponent(newLine);

			if (eventParticipantLines.size() > 1) {
				eventParticipantLines.get(0).getDelete().setEnabled(true);
			}
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
			ButtonHelper.createButton(Captions.actionSave, event -> saveCallback.accept(getEventParticipantLineDtos()), ValoTheme.BUTTON_PRIMARY);

		buttonsPanel.addComponent(saveButton);
		buttonsPanel.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(saveButton, 0);

		addComponent(buttonsPanel);
		setComponentAlignment(buttonsPanel, Alignment.BOTTOM_RIGHT);
	}

	private LinkedList<LineDto<EventParticipantDto>> getEventParticipantLineDtos() {
		return eventParticipantLines.stream().map(line -> {
			EventParticipantLineDto eventParticipantLineDto = line.getBean();
			LineDto<EventParticipantDto> result = new LineDto<>();

			final EventParticipantDto eventParticipant =
				EventParticipantDto.build(eventDto.toReference(), UserProvider.getCurrent().getUserReference());
			eventParticipant.setInvolvementDescription(eventParticipantLineDto.getInvolvementDescription());

			if (eventDto.getEventLocation() == null
				|| eventDto.getEventLocation().getDistrict() == null
				|| (district.getValue() != null && !eventDto.getEventLocation().getDistrict().getUuid().equals(district.getValue().getUuid()))) {
				eventParticipant.setRegion(eventParticipantLineDto.region);
				eventParticipant.setDistrict(eventParticipantLineDto.district);
			}

			final PersonDto person = PersonDto.build();
			person.setFirstName(eventParticipantLineDto.getPerson().getFirstName());
			person.setLastName(eventParticipantLineDto.getPerson().getLastName());
			if (eventParticipantLineDto.getPerson().getBirthDate() != null) {
				person.setBirthdateYYYY(eventParticipantLineDto.getPerson().getBirthDate().getDateOfBirthYYYY());
				person.setBirthdateMM(eventParticipantLineDto.getPerson().getBirthDate().getDateOfBirthMM());
				person.setBirthdateDD(eventParticipantLineDto.getPerson().getBirthDate().getDateOfBirthDD());
			}
			person.setSex(eventParticipantLineDto.getPerson().getSex());
			result.setPerson(person);
			eventParticipant.setPerson(person);
			eventParticipant.setEvent(this.eventDto.toReference());
			result.setEntity(eventParticipant);

			return result;

		}).collect(Collectors.toCollection(LinkedList::new));

	}

	public void closeWindow() {
		window.close();
	}

	public void validate() throws ValidationRuntimeException {
		boolean validationFailed = false;
		for (EventParticipantLineLayout line : eventParticipantLines) {
			if (line.hasErrors()) {
				validationFailed = true;
			}
		}
		if (validationFailed) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.errorFieldValidationFailed));
		}
	}

	public void setSaveCallback(Consumer<LinkedList<LineDto<EventParticipantDto>>> saveCallback) {
		this.saveCallback = saveCallback;
	}

	private void updateDistricts(RegionReferenceDto regionDto) {
		FieldHelper.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
	}

	private EventParticipantLineLayout buildNewLine(VerticalLayout lineComponent) {
		EventParticipantLineLayout newLine = new EventParticipantLineLayout(eventParticipantLines.size());
		DistrictReferenceDto districtReferenceDto = district.getValue();
		EventParticipantLineDto newLineDto = new EventParticipantLineDto();
		if (region.getValue() != null) {
			newLineDto.setRegion(region.getValue());
		}
		if (district.getValue() != null) {
			newLineDto.setDistrict(district.getValue());
		}

		newLine.setBean(newLineDto);
		newLine.addDeleteLineListener(e -> {
			EventParticipantLineLayout selectedLine = (EventParticipantLineLayout) e.getComponent();
			lineComponent.removeComponent(selectedLine);
			eventParticipantLines.remove(selectedLine);
			eventParticipantLines.get(0).formatAsFirstLine();
			if (eventParticipantLines.size() > 1) {
				eventParticipantLines.get(0).getDelete().setEnabled(true);
			}
		});

		return newLine;
	}

	class EventParticipantLineLayout extends LineLayout {

		private final Binder<EventParticipantLineDto> binder = new Binder<>(EventParticipantLineDto.class);
		private final PersonField person;
		private TextField involvementDescription;

		private final Button delete;

		public EventParticipantLineLayout(int lineIndex) {

			addStyleName(CssStyles.SPACING_SMALL);
			setMargin(false);

			if (eventDto.getEventLocation().getDistrict() == null) {
				binder.forField(region).asRequired().bind(EventParticipantLineDto.REGION);
				binder.forField(district).asRequired().bind(EventParticipantLineDto.DISTRICT);
			} else {
				binder.forField(region).bind(EventParticipantLineDto.REGION);
				binder.forField(district).bind(EventParticipantLineDto.DISTRICT);
			}

			involvementDescription = new TextField();
			involvementDescription.setId("lineListingInvolvementDescription_" + lineIndex);
			involvementDescription.setWidth(160, Unit.PIXELS);
			binder.forField(involvementDescription).bind(EventParticipantLineDto.INVOLVEMENT_DESCRIPTION);

			person = new PersonField();
			person.setId("lineListingPerson_" + lineIndex);
			binder.forField(person).bind(EventParticipantLineDto.PERSON);

			delete = ButtonHelper
				.createIconButtonWithCaption("delete_" + lineIndex, null, VaadinIcons.TRASH, event -> fireEvent(new DeleteLineEvent(this)));

			addComponents(involvementDescription, person, delete);

			if (lineIndex == 0) {
				formatAsFirstLine();
			} else {
				formatAsOtherLine();
			}
		}

		public EventParticipantLineDto getBean() {
			return binder.getBean();
		}

		public Button getDelete() {
			return delete;
		}

		public void setBean(EventParticipantLineDto bean) {
			binder.setBean(bean);
		}

		public boolean hasErrors() {
			BinderValidationStatus<PersonFieldDto> personValidationStatus = person.validate();
			BinderValidationStatus<EventParticipantLineDto> lineValidationStatus = binder.validate();
			return personValidationStatus.hasErrors() || lineValidationStatus.hasErrors();
		}

		private void formatAsFirstLine() {
			formatAsOtherLine();

			involvementDescription
				.setCaption(I18nProperties.getPrefixCaption(EventParticipantDto.I18N_PREFIX, EventParticipantDto.INVOLVEMENT_DESCRIPTION));
			person.showCaptions();
			delete.setEnabled(false);
			setComponentAlignment(delete, Alignment.MIDDLE_LEFT);
		}

		private void formatAsOtherLine() {
			person.hideCaptions();
		}
	}

	public static class EventParticipantLineDto implements Serializable {

		public static final String REGION = "region";
		public static final String DISTRICT = "district";
		public static final String PERSON = "person";
		public static final String INVOLVEMENT_DESCRIPTION = "involvementDescription";

		private RegionReferenceDto region;
		private DistrictReferenceDto district;
		private PersonFieldDto person;
		private String involvementDescription;

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

		public PersonFieldDto getPerson() {
			return person;
		}

		public void setPerson(PersonFieldDto person) {
			this.person = person;
		}

		public String getInvolvementDescription() {
			return involvementDescription;
		}

		public void setInvolvementDescription(String involvementDescription) {
			this.involvementDescription = involvementDescription;
		}
	}

	public EventDto getEventDto() {
		return eventDto;
	}

	public RegionReferenceDto getRegion() {
		return region.getValue();
	}

	public DistrictReferenceDto getDistrict() {
		return district.getValue();
	}
}
