package de.symeda.sormas.ui.utils;

import java.util.Collections;
import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;

public class ArchivingController {

	public static final String ARCHIVE_DEARCHIVE_BUTTON_ID = "archiveDearchive";

	public void archiveEntity(EntityDto coreEntityDto, CoreFacade entityFacade, CoreEntityArchiveMessages archiveMessages, Runnable callback) {
		VerticalLayout verticalLayout = new VerticalLayout();

		Label contentLabel = new Label(I18nProperties.getString(archiveMessages.getConfirmationArchiveEntity()));
		contentLabel.setWidth(100, Sizeable.Unit.PERCENTAGE);
		verticalLayout.addComponent(contentLabel);

		DateField endOfProcessingDate = new DateField();
		endOfProcessingDate.setValue(DateHelper8.toLocalDate(entityFacade.calculateEndOfProcessingDate(coreEntityDto.getUuid())));
		endOfProcessingDate.setCaption(I18nProperties.getCaption(Captions.endOfProcessingDate));
		endOfProcessingDate.setDateFormat(DateFormatHelper.getDateFormatPattern());

		verticalLayout.addComponent(endOfProcessingDate);
		verticalLayout.setMargin(false);

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(archiveMessages.getHeadingArchiveEntity()),
			verticalLayout,
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			e -> {
				if (Boolean.TRUE.equals(e)) {
					entityFacade.archive(coreEntityDto.getUuid(), DateHelper8.toDate(endOfProcessingDate.getValue()));

					Notification.show(
						String.format(
							I18nProperties.getString(archiveMessages.getMessageEntityArchived()),
							I18nProperties.getString(archiveMessages.getEntityName())),
						Notification.Type.ASSISTIVE_NOTIFICATION);
					callback.run();
				}
			});
	}

	public void dearchiveEntity(EntityDto coreEntityDto, CoreFacade entityFacade, CoreEntityArchiveMessages archiveMessages, Runnable callback) {
		VerticalLayout verticalLayout = new VerticalLayout();

		Label contentLabel = new Label(
			String.format(
				I18nProperties.getString(archiveMessages.getConfirmationDearchiveEntity()),
				I18nProperties.getString(archiveMessages.getEntityName()).toLowerCase(),
				I18nProperties.getString(archiveMessages.getEntityName()).toLowerCase()));
		contentLabel.setWidth(100, Sizeable.Unit.PERCENTAGE);
		verticalLayout.addComponent(contentLabel);
		verticalLayout.setMargin(false);

		TextArea dearchiveReason = new TextArea();
		dearchiveReason.setCaption(I18nProperties.getCaption(Captions.dearchiveReason));
		dearchiveReason.setWidth(100, Sizeable.Unit.PERCENTAGE);
		dearchiveReason.setRows(2);
		dearchiveReason.setRequiredIndicatorVisible(true);
		verticalLayout.addComponent(dearchiveReason);

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(archiveMessages.getHeadingDearchiveEntity()),
			verticalLayout,
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					if (dearchiveReason.getValue().isEmpty()) {
						dearchiveReason.setComponentError(new UserError(I18nProperties.getString(Strings.messageArchiveUndoneReasonMandatory)));
						return false;
					}
					entityFacade.dearchive(Collections.singletonList(coreEntityDto.getUuid()), dearchiveReason.getValue());
					Notification.show(
						String.format(
							I18nProperties.getString(archiveMessages.getMessageEntityDearchived()),
							I18nProperties.getString(archiveMessages.getEntityName())),
						Notification.Type.ASSISTIVE_NOTIFICATION);
					callback.run();
				}
				return true;
			});
	}

	public void archiveSelectedItems(
		List<String> entityUuids,
		CoreFacade entityFacade,
		String noSelectionMessage,
		String archiveConfirmationMessage,
		String archivedHeading,
		String archivedMessage,
		Runnable callback) {

		if (entityUuids.isEmpty()) {
			new Notification(
				I18nProperties.getString(noSelectionMessage),
				I18nProperties.getString(noSelectionMessage),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmArchiving),
				new Label(String.format(I18nProperties.getString(archiveConfirmationMessage), entityUuids.size())),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				e -> {
					if (Boolean.TRUE.equals(e)) {
						entityFacade.archive(entityUuids);

						callback.run();
						new Notification(
							I18nProperties.getString(archivedHeading),
							I18nProperties.getString(archivedMessage),
							Notification.Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}

	public void dearchiveSelectedItems(
		List<String> entityUuids,
		CoreFacade entityFacade,
		String noSelectionMessage,
		String messageNoEntitySelected,
		String dearchiveConfirmationMessage,
		String entity,
		String headingConfirmationDeachiving,
		String headingEntityDearchived,
		String messageEntityDearchived,
		Runnable callback) {

		if (entityUuids.isEmpty()) {
			new Notification(
				I18nProperties.getString(noSelectionMessage),
				I18nProperties.getString(messageNoEntitySelected),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VerticalLayout verticalLayout = new VerticalLayout();

			Label contentLabel = new Label(
				String.format(
					String.format(I18nProperties.getString(dearchiveConfirmationMessage), entityUuids.size()),
					I18nProperties.getString(entity).toLowerCase(),
					I18nProperties.getString(entity).toLowerCase()));
			contentLabel.setWidth(100, Sizeable.Unit.PERCENTAGE);
			verticalLayout.addComponent(contentLabel);

			TextArea dearchiveReason = new TextArea();
			dearchiveReason.setCaption(I18nProperties.getCaption(Captions.dearchiveReason));
			dearchiveReason.setWidth(100, Sizeable.Unit.PERCENTAGE);
			dearchiveReason.setRows(2);
			dearchiveReason.setRequiredIndicatorVisible(true);
			verticalLayout.addComponent(dearchiveReason);
			verticalLayout.setMargin(false);

			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(headingConfirmationDeachiving),
				verticalLayout,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				confirmed -> {
					if (Boolean.TRUE.equals(confirmed)) {
						if (dearchiveReason.getValue().isEmpty()) {
							dearchiveReason.setComponentError(new UserError(I18nProperties.getString(Strings.messageArchiveUndoneReasonMandatory)));
							return false;
						}
						entityFacade.dearchive(entityUuids, dearchiveReason.getValue());

						callback.run();
						new Notification(
							I18nProperties.getString(headingEntityDearchived),
							I18nProperties.getString(messageEntityDearchived),
							Notification.Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
					return true;
				});
		}
	}

	public void addArchivingButton(
		EntityDto entityDto,
		CoreFacade coreFacade,
		CoreEntityArchiveMessages archiveMessages,
		CommitDiscardWrapperComponent editView,
		Runnable callback) {
		boolean archived = coreFacade.isArchived(entityDto.getUuid());
		Button archiveButton = ButtonHelper.createButton(
			ARCHIVE_DEARCHIVE_BUTTON_ID,
			I18nProperties.getCaption(archived ? Captions.actionDearchiveCoreEntity : Captions.actionArchiveCoreEntity),
			e -> {
				if (editView.isModified()) {
					editView.commit();
				}

				if (archived) {
					dearchiveEntity(entityDto, coreFacade, archiveMessages, callback);
				} else {
					archiveEntity(entityDto, coreFacade, archiveMessages, callback);
				}
			},
			ValoTheme.BUTTON_LINK);

		editView.getButtonsPanel().addComponentAsFirst(archiveButton);
		editView.getButtonsPanel().setComponentAlignment(archiveButton, Alignment.BOTTOM_LEFT);
	}
}
