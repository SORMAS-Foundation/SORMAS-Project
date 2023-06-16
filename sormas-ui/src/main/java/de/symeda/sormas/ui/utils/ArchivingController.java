package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.ArchivableFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.ui.utils.ArchivingHandlers.ArchiveHandler;

public class ArchivingController {

	public static final String ARCHIVE_DEARCHIVE_BUTTON_ID = "archiveDearchive";

	public <F extends ArchivableFacade> void addArchivingButton(
		EntityDto entityDto,
		ArchiveHandler<F> archiveHandler,
		CommitDiscardWrapperComponent<?> editView,
		Runnable callback) {
		boolean archived = archiveHandler.isArchived(entityDto.getUuid());
		Button archiveButton = ButtonHelper.createButton(
			ARCHIVE_DEARCHIVE_BUTTON_ID,
			I18nProperties.getCaption(archived ? Captions.actionDearchiveCoreEntity : Captions.actionArchiveCoreEntity),
			e -> {
				boolean isCommitSuccessFul = true;

				if (editView.isModified()) {
					isCommitSuccessFul = editView.commitAndHandle();
				}

				if (isCommitSuccessFul) {
					if (archived) {
						dearchive(entityDto, archiveHandler, callback);
					} else {
						archive(entityDto, archiveHandler, callback);
					}
				}
			},
			ValoTheme.BUTTON_LINK);

		editView.getButtonsPanel().addComponentAsFirst(archiveButton);
		editView.getButtonsPanel().setComponentAlignment(archiveButton, Alignment.BOTTOM_LEFT);
	}

	private <F extends ArchivableFacade> void archive(EntityDto entityDto, ArchiveHandler<F> archiveHandler, Runnable callback) {
		ArchiveMessages archiveMessages = archiveHandler.getArchiveMessages();

		VerticalLayout verticalLayout = new VerticalLayout();

		Label contentLabel = new Label(I18nProperties.getString(archiveMessages.getConfirmationArchiveEntity()));
		contentLabel.setWidth(100, Sizeable.Unit.PERCENTAGE);
		verticalLayout.setMargin(false);
		verticalLayout.addComponent(contentLabel);

		archiveHandler.addAdditionalArchiveFields(verticalLayout, entityDto);

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(archiveMessages.getHeadingArchiveEntity()),
			verticalLayout,
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			e -> {
				if (Boolean.TRUE.equals(e)) {
					archiveHandler.archive(entityDto.getUuid());
					Notification.show(
						String.format(
							I18nProperties.getString(archiveMessages.getMessageEntityArchived()),
							I18nProperties.getString(archiveMessages.getEntityName())),
						Notification.Type.ASSISTIVE_NOTIFICATION);

					callback.run();
				}
			});
	}

	private <F extends ArchivableFacade> void dearchive(EntityDto entityDto, ArchiveHandler<F> archiveHandler, Runnable callback) {
		ArchiveMessages archiveMessages = archiveHandler.getArchiveMessages();

		VerticalLayout verticalLayout = new VerticalLayout();

		Label contentLabel = new Label(I18nProperties.getString(archiveMessages.getConfirmationDearchiveEntity()));
		contentLabel.setWidth(100, Sizeable.Unit.PERCENTAGE);
		verticalLayout.addComponent(contentLabel);
		verticalLayout.setMargin(false);

		archiveHandler.addAdditionalDearchiveFields(verticalLayout);

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(archiveMessages.getHeadingDearchiveEntity()),
			verticalLayout,
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			confirmed -> {
				if (Boolean.TRUE.equals(confirmed)) {
					if (!archiveHandler.validateAdditionalDearchivationFields()) {
						return false;
					}

					archiveHandler.dearchive(Collections.singletonList(entityDto.getUuid()));
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

	public <T extends HasUuid, F extends ArchivableFacade> void archiveSelectedItems(
		Collection<T> entities,
		ArchiveHandler<F> archiveHandler,
		Consumer<List<T>> batchCallback) {
		ArchiveMessages archiveMessages = archiveHandler.getArchiveMessages();

		if (entities.isEmpty()) {
			new Notification(
				I18nProperties.getString(archiveMessages.getHeadingNoEntitySelected()),
				I18nProperties.getString(archiveMessages.getMessageNoEntitySelected()),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {

			VerticalLayout verticalLayout = new VerticalLayout();
			Label contentLabel =
				new Label(String.format(I18nProperties.getString(archiveMessages.getConfirmationArchiveEntities()), entities.size()));
			contentLabel.setWidth(100, Sizeable.Unit.PERCENTAGE);
			verticalLayout.addComponent(contentLabel);
			verticalLayout.setMargin(false);

			archiveHandler.addAdditionalArchiveFields(verticalLayout, null);

			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmArchiving),
				verticalLayout,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				e -> {
					if (Boolean.TRUE.equals(e)) {
						List<T> selectedCasesCpy = new ArrayList<>(entities);
						new BulkOperationHandler<T>().doBulkOperation(selectedEntries -> {
							archiveHandler.archive(selectedEntries.stream().map(HasUuid::getUuid).collect(Collectors.toList()));
							return selectedEntries.size();
						}, selectedCasesCpy, batchCallback);
					}
				});
		}
	}

	public <T extends HasUuid, F extends ArchivableFacade> void dearchiveSelectedItems(
		Collection<T> entities,
		ArchiveHandler<F> archiveHandler,
		Consumer<List<T>> batchCallback) {

		ArchiveMessages archiveMessages = archiveHandler.getArchiveMessages();

		if (entities.isEmpty()) {
			new Notification(
				I18nProperties.getString(archiveMessages.getHeadingNoEntitySelected()),
				I18nProperties.getString(archiveMessages.getMessageNoEntitySelected()),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VerticalLayout verticalLayout = new VerticalLayout();

			Label contentLabel = new Label(String.format(I18nProperties.getString(archiveMessages.getConfirmDearchiveEntities()), entities.size()));
			contentLabel.setWidth(100, Sizeable.Unit.PERCENTAGE);
			verticalLayout.setMargin(false);
			verticalLayout.addComponent(contentLabel);

			archiveHandler.addAdditionalDearchiveFields(verticalLayout);

			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(archiveMessages.getHeadingConfirmationDearchiving()),
				verticalLayout,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				confirmed -> {
					if (Boolean.TRUE.equals(confirmed)) {
						if (!archiveHandler.validateAdditionalDearchivationFields()) {
							return false;
						}

						List<T> selectedCasesCpy = new ArrayList<>(entities);
						new BulkOperationHandler<T>().doBulkOperation(selectedEntries -> {
							archiveHandler.dearchive(selectedEntries.stream().map(HasUuid::getUuid).collect(Collectors.toList()));
							return selectedEntries.size();
						}, selectedCasesCpy, batchCallback);
					}
					return true;
				});
		}
	}
}
