package de.symeda.sormas.ui.utils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.UserError;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.caze.AbstractCaseView;
import de.symeda.sormas.ui.events.EventDataView;

public class ArchiveController {

	public void archiveEntity(
		EntityDto coreEntityDto,
		CoreFacade entityFacade,
		String popupHeading,
		String archiveConfirmationMessage,
		String entity,
		String archiveMessage,
		String detailViewName) {
		VerticalLayout verticalLayout = new VerticalLayout();

		Label contentLabel = new Label(
			String.format(
				I18nProperties.getString(archiveConfirmationMessage),
				I18nProperties.getString(entity).toLowerCase(),
				I18nProperties.getString(entity).toLowerCase()));
		contentLabel.setWidth(100, Sizeable.Unit.PERCENTAGE);
		verticalLayout.addComponent(contentLabel);

		DateField endOfProcessingDate = new DateField();
		endOfProcessingDate
			.setValue(DateHelper8.toLocalDate(entityFacade.calculateEndOfProcessingDate(Collections.singletonList(coreEntityDto.getUuid()))));
		endOfProcessingDate.setCaption(I18nProperties.getCaption(Captions.endOfProcessingDate));
		verticalLayout.addComponent(endOfProcessingDate);

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(popupHeading),
			verticalLayout,
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			e -> {
				if (Boolean.TRUE.equals(e)) {
					entityFacade
						.archiveCoreEntities(Collections.singletonList(coreEntityDto.getUuid()), DateHelper8.toDate(endOfProcessingDate.getValue()));

					Notification.show(
						String.format(I18nProperties.getString(archiveMessage), I18nProperties.getString(entity)),
						Notification.Type.ASSISTIVE_NOTIFICATION);
					navigateToView(detailViewName, coreEntityDto.getUuid(), null);
				}
			});
	}

	public void dearchiveEntity(
		EntityDto coreEntityDto,
		CoreFacade entityFacade,
		String popupHeading,
		String dearchiveConfirmationMessage,
		String entity,
		String dearchiveMessage,
		String detailViewName) {
		VerticalLayout verticalLayout = new VerticalLayout();

		Label contentLabel = new Label(
			String.format(
				I18nProperties.getString(dearchiveConfirmationMessage),
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

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(popupHeading),
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
					entityFacade.dearchiveCoreEntities(Collections.singletonList(coreEntityDto.getUuid()), dearchiveReason.getValue());
					Notification.show(
						String.format(I18nProperties.getString(dearchiveMessage), I18nProperties.getString(entity)),
						Notification.Type.ASSISTIVE_NOTIFICATION);
					navigateToView(detailViewName, coreEntityDto.getUuid(), null);
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
						Date endOfProcessingDate = entityFacade.calculateEndOfProcessingDate(entityUuids);
						entityFacade.archiveCoreEntities(entityUuids, endOfProcessingDate);

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

			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(headingConfirmationDeachiving),
				verticalLayout,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				connfirmed -> {
					if (Boolean.TRUE.equals(connfirmed)) {
						if (dearchiveReason.getValue().isEmpty()) {
							dearchiveReason.setComponentError(new UserError(I18nProperties.getString(Strings.messageArchiveUndoneReasonMandatory)));
							return false;
						}
						entityFacade.dearchiveCoreEntities(entityUuids, dearchiveReason.getValue());

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

	public void navigateToView(String viewName, String caseUuid, ViewMode viewMode) {
		navigateToView(viewName, caseUuid, viewMode, false);
	}

	public void navigateToView(String viewName, String caseUuid, ViewMode viewMode, boolean openTab) {

		String navigationState = viewName + "/" + caseUuid;
		if (viewMode == ViewMode.NORMAL) {
			// pass full view mode as param so it's also used for other views when switching
			navigationState += "?" + AbstractCaseView.VIEW_MODE_URL_PREFIX + "=" + viewMode;
		}

		if (openTab) {
			SormasUI.get().getPage().open(SormasUI.get().getPage().getLocation().getRawPath() + "#!" + navigationState, "_blank", false);
		} else {
			SormasUI.get().getNavigator().navigateTo(navigationState);
		}
	}

	public void navigateToData(String eventUuid) {
		navigateToData(eventUuid, false);
	}

	public void navigateToData(String eventUuid, boolean openTab) {

		String navigationState = EventDataView.VIEW_NAME + "/" + eventUuid;
		if (openTab) {
			SormasUI.get().getPage().open(SormasUI.get().getPage().getLocation().getRawPath() + "#!" + navigationState, "_blank", false);
		} else {
			SormasUI.get().getNavigator().navigateTo(navigationState);
		}
	}

}
