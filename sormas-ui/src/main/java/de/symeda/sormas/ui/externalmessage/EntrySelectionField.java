package de.symeda.sormas.ui.externalmessage;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.caze.components.caseselection.CaseSelectionGrid;
import de.symeda.sormas.ui.contact.ContactSelectionGrid;
import de.symeda.sormas.ui.events.EventParticipantSelectionGrid;
import de.symeda.sormas.ui.externalmessage.labmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EntrySelectionField extends CustomField<PickOrCreateEntryResult> {

	private static final Object CREATE_CASE = "createCase";
	private static final Object CREATE_CONTACT = "createContact";
	private static final Object CREATE_EVENT_PARTICIPANT = "createEventParticipant";
	private static final Object SELECT_CASE = "selectCase";
	private static final Object SELECT_CONTACT = "selectContact";
	private static final Object SELECT_EVENT_PARTICIPANT = "selectEventParticipant";
	private final ExternalMessageDto externalMessageDto;
	private final List<CaseSelectionDto> cases;
	private final List<SimilarContactDto> contacts;
	private final List<SimilarEventParticipantDto> eventParticipants;
	private VerticalLayout mainLayout;
	private RadioButtonGroup<Object> rbSelectCase;
	private RadioButtonGroup<Object> rbSelectContact;
	private RadioButtonGroup<Object> rbSelectEventParticipant;
	private RadioButtonGroup<Object> rbCreateEntity;
	private Consumer<Boolean> selectionChangeCallback;
	protected CaseSelectionGrid caseGrid;
	protected ContactSelectionGrid contactGrid;
	protected EventParticipantSelectionGrid eventParticipantGrid;

	public EntrySelectionField(
		ExternalMessageDto externalMessageDto,
		List<CaseSelectionDto> cases,
		List<SimilarContactDto> contacts,
		List<SimilarEventParticipantDto> eventParticipants) {

		this.externalMessageDto = externalMessageDto;
		this.cases = cases;
		this.contacts = contacts;
		this.eventParticipants = eventParticipants;
	}

	@Override
	protected Component initContent() {
		// Main layout
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(false);
		mainLayout.setSizeUndefined();

		addInfoComponent();
		addLabMessageComponent();
		if (cases != null && !cases.isEmpty()) {
			addSelectCaseRadioGroup();
			addCaseGrid();
		}
		if (contacts != null && !contacts.isEmpty()) {
			addSelectContactRadioGroup();
			addContactGrid();
		}
		if (eventParticipants != null && !eventParticipants.isEmpty()) {
			addSelectEventParticipantRadioGroup();
			addEventParticipantGrid();
		}
		addCreateEntityRadioGroup();

		return mainLayout;
	}

	private void addCreateEntityRadioGroup() {
		rbCreateEntity = new RadioButtonGroup<>();
		rbCreateEntity.setItems(CREATE_CASE, CREATE_CONTACT, CREATE_EVENT_PARTICIPANT);
		rbCreateEntity.setItemCaptionGenerator((item) -> {
			if (item == CREATE_CASE) {
				return I18nProperties.getCaption(Captions.caseCreateNew);
			} else if (item == CREATE_CONTACT) {
				return I18nProperties.getCaption(Captions.contactCreateNew);
			} else if (item == CREATE_EVENT_PARTICIPANT) {
				return I18nProperties.getCaption(Captions.eventParticipantCreateNew);
			}
			throw new IllegalArgumentException((String) item);
		});
		rbCreateEntity.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				if (rbSelectCase != null) {
					rbSelectCase.setValue(null);
				}
				if (rbSelectContact != null) {
					rbSelectContact.setValue(null);
				}
				if (rbSelectEventParticipant != null) {
					rbSelectEventParticipant.setValue(null);
				}
				if (caseGrid != null) {
					caseGrid.deselectAll();
					caseGrid.setEnabled(false);
				}
				if (contactGrid != null) {
					contactGrid.deselectAll();
					contactGrid.setEnabled(false);
				}
				if (eventParticipantGrid != null) {
					eventParticipantGrid.deselectAll();
					eventParticipantGrid.setEnabled(false);
				}
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});
		CssStyles.style(rbCreateEntity, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);

		mainLayout.addComponent(rbCreateEntity);
	}

	private void addEventParticipantGrid() {
		eventParticipantGrid = new EventParticipantSelectionGrid(eventParticipants);
		eventParticipantGrid.addSelectionListener(e -> {
			if (e.getSelected().size() > 0) {
				rbCreateEntity.setValue(null);
				if (rbSelectCase != null) {
					rbSelectCase.setValue(null);
				}
				if (rbSelectContact != null) {
					rbSelectContact.setValue(null);
				}
			}
			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getSelected().isEmpty());
			}
		});
		eventParticipantGrid.setEnabled(false);
		mainLayout.addComponent(eventParticipantGrid);
	}

	private void addSelectEventParticipantRadioGroup() {
		rbSelectEventParticipant = new RadioButtonGroup<>();
		rbSelectEventParticipant.setItems(SELECT_EVENT_PARTICIPANT);
		rbSelectEventParticipant.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.eventParticipantSelect));
		CssStyles.style(rbSelectEventParticipant, CssStyles.VSPACE_NONE);
		rbSelectEventParticipant.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				rbCreateEntity.setValue(null);
				if (rbSelectCase != null) {
					rbSelectCase.setValue(null);
				}
				if (rbSelectContact != null) {
					rbSelectContact.setValue(null);
				}
				eventParticipantGrid.setEnabled(true);
				if (caseGrid != null) {
					caseGrid.setEnabled(false);
				}
				if (contactGrid != null) {
					contactGrid.setEnabled(false);
				}
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(eventParticipantGrid.getSelectedRow() != null);
				}
			}
		});

		mainLayout.addComponent(rbSelectEventParticipant);
	}

	private void addContactGrid() {
		contactGrid = new ContactSelectionGrid(contacts);
		contactGrid.addSelectionListener(e -> {
			if (e.getSelected().size() > 0) {
				rbCreateEntity.setValue(null);
				if (rbSelectCase != null) {
					rbSelectCase.setValue(null);
				}
				if (rbSelectEventParticipant != null) {
					rbSelectEventParticipant.setValue(null);
				}
			}
			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getSelected().isEmpty());
			}
		});
		contactGrid.setEnabled(false);
		mainLayout.addComponent(contactGrid);
	}

	private void addSelectContactRadioGroup() {
		rbSelectContact = new RadioButtonGroup<>();
		rbSelectContact.setItems(SELECT_CONTACT);
		rbSelectContact.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.contactSelect));
		CssStyles.style(rbSelectContact, CssStyles.VSPACE_NONE);
		rbSelectContact.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				rbCreateEntity.setValue(null);
				if (rbSelectCase != null) {
					rbSelectCase.setValue(null);
				}
				if (rbSelectEventParticipant != null) {
					rbSelectEventParticipant.setValue(null);
				}
				contactGrid.setEnabled(true);
				if (caseGrid != null) {
					caseGrid.setEnabled(false);
				}
				if (eventParticipantGrid != null) {
					eventParticipantGrid.setEnabled(false);
				}
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(contactGrid.getSelectedRow() != null);
				}
			}
		});

		mainLayout.addComponent(rbSelectContact);
	}

	private void addCaseGrid() {
		caseGrid = new CaseSelectionGrid(cases);
		caseGrid.addSelectionListener(e -> {
			if (e.getSelected().size() > 0) {
				rbCreateEntity.setValue(null);
				if (rbSelectContact != null) {
					rbSelectContact.setValue(null);
				}
				if (rbSelectEventParticipant != null) {
					rbSelectEventParticipant.setValue(null);
				}
			}
			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getSelected().isEmpty());
			}
		});
		caseGrid.setEnabled(false);
		mainLayout.addComponent(caseGrid);
	}

	private void addSelectCaseRadioGroup() {
		rbSelectCase = new RadioButtonGroup<>();
		rbSelectCase.setItems(SELECT_CASE);
		rbSelectCase.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.caseSelect));
		CssStyles.style(rbSelectCase, CssStyles.VSPACE_NONE);
		rbSelectCase.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				rbCreateEntity.setValue(null);
				if (rbSelectContact != null) {
					rbSelectContact.setValue(null);
				}
				if (rbSelectEventParticipant != null) {
					rbSelectEventParticipant.setValue(null);
				}
				caseGrid.setEnabled(true);
				if (contactGrid != null) {
					contactGrid.setEnabled(false);
				}
				if (eventParticipantGrid != null) {
					eventParticipantGrid.setEnabled(false);
				}
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(caseGrid.getSelectedRow() != null);
				}
			}
		});

		mainLayout.addComponent(rbSelectCase);
	}

	private void addLabMessageComponent() {
		HorizontalLayout labMessageLayout = new HorizontalLayout();
		labMessageLayout.setSpacing(true);

		createAndAddLabel(externalMessageDto.getMessageDateTime(), ExternalMessageDto.MESSAGE_DATE_TIME, labMessageLayout);
		createAndAddLabel(externalMessageDto.getSampleDateTime(), ExternalMessageDto.SAMPLE_DATE_TIME, labMessageLayout);
		createAndAddLabel(externalMessageDto.getPersonFirstName(), ExternalMessageDto.PERSON_FIRST_NAME, labMessageLayout);
		createAndAddLabel(externalMessageDto.getPersonLastName(), ExternalMessageDto.PERSON_LAST_NAME, labMessageLayout);
		createAndAddLabel(externalMessageDto.getPersonBirthDateDD(), ExternalMessageDto.PERSON_BIRTH_DATE_DD, labMessageLayout);
		createAndAddLabel(externalMessageDto.getPersonBirthDateMM(), ExternalMessageDto.PERSON_BIRTH_DATE_MM, labMessageLayout);
		createAndAddLabel(externalMessageDto.getPersonBirthDateYYYY(), ExternalMessageDto.PERSON_BIRTH_DATE_YYYY, labMessageLayout);
		createAndAddLabel(externalMessageDto.getPersonSex(), ExternalMessageDto.PERSON_SEX, labMessageLayout);

		mainLayout.addComponent(labMessageLayout);
	}

	private void createAndAddLabel(Object value, String property, HorizontalLayout layout) {
		Label label = new Label();
		if (value != null) {
			label.setValue(value.toString());
		}
		label.setCaption(I18nProperties.getPrefixCaption(ExternalMessageDto.I18N_PREFIX, property));
		label.setWidthUndefined();
		layout.addComponent(label);
	}

	private void addInfoComponent() {
		if (cases != null && !cases.isEmpty()
			|| contacts != null && !contacts.isEmpty()
			|| eventParticipants != null && !eventParticipants.isEmpty()) {
			mainLayout.addComponent(VaadinUiUtil.createInfoComponent(I18nProperties.getString(Strings.infoSelectOrCreateEntry)));
		} else {
			mainLayout.addComponent(VaadinUiUtil.createInfoComponent(I18nProperties.getString(Strings.infoCreateEntry)));
		}
	}

	@Override
	protected void doSetValue(PickOrCreateEntryResult pickOrCreateEntryResult) {
		if (pickOrCreateEntryResult == null) {
			throw new IllegalArgumentException();
		}

		if (pickOrCreateEntryResult.getCaze() != null) {
			rbSelectCase.setValue(SELECT_CASE);
			caseGrid.select(pickOrCreateEntryResult.getCaze());
		} else if (pickOrCreateEntryResult.getContact() != null) {
			rbSelectContact.setValue(SELECT_CONTACT);
			contactGrid.select(pickOrCreateEntryResult.getContact());
		} else if (pickOrCreateEntryResult.getEventParticipant() != null) {
			rbSelectEventParticipant.setValue(SELECT_EVENT_PARTICIPANT);
			eventParticipantGrid.select(pickOrCreateEntryResult.getEventParticipant());
		}
	}

	@Override
	public PickOrCreateEntryResult getValue() {
		if (caseGrid != null && rbSelectCase.getValue() != null) {
			PickOrCreateEntryResult value = new PickOrCreateEntryResult();
			value.setCaze((CaseSelectionDto) caseGrid.getSelectedRow());
			return value;
		} else if (contactGrid != null && rbSelectContact.getValue() != null) {
			PickOrCreateEntryResult value = new PickOrCreateEntryResult();
			value.setContact((SimilarContactDto) contactGrid.getSelectedRow());
			return value;
		} else if (eventParticipantGrid != null && rbSelectEventParticipant.getValue() != null) {
			PickOrCreateEntryResult value = new PickOrCreateEntryResult();
			value.setEventParticipant((SimilarEventParticipantDto) eventParticipantGrid.getSelectedRow());
			return value;
		} else if (CREATE_CASE.equals(rbCreateEntity.getValue())) {
			PickOrCreateEntryResult value = new PickOrCreateEntryResult();
			value.setNewCase(true);
			return value;
		} else if (CREATE_CONTACT.equals(rbCreateEntity.getValue())) {
			PickOrCreateEntryResult value = new PickOrCreateEntryResult();
			value.setNewContact(true);
			return value;
		} else if (CREATE_EVENT_PARTICIPANT.equals(rbCreateEntity.getValue())) {
			PickOrCreateEntryResult value = new PickOrCreateEntryResult();
			value.setNewEventParticipant(true);
			return value;
		}
		return null;
	}

	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}
}
