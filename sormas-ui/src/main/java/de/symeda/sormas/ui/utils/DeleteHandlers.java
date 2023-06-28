package de.symeda.sormas.ui.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Sizeable;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.DeletableFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.utils.FieldConstraints;

public final class DeleteHandlers {

	private DeleteHandlers() {

	}

	public static CoreEntityDeleteHandler forCase() {
		return new CoreEntityDeleteHandler<>(FacadeProvider.getCaseFacade(), DeleteRestoreMessages.CASE);
	}

	public static CoreEntityDeleteHandler forContact() {
		return new CoreEntityDeleteHandler<>(FacadeProvider.getContactFacade(), DeleteRestoreMessages.CONTACT);
	}

	public static CoreEntityDeleteHandler forEvent() {
		return new CoreEntityDeleteHandler<>(FacadeProvider.getEventFacade(), DeleteRestoreMessages.EVENT);
	}

	public static CoreEntityDeleteHandler forEventParticipant() {
		return new CoreEntityDeleteHandler<>(FacadeProvider.getEventParticipantFacade(), DeleteRestoreMessages.EVENT_PARTICIPANT);
	}

	public static CoreEntityDeleteHandler forTravelEntry() {
		return new CoreEntityDeleteHandler<>(FacadeProvider.getTravelEntryFacade(), DeleteRestoreMessages.TRAVEL_ENTRY);
	}

	public static SampleDeleteHandler forSample() {
		return new SampleDeleteHandler();
	}

	public static class DeleteHandler<T extends EntityDto, F extends DeletableFacade> implements DeleteRestoreController.IDeleteHandler<T> {

		protected final F entityFacade;
		private final DeleteRestoreMessages deleteRestoreMessages;

		protected ComboBox<DeletionReason> deleteReasonComboBox;
		protected TextArea otherDeletionReason;

		private DeleteHandler(F entityFacade, DeleteRestoreMessages deleteRestoreMessages) {
			this.entityFacade = entityFacade;
			this.deleteRestoreMessages = deleteRestoreMessages;
		}

		@Override
		public void delete(String uuid, DeletionDetails deletionDetails) {

		}

		@Override
		public int delete(List<String> uuids, DeletionDetails deletionDetails) {
			entityFacade.delete(uuids, deletionDetails);
			return uuids.size();
		}

		@Override
		public DeleteRestoreMessages getDeleteRestoreMessages() {
			return deleteRestoreMessages;
		}

		@Override
		public void addAdditionalDeleteReasonField(VerticalLayout verticalLayout) {
			deleteReasonComboBox = new ComboBox(null, Arrays.asList(DeletionReason.values()));
			deleteReasonComboBox.setCaption(I18nProperties.getCaption(Captions.deletionReason));
			deleteReasonComboBox.setWidth(100, Sizeable.Unit.PERCENTAGE);
			deleteReasonComboBox.setRequiredIndicatorVisible(true);
			verticalLayout.addComponent(deleteReasonComboBox);

			otherDeletionReason = new TextArea();
			otherDeletionReason.setCaption(I18nProperties.getCaption(Captions.otherDeletionReason));
			verticalLayout.addComponent(otherDeletionReason);
			otherDeletionReason.setVisible(false);
			otherDeletionReason.setWidth(100, Sizeable.Unit.PERCENTAGE);
			otherDeletionReason.setRows(3);
			otherDeletionReason.setMaxLength(FieldConstraints.CHARACTER_LIMIT_TEXT);
			otherDeletionReason.setRequiredIndicatorVisible(true);

			deleteReasonComboBox.addValueChangeListener(valueChangeEvent -> {
				otherDeletionReason.setVisible(valueChangeEvent.getValue() == (DeletionReason.OTHER_REASON));
			});

			deleteReasonComboBox.addValueChangeListener(valueChangeEvent -> {
				if (deleteReasonComboBox.isEmpty()) {
					deleteReasonComboBox.setComponentError(new UserError(I18nProperties.getString(Strings.messageDeleteReasonNotFilled)));
				} else {
					deleteReasonComboBox.setComponentError(null);
				}
			});

			otherDeletionReason.addValueChangeListener(valueChangeEvent -> {
				if (deleteReasonComboBox.getValue() == DeletionReason.OTHER_REASON && StringUtils.isBlank(otherDeletionReason.getValue())) {
					otherDeletionReason.setComponentError(new UserError(I18nProperties.getString(Strings.messageOtherDeleteReasonNotFilled)));
				} else {
					otherDeletionReason.setComponentError(null);
				}
			});
		}

		@Override
		public boolean validateAdditionalDeleteReasonFields() {
			if (deleteReasonComboBox.isEmpty()) {
				deleteReasonComboBox.setComponentError(new UserError(I18nProperties.getString(Strings.messageDeleteReasonNotFilled)));
				return false;
			} else if (deleteReasonComboBox.getValue() == DeletionReason.OTHER_REASON && StringUtils.isBlank(otherDeletionReason.getValue())) {
				otherDeletionReason.setComponentError(new UserError(I18nProperties.getString(Strings.messageOtherDeleteReasonNotFilled)));
				return false;
			}

			return true;
		}

		@Override
		public ComboBox<DeletionReason> getDeleteReasonComboBox() {
			return deleteReasonComboBox;
		}

		@Override
		public TextArea getOtherDeletionReason() {
			return otherDeletionReason;
		}

		@Override
		public void clearOtherReason() {
			if (deleteReasonComboBox.getValue() != DeletionReason.OTHER_REASON && !StringUtils.isBlank(otherDeletionReason.getValue())) {
				otherDeletionReason.clear();
			}
		}
	}

	//TODO: check if can extend the basic handler class
	public static class CoreEntityDeleteHandler<T extends EntityDto, F extends CoreFacade<T, ?, ?, ?>>
		implements DeleteRestoreController.IDeleteHandler<T> {

		protected final F entityFacade;
		private final DeleteRestoreMessages deleteRestoreMessages;

		protected ComboBox<DeletionReason> deleteReasonComboBox;
		protected TextArea otherDeletionReason;

		public CoreEntityDeleteHandler(F entityFacade, DeleteRestoreMessages deleteRestoreMessages) {
			this.entityFacade = entityFacade;
			this.deleteRestoreMessages = deleteRestoreMessages;
		}

		@Override
		public void delete(String uuid, DeletionDetails deletionDetails) {

		}

		@Override
		public int delete(List<String> uuids, DeletionDetails deletionDetails) {
			entityFacade.delete(uuids, deletionDetails);
			return uuids.size();
		}

		@Override
		public DeleteRestoreMessages getDeleteRestoreMessages() {
			return deleteRestoreMessages;
		}

		@Override
		public void addAdditionalDeleteReasonField(VerticalLayout verticalLayout) {
			deleteReasonComboBox = new ComboBox(null, Arrays.asList(DeletionReason.values()));
			deleteReasonComboBox.setCaption(I18nProperties.getCaption(Captions.deletionReason));
			deleteReasonComboBox.setWidth(100, Sizeable.Unit.PERCENTAGE);
			deleteReasonComboBox.setRequiredIndicatorVisible(true);
			verticalLayout.addComponent(deleteReasonComboBox);

			otherDeletionReason = new TextArea();
			otherDeletionReason.setCaption(I18nProperties.getCaption(Captions.otherDeletionReason));
			verticalLayout.addComponent(otherDeletionReason);
			otherDeletionReason.setVisible(false);
			otherDeletionReason.setWidth(100, Sizeable.Unit.PERCENTAGE);
			otherDeletionReason.setRows(3);
			otherDeletionReason.setMaxLength(FieldConstraints.CHARACTER_LIMIT_TEXT);
			otherDeletionReason.setRequiredIndicatorVisible(true);

			deleteReasonComboBox.addValueChangeListener(valueChangeEvent -> {
				otherDeletionReason.setVisible(valueChangeEvent.getValue() == (DeletionReason.OTHER_REASON));
			});

			deleteReasonComboBox.addValueChangeListener(valueChangeEvent -> {
				if (deleteReasonComboBox.isEmpty()) {
					deleteReasonComboBox.setComponentError(new UserError(I18nProperties.getString(Strings.messageDeleteReasonNotFilled)));
				} else {
					deleteReasonComboBox.setComponentError(null);
				}
			});

			otherDeletionReason.addValueChangeListener(valueChangeEvent -> {
				if (deleteReasonComboBox.getValue() == DeletionReason.OTHER_REASON && StringUtils.isBlank(otherDeletionReason.getValue())) {
					otherDeletionReason.setComponentError(new UserError(I18nProperties.getString(Strings.messageOtherDeleteReasonNotFilled)));
				} else {
					otherDeletionReason.setComponentError(null);
				}
			});
		}

		@Override
		public TextArea getOtherDeletionReason() {
			return otherDeletionReason;
		}

		@Override
		public ComboBox<DeletionReason> getDeleteReasonComboBox() {
			return deleteReasonComboBox;
		}

		@Override
		public void clearOtherReason() {
			if (deleteReasonComboBox.getValue() != DeletionReason.OTHER_REASON && !StringUtils.isBlank(otherDeletionReason.getValue())) {
				otherDeletionReason.clear();
			}
		}

		@Override
		public boolean validateAdditionalDeleteReasonFields() {
			if (deleteReasonComboBox.isEmpty()) {
				deleteReasonComboBox.setComponentError(new UserError(I18nProperties.getString(Strings.messageDeleteReasonNotFilled)));
				return false;
			} else if (deleteReasonComboBox.getValue() == DeletionReason.OTHER_REASON && StringUtils.isBlank(otherDeletionReason.getValue())) {
				otherDeletionReason.setComponentError(new UserError(I18nProperties.getString(Strings.messageOtherDeleteReasonNotFilled)));
				return false;
			}

			return true;
		}
	}

	public static class SampleDeleteHandler extends DeleteHandler<SampleDto, SampleFacade> {

		protected SampleDeleteHandler() {
			super(FacadeProvider.getSampleFacade(), DeleteRestoreMessages.SAMPLE);
		}
	}
}
