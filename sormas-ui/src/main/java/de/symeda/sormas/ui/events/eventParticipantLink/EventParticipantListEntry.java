package de.symeda.sormas.ui.events.eventParticipantLink;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantListEntryDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class EventParticipantListEntry extends HorizontalLayout {

	public static final String SEPARATOR = ": ";
	private final EventParticipantListEntryDto eventParticipantListEntryDto;
	private Button editButton;

	public EventParticipantListEntry(EventParticipantListEntryDto eventParticipantListEntryDto) {
		this.eventParticipantListEntryDto = eventParticipantListEntryDto;
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setMargin(false);
		mainLayout.setSpacing(false);
		addComponent(mainLayout);
		setExpandRatio(mainLayout, 1);

		HorizontalLayout idLayout = new HorizontalLayout();
		Label eventParticipantUuidLabel = new Label(
			I18nProperties.getPrefixCaption(EventParticipantDto.I18N_PREFIX, EventParticipantDto.UUID) + SEPARATOR
				+ DataHelper.toStringNullable(DataHelper.getShortUuid(eventParticipantListEntryDto.getUuid())));
		eventParticipantUuidLabel.setDescription(eventParticipantListEntryDto.getUuid());
		Label eventUuidLabel = new Label(
			I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.UUID) + SEPARATOR
				+ DataHelper.toStringNullable(DataHelper.getShortUuid(eventParticipantListEntryDto.getEventUuid())));
		eventUuidLabel.setDescription(eventParticipantListEntryDto.getEventUuid());

		idLayout.addComponent(eventParticipantUuidLabel);
		idLayout.addComponent(eventUuidLabel);
		idLayout.setWidthFull();
		idLayout.setComponentAlignment(eventParticipantUuidLabel, Alignment.MIDDLE_LEFT);
		idLayout.setComponentAlignment(eventUuidLabel, Alignment.MIDDLE_RIGHT);
		mainLayout.addComponent(idLayout);

		HorizontalLayout diseaseStatusLayout = new HorizontalLayout();
		Label diseaseLabel = new Label(eventParticipantListEntryDto.getDisease().toString());
		diseaseLabel.addStyleNames(CssStyles.LABEL_BOLD);
		diseaseLabel.setDescription(eventParticipantListEntryDto.getDisease().toString());
		diseaseStatusLayout.addComponent(diseaseLabel);

		Label statusLabel =
			new Label(eventParticipantListEntryDto.getEventStatus().toString());
		statusLabel.addStyleNames(CssStyles.LABEL_BOLD);
		statusLabel.setDescription(eventParticipantListEntryDto.getEventStatus().toString());
		diseaseStatusLayout.addComponent(statusLabel);

		diseaseStatusLayout.addComponent(diseaseLabel);
		diseaseStatusLayout.addComponent(statusLabel);
		diseaseStatusLayout.setWidthFull();
		diseaseStatusLayout.setComponentAlignment(diseaseLabel, Alignment.MIDDLE_LEFT);
		diseaseStatusLayout.setComponentAlignment(statusLabel, Alignment.MIDDLE_RIGHT);
		mainLayout.addComponent(diseaseStatusLayout);

		Label titleLabel = new Label(eventParticipantListEntryDto.getEventTitle());
		titleLabel.setDescription(eventParticipantListEntryDto.getEventTitle());
		mainLayout.addComponent(titleLabel);

		mainLayout.addComponent(titleLabel);
	}

	public void addEditListener(int rowIndex, Button.ClickListener editClickListener) {
		if (editButton == null) {
			editButton = ButtonHelper.createIconButtonWithCaption(
				"edit-participant-" + rowIndex,
				null,
				VaadinIcons.PENCIL,
				null,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);

			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(editButton, 0);
		}

		editButton.addClickListener(editClickListener);
	}

	public EventParticipantListEntryDto getEventParticipantListEntryDto() {
		return eventParticipantListEntryDto;
	}
}
