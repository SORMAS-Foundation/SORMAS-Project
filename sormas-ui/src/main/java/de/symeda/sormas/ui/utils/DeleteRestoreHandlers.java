package de.symeda.sormas.ui.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Sizeable;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.DeletableFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PermanentlyDeletableFacade;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.utils.FieldConstraints;

public final class DeleteRestoreHandlers {

	private DeleteRestoreHandlers() {

	}

	public static CoreEntityDeleteRestoreHandler forCase() {
		return new CoreEntityDeleteRestoreHandler<>(FacadeProvider.getCaseFacade(), DeleteRestoreMessages.CASE);
	}

	public static CoreEntityDeleteRestoreHandler forContact() {
		return new CoreEntityDeleteRestoreHandler<>(FacadeProvider.getContactFacade(), DeleteRestoreMessages.CONTACT);
	}

	public static CoreEntityDeleteRestoreHandler forEvent() {
		return new CoreEntityDeleteRestoreHandler<>(FacadeProvider.getEventFacade(), DeleteRestoreMessages.EVENT);
	}

	public static CoreEntityDeleteRestoreHandler forEventParticipant() {
		return new CoreEntityDeleteRestoreHandler<>(FacadeProvider.getEventParticipantFacade(), DeleteRestoreMessages.EVENT_PARTICIPANT);
	}

	public static CoreEntityDeleteRestoreHandler forTravelEntry() {
		return new CoreEntityDeleteRestoreHandler<>(FacadeProvider.getTravelEntryFacade(), DeleteRestoreMessages.TRAVEL_ENTRY);
	}

	public static SampleDeleteRestoreHandler forSample() {
		return new SampleDeleteRestoreHandler();
	}

	public static PermanentDeleteHandler forTask() {
		return new PermanentDeleteHandler(FacadeProvider.getTaskFacade(), DeleteRestoreMessages.TASK);
	}

	public static PermanentDeleteHandler forVisit() {
		return new PermanentDeleteHandler(FacadeProvider.getVisitFacade(), DeleteRestoreMessages.VISIT);
	}

	public static PermanentDeleteHandler forExternalMessage() {
		return new PermanentDeleteHandler(FacadeProvider.getExternalMessageFacade(), DeleteRestoreMessages.EXTERNAL_MESSAGE);
	}

	public static CoreEntityDeleteRestoreHandler forEnvironmentSample() {
		return new CoreEntityDeleteRestoreHandler(FacadeProvider.getEnvironmentSampleFacade(), DeleteRestoreMessages.ENVIRONMENT_SAMPLE);
	}

	public static class DeleteRestoreHandler<T extends EntityDto, F extends DeletableFacade>
		implements DeleteRestoreController.IDeleteRestoreHandler<T> {

		protected final F entityFacade;
		private final DeleteRestoreMessages deleteRestoreMessages;

		protected ComboBox<DeletionReason> deleteReasonComboBox;
		protected TextArea otherDeletionReason;

		private DeleteRestoreHandler(F entityFacade, DeleteRestoreMessages deleteRestoreMessages) {
			this.entityFacade = entityFacade;
			this.deleteRestoreMessages = deleteRestoreMessages;
		}

		@Override
		public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
			return entityFacade.delete(uuids, deletionDetails);
		}

		@Override
		public List<ProcessedEntity> restore(List<String> uuids) {
			return entityFacade.restore(uuids);
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

	public static class CoreEntityDeleteRestoreHandler<T extends EntityDto, F extends DeletableFacade> extends DeleteRestoreHandler<T, F>
		implements DeleteRestoreController.IDeleteRestoreHandler<T> {

		protected CoreEntityDeleteRestoreHandler(F entityFacade, DeleteRestoreMessages deleteRestoreMessages) {
			super(entityFacade, deleteRestoreMessages);
		}
	}

	public static class SampleDeleteRestoreHandler extends DeleteRestoreHandler<SampleDto, SampleFacade> {

		protected SampleDeleteRestoreHandler() {
			super(FacadeProvider.getSampleFacade(), DeleteRestoreMessages.SAMPLE);
		}
	}

	public static class PermanentDeleteHandler<T extends EntityDto, F extends PermanentlyDeletableFacade>
		implements PermanentDeleteController.IPermanentDeleteHandler<T> {

		protected final F entityFacade;
		private final DeleteRestoreMessages deleteRestoreMessages;

		private PermanentDeleteHandler(F entityFacade, DeleteRestoreMessages deleteRestoreMessages) {
			this.entityFacade = entityFacade;
			this.deleteRestoreMessages = deleteRestoreMessages;
		}

		@Override
		public List<ProcessedEntity> delete(List<String> uuids) {
			List<ProcessedEntity> processedEntities = entityFacade.delete(uuids);
			return processedEntities;
		}

		@Override
		public DeleteRestoreMessages getDeleteRestoreMessages() {
			return deleteRestoreMessages;
		}
	}
}
