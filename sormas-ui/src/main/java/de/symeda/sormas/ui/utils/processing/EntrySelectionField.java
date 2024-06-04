/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils.processing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.data.provider.Query;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.dataprocessing.PickOrCreateEntryResult;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.caze.components.caseselection.CaseSelectionGrid;
import de.symeda.sormas.ui.contact.ContactSelectionGrid;
import de.symeda.sormas.ui.events.EventParticipantSelectionGrid;
import de.symeda.sormas.ui.utils.CssStyles;

public class EntrySelectionField extends CustomField<PickOrCreateEntryResult> {

	private final Options selectableOptions;
	private Consumer<Boolean> selectionChangeCallback;

	private RadioButtonGroup<OptionType> rbSelectCase;
	private RadioButtonGroup<OptionType> rbSelectContact;
	protected CaseSelectionGrid caseGrid;
	protected ContactSelectionGrid contactGrid;
	protected EventParticipantSelectionGrid eventParticipantGrid;
	private RadioButtonGroup<OptionType> rbSelectEventParticipant;
	private RadioButtonGroup<OptionType> rbCreateEntity;

	public EntrySelectionField(Options selectableOptions) {
		super();
		this.selectableOptions = selectableOptions;
	}

	@Override
	protected Component initContent() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(false);
		mainLayout.setSizeFull();

		for (Option<?> option : selectableOptions.selectableOptions) {
			switch (option.type) {
			case SELECT_CASE: {
				if (CollectionUtils.isNotEmpty(option.selectableItems)) {
					addSelectCaseRadioGroup(mainLayout);
					addCaseGrid((List<CaseSelectionDto>) option.selectableItems, mainLayout);
				}
				break;
			}
			case SELECT_CONTACT: {
				if (CollectionUtils.isNotEmpty(option.selectableItems)) {
					addSelectContactRadioGroup(mainLayout);
					addContactGrid((List<SimilarContactDto>) option.selectableItems, mainLayout);
				}
				break;
			}
			case SELECT_EVENT_PARTICIPANT: {
				if (CollectionUtils.isNotEmpty(option.selectableItems)) {
					addSelectEventParticipantRadioGroup(mainLayout);
					addEventParticipantGrid((List<SimilarEventParticipantDto>) option.selectableItems, mainLayout);
				}
				break;
			}
			default:
				addCreateEntityRadio(option.type, mainLayout);
			}

		}

		return mainLayout;
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

	public void setSelectionChangeCallback(Consumer<Boolean> selectionChangeCallback) {
		this.selectionChangeCallback = selectionChangeCallback;
	}

	private void addSelectCaseRadioGroup(AbstractLayout mainLayout) {
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

	private void addCaseGrid(List<CaseSelectionDto> cases, AbstractLayout mainLayout) {
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

	private void addSelectContactRadioGroup(AbstractLayout mainLayout) {
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

	private void addContactGrid(List<SimilarContactDto> contacts, AbstractLayout mainLayout) {
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

	private void addSelectEventParticipantRadioGroup(AbstractLayout mainLayout) {
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

	private void addEventParticipantGrid(List<SimilarEventParticipantDto> eventParticipants, AbstractLayout mainLayout) {
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

	private void addCreateEntityRadio(OptionType creationOptionType, AbstractLayout mainLayout) {
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

	public enum OptionType {
		CREATE_CASE,
		CREATE_CONTACT,
		CREATE_EVENT_PARTICIPANT,
		SELECT_CASE,
		SELECT_CONTACT,
		SELECT_EVENT_PARTICIPANT;
	}

	public static final class Option<T extends Serializable> implements Serializable {

		private static final long serialVersionUID = -3866850428669683952L;

		private final OptionType type;
		private final List<T> selectableItems;

		private Option(OptionType type, List<T> selectableItems) {
			this.type = type;
			this.selectableItems = selectableItems;
		}

		public OptionType getType() {
			return type;
		}

		public List<T> getSelectableItems() {
			return selectableItems;
		}
	}

	public static final class Options implements Serializable {

		private static final long serialVersionUID = 5824346382436184226L;

		private final List<Option<?>> selectableOptions;

		private Options(List<Option<?>> selectableOptions) {
			this.selectableOptions = selectableOptions;
		}

		public Stream<Option<?>> stream() {
			return selectableOptions.stream();
		}

		public static class Builder {

			private final List<Option<?>> options;

			public Builder() {
				options = new ArrayList<>();
			}

			public Options.Builder addSelectCase(List<CaseSelectionDto> selectableCases) {
				if (!selectableCases.isEmpty() && UiUtil.permitted(FeatureType.CASE_SURVEILANCE, UserRight.CASE_CREATE, UserRight.CASE_EDIT)) {
					return add(OptionType.SELECT_CASE, selectableCases);
				} else {
					return this;
				}
			}

			public Options.Builder addSelectContact(List<SimilarContactDto> selectableContacts) {
				if (!selectableContacts.isEmpty()
					&& UiUtil.permitted(FeatureType.CONTACT_TRACING, UserRight.CONTACT_CREATE, UserRight.CONTACT_EDIT)) {
					return add(OptionType.SELECT_CONTACT, selectableContacts);
				} else {
					return this;
				}
			}

			public Options.Builder addSelectEventParticipant(List<SimilarEventParticipantDto> selectableEventParticipants) {
				if (!selectableEventParticipants.isEmpty()
					&& UiUtil.permitted(FeatureType.EVENT_SURVEILLANCE, UserRight.EVENTPARTICIPANT_CREATE, UserRight.EVENTPARTICIPANT_EDIT)) {
					return add(OptionType.SELECT_EVENT_PARTICIPANT, selectableEventParticipants);
				} else {
					return this;
				}
			}

			public Options.Builder addCreateEntry(OptionType optionType) {
				return add(optionType, null);
			}

			public Options.Builder addCreateEntry(OptionType optionType, FeatureType requiredFeatureType, UserRight... requiredUserRights) {
				if (UiUtil.permitted(requiredFeatureType, requiredUserRights)) {
					return addCreateEntry(optionType);
				} else {
					return this;
				}
			}

			private Options.Builder add(OptionType optionType, List<? extends Serializable> selectableOptions) {
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
