/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.externalmessage.processing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.data.provider.Query;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.components.caseselection.CaseSelectionGrid;
import de.symeda.sormas.ui.contact.ContactSelectionGrid;
import de.symeda.sormas.ui.events.EventParticipantSelectionGrid;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EntrySelectionField extends CustomField<PickOrCreateEntryResult> {

	private static final long serialVersionUID = 5315286409460459687L;

	private VerticalLayout mainLayout;
	private final ExternalMessageDto externalMessageDto;
	private final Options selectableOptions;
	private RadioButtonGroup<OptionType> rbSelectCase;
	private RadioButtonGroup<OptionType> rbSelectContact;
	private Consumer<Boolean> selectionChangeCallback;
	protected CaseSelectionGrid caseGrid;
	protected ContactSelectionGrid contactGrid;
	protected EventParticipantSelectionGrid eventParticipantGrid;
	private RadioButtonGroup<OptionType> rbSelectEventParticipant;
	private RadioButtonGroup<OptionType> rbCreateEntity;

	public EntrySelectionField(ExternalMessageDto externalMessageDto, Options selectableOptions) {

		this.externalMessageDto = externalMessageDto;
		this.selectableOptions = selectableOptions;
	}

	private static void createAndAddLabel(Object value, String property, HorizontalLayout layout) {
		Label label = new Label();
		if (value != null) {
			label.setValue(value.toString());
		}
		label.setCaption(I18nProperties.getPrefixCaption(ExternalMessageDto.I18N_PREFIX, property));
		label.setWidthUndefined();
		layout.addComponent(label);
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

		for (Option<?> option : selectableOptions.selectableOptions) {
			switch (option.type) {
			case SELECT_CASE: {
				if (CollectionUtils.isNotEmpty(option.selectableItems)) {
					addSelectCaseRadioGroup();
					addCaseGrid((List<CaseSelectionDto>) option.selectableItems);
				}
				break;
			}
			case SELECT_CONTACT: {
				if (CollectionUtils.isNotEmpty(option.selectableItems)) {
					addSelectContactRadioGroup();
					addContactGrid((List<SimilarContactDto>) option.selectableItems);
				}
				break;
			}
			case SELECT_EVENT_PARTICIPANT: {
				if (CollectionUtils.isNotEmpty(option.selectableItems)) {
					addSelectEventParticipantRadioGroup();
					addEventParticipantGrid((List<SimilarEventParticipantDto>) option.selectableItems);
				}
				break;
			}
			default:
				addCreateEntityRadio(option.type);
			}

		}

		return mainLayout;
	}

	private void addEventParticipantGrid(List<SimilarEventParticipantDto> eventParticipants) {
		eventParticipantGrid = new EventParticipantSelectionGrid(eventParticipants);
		eventParticipantGrid.addSelectionListener(e -> {
			if (!e.getSelected().isEmpty()) {
				onRadioSelected(rbSelectEventParticipant);
			}

			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getSelected().isEmpty());
			}
		});
		eventParticipantGrid.setEnabled(false);
		mainLayout.addComponent(eventParticipantGrid);
	}

	private void addCreateEntityRadio(OptionType creationOptionType) {
		if (rbCreateEntity == null) {
			rbCreateEntity = new RadioButtonGroup<>();
			rbCreateEntity.setItemCaptionGenerator((item) -> {
				if (item == OptionType.CREATE_CASE) {
					return I18nProperties.getCaption(Captions.caseCreateNew);
				} else if (item == OptionType.CREATE_CONTACT) {
					return I18nProperties.getCaption(Captions.contactCreateNew);
				} else if (item == OptionType.CREATE_EVENT_PARTICIPANT) {
					return I18nProperties.getCaption(Captions.eventParticipantCreateNew);
				}
				throw new IllegalArgumentException(item.name());
			});

			rbCreateEntity.addValueChangeListener(e -> {
				if (e.getValue() != null) {
					onRadioSelected(rbCreateEntity);

					if (selectionChangeCallback != null) {
						selectionChangeCallback.accept(true);
					}
				}
			});
			CssStyles.style(rbCreateEntity, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);

			mainLayout.addComponent(rbCreateEntity);
		}

		rbCreateEntity.setItems(
			Stream.concat(rbCreateEntity.getDataProvider().fetch(new Query<>()), Stream.of(creationOptionType)).collect(Collectors.toList()));
	}

	private void onRadioSelected(RadioButtonGroup<OptionType> radio) {
		Stream.of(rbSelectCase, rbSelectContact, rbSelectEventParticipant, rbCreateEntity)
			.filter(Objects::nonNull)
			.filter(rb -> !rb.equals(radio))
			.forEach(rb -> rb.setValue(null));
	}

	private void addContactGrid(List<SimilarContactDto> contacts) {
		contactGrid = new ContactSelectionGrid(contacts);
		contactGrid.addSelectionListener(e -> {
			if (!e.getSelected().isEmpty()) {
				onRadioSelected(rbSelectContact);
			}

			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getSelected().isEmpty());
			}
		});
		contactGrid.setEnabled(false);
		mainLayout.addComponent(contactGrid);
	}

	private void addCaseGrid(List<CaseSelectionDto> cases) {
		caseGrid = new CaseSelectionGrid(cases);
		caseGrid.addSelectionListener(e -> {
			if (!e.getSelected().isEmpty()) {
				onRadioSelected(rbSelectCase);
			}

			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getSelected().isEmpty());
			}
		});
		caseGrid.setEnabled(false);
		mainLayout.addComponent(caseGrid);
	}

	private void addSelectEventParticipantRadioGroup() {
		rbSelectEventParticipant = new RadioButtonGroup<>();
		rbSelectEventParticipant.setItems(OptionType.SELECT_EVENT_PARTICIPANT);
		rbSelectEventParticipant.setItemCaptionGenerator(item -> I18nProperties.getCaption(Captions.eventParticipantSelect));
		CssStyles.style(rbSelectEventParticipant, CssStyles.VSPACE_NONE);
		rbSelectEventParticipant.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				onRadioSelected(rbSelectEventParticipant);
				eventParticipantGrid.setEnabled(true);

				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(eventParticipantGrid.getSelectedRow() != null);
				}
			} else {
				eventParticipantGrid.deselectAll();
				eventParticipantGrid.setEnabled(false);
			}
		});

		mainLayout.addComponent(rbSelectEventParticipant);
	}

	private void addSelectContactRadioGroup() {
		rbSelectContact = new RadioButtonGroup<>();
		rbSelectContact.setItems(OptionType.SELECT_CONTACT);
		rbSelectContact.setItemCaptionGenerator(item -> I18nProperties.getCaption(Captions.contactSelect));
		CssStyles.style(rbSelectContact, CssStyles.VSPACE_NONE);
		rbSelectContact.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				onRadioSelected(rbSelectContact);
				contactGrid.setEnabled(true);

				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(contactGrid.getSelectedRow() != null);
				}
			} else {
				contactGrid.deselectAll();
				contactGrid.setEnabled(true);
			}
		});

		mainLayout.addComponent(rbSelectContact);
	}

	private void addLabMessageComponent() {
		HorizontalLayout labMessageLayout = new HorizontalLayout();
		labMessageLayout.setSpacing(true);

		List<SampleReportDto> sampleReports = externalMessageDto.getSampleReports();
		SampleReportDto sampleReport = sampleReports != null ? sampleReports.get(0) : null;

		createAndAddLabel(externalMessageDto.getMessageDateTime(), ExternalMessageDto.MESSAGE_DATE_TIME, labMessageLayout);
		if (sampleReport != null) {
			createAndAddLabel(sampleReport.getSampleDateTime(), SampleReportDto.SAMPLE_DATE_TIME, labMessageLayout);
		}
		createAndAddLabel(externalMessageDto.getPersonFirstName(), ExternalMessageDto.PERSON_FIRST_NAME, labMessageLayout);
		createAndAddLabel(externalMessageDto.getPersonLastName(), ExternalMessageDto.PERSON_LAST_NAME, labMessageLayout);
		createAndAddLabel(externalMessageDto.getPersonBirthDateDD(), ExternalMessageDto.PERSON_BIRTH_DATE_DD, labMessageLayout);
		createAndAddLabel(externalMessageDto.getPersonBirthDateMM(), ExternalMessageDto.PERSON_BIRTH_DATE_MM, labMessageLayout);
		createAndAddLabel(externalMessageDto.getPersonBirthDateYYYY(), ExternalMessageDto.PERSON_BIRTH_DATE_YYYY, labMessageLayout);
		createAndAddLabel(externalMessageDto.getPersonSex(), ExternalMessageDto.PERSON_SEX, labMessageLayout);

		mainLayout.addComponent(labMessageLayout);
	}

	private void addSelectCaseRadioGroup() {
		rbSelectCase = new RadioButtonGroup<>();
		rbSelectCase.setItems(OptionType.SELECT_CASE);
		rbSelectCase.setItemCaptionGenerator(item -> I18nProperties.getCaption(Captions.caseSelect));
		CssStyles.style(rbSelectCase, CssStyles.VSPACE_NONE);
		rbSelectCase.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				onRadioSelected(rbSelectCase);
				caseGrid.setEnabled(true);

				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(caseGrid.getSelectedRow() != null);
				}
			} else {
				caseGrid.deselectAll();
				caseGrid.setEnabled(false);
			}
		});

		mainLayout.addComponent(rbSelectCase);
	}

	private void addInfoComponent() {
		if (CollectionUtils.isNotEmpty(getSelectableItems(OptionType.SELECT_CASE))
			|| CollectionUtils.isNotEmpty(getSelectableItems(OptionType.SELECT_CONTACT))
			|| CollectionUtils.isNotEmpty(getSelectableItems(OptionType.SELECT_EVENT_PARTICIPANT))) {
			mainLayout.addComponent(VaadinUiUtil.createInfoComponent(I18nProperties.getString(Strings.infoSelectOrCreateEntry)));
		} else {
			mainLayout.addComponent(VaadinUiUtil.createInfoComponent(I18nProperties.getString(Strings.infoCreateEntry)));
		}
	}

	private List<?> getSelectableItems(OptionType optionType) {
		return selectableOptions.selectableOptions.stream()
			.filter(o -> o.type == optionType)
			.map(o -> o.selectableItems)
			.findFirst()
			.orElseGet(Collections::emptyList);
	}

	@Override
	protected void doSetValue(PickOrCreateEntryResult pickOrCreateEntryResult) {
		if (pickOrCreateEntryResult == null) {
			throw new IllegalArgumentException();
		}

		if (pickOrCreateEntryResult.getCaze() != null) {
			rbSelectCase.setValue(OptionType.SELECT_CASE);
			caseGrid.select(pickOrCreateEntryResult.getCaze());
		} else if (pickOrCreateEntryResult.getContact() != null) {
			rbSelectContact.setValue(OptionType.SELECT_CONTACT);
			contactGrid.select(pickOrCreateEntryResult.getContact());
		} else if (pickOrCreateEntryResult.getEventParticipant() != null) {
			rbSelectEventParticipant.setValue(OptionType.SELECT_EVENT_PARTICIPANT);
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
		} else if (OptionType.CREATE_CASE.equals(rbCreateEntity.getValue())) {
			PickOrCreateEntryResult value = new PickOrCreateEntryResult();
			value.setNewCase(true);
			return value;
		} else if (OptionType.CREATE_CONTACT.equals(rbCreateEntity.getValue())) {
			PickOrCreateEntryResult value = new PickOrCreateEntryResult();
			value.setNewContact(true);
			return value;
		} else if (OptionType.CREATE_EVENT_PARTICIPANT.equals(rbCreateEntity.getValue())) {
			PickOrCreateEntryResult value = new PickOrCreateEntryResult();
			value.setNewEventParticipant(true);
			return value;
		}
		return null;
	}

	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}

	public enum OptionType {
		CREATE_CASE,
		CREATE_CONTACT,
		CREATE_EVENT_PARTICIPANT,
		SELECT_CASE,
		SELECT_CONTACT,
		SELECT_EVENT_PARTICIPANT;
	}

	private static final class Option<T extends Serializable> implements Serializable {

		private static final long serialVersionUID = -3866850428669683952L;

		private final OptionType type;
		private final List<T> selectableItems;

		private Option(OptionType type, List<T> selectableItems) {
			this.type = type;
			this.selectableItems = selectableItems;
		}
	}

	public static final class Options implements Serializable {

		private static final long serialVersionUID = 5824346382436184226L;

		private final List<Option<?>> selectableOptions;

		private Options(List<Option<?>> selectableOptions) {
			this.selectableOptions = selectableOptions;
		}

		public static class Builder {

			private final List<Option<?>> options;

			public Builder() {
				options = new ArrayList<>();
			}

			public Builder addSelectCase(List<CaseSelectionDto> selectableCases) {
				if (!selectableCases.isEmpty()
					&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE)
					&& Objects.requireNonNull(UserProvider.getCurrent()).hasAllUserRights(UserRight.CASE_CREATE, UserRight.CASE_EDIT)) {
					return add(OptionType.SELECT_CASE, selectableCases);
				} else {
					return this;
				}
			}

			public Builder addSelectContact(List<SimilarContactDto> selectableContacts) {
				if (!selectableContacts.isEmpty()
					&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CONTACT_TRACING)
					&& Objects.requireNonNull(UserProvider.getCurrent()).hasAllUserRights(UserRight.CONTACT_CREATE, UserRight.CONTACT_EDIT)) {
					return add(OptionType.SELECT_CONTACT, selectableContacts);
				} else {
					return this;
				}
			}

			public Builder addSelectEventParticipant(List<SimilarEventParticipantDto> selectableEventParticipants) {
				if (!selectableEventParticipants.isEmpty()
					&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE)
					&& Objects.requireNonNull(UserProvider.getCurrent())
						.hasAllUserRights(UserRight.EVENTPARTICIPANT_CREATE, UserRight.EVENTPARTICIPANT_EDIT)) {
					return add(OptionType.SELECT_EVENT_PARTICIPANT, selectableEventParticipants);
				} else {
					return this;
				}
			}

			public Builder addCreateEntry(OptionType optionType) {
				return add(optionType, null);
			}

			public Builder addCreateEntry(OptionType optionType, FeatureType requiredFeatureType, UserRight... requiredUserRights) {
				if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(requiredFeatureType)
					&& Objects.requireNonNull(UserProvider.getCurrent()).hasAllUserRights(requiredUserRights)) {
					return addCreateEntry(optionType);
				} else {
					return this;
				}
			}

			private Builder add(OptionType optionType, List<? extends Serializable> selectableOptions) {
				options.add(new Option<>(optionType, selectableOptions));

				return this;
			}

			public Options build() {
				return new Options(options);
			}

			public int size() {
				return options.size();
			}

			public PickOrCreateEntryResult getSingleAvailableCreateResult() {
				if (size() != 1) {
					return null;
				} else {
					PickOrCreateEntryResult result = new PickOrCreateEntryResult();
					switch (options.get(0).type) {
					case CREATE_CASE:
						result.setNewCase(true);
						break;
					case CREATE_CONTACT:
						result.setNewContact(true);
						break;
					case CREATE_EVENT_PARTICIPANT:
						result.setNewEventParticipant(true);
						break;
					default:
						throw new IllegalArgumentException(options.get(0).type.toString());
					}
					return result;
				}
			}
		}
	}
}
