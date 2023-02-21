package de.symeda.sormas.ui.externalsurveillanceservice;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.share.ExternalShareInfoCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.DirtyStateComponent;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Provides UI components to integrate with the external surveillance tool gateway
 */
public class ExternalSurveillanceServiceGateway {

	private static final Logger logger = LoggerFactory.getLogger(ExternalSurveillanceServiceGateway.class);

	public static final String EXTERANEL_SURVEILLANCE_TOOL_GATEWAY_LOC = "externalSurvToolGateway";

	private ExternalSurveillanceServiceGateway() {
		//NOOP
	}

	public static ExternalSurveillanceShareComponent addComponentToLayout(
		LayoutWithSidePanel targetLayout,
		DirtyStateComponent editComponent,
		CaseDataDto caze,
		boolean isEditAllowed) {
		return addComponentToLayout(
			targetLayout,
			editComponent,
			I18nProperties.getString(Strings.entityCase),
			I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_confirmSendCase),
			I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_confirmDeleteCase),
			caze.isDontShareWithReportingTool() || !isEditAllowed ? null : () -> {
				FacadeProvider.getExternalSurveillanceToolFacade().sendCases(Collections.singletonList(caze.getUuid()), false);
			},
			isEditAllowed ? () -> {
				FacadeProvider.getExternalSurveillanceToolFacade().deleteCases(Collections.singletonList(caze));
			} : null,
			new ExternalShareInfoCriteria().caze(caze.toReference()));
	}

	public static ExternalSurveillanceShareComponent addComponentToLayout(
		LayoutWithSidePanel targetLayout,
		DirtyStateComponent editComponent,
		EventDto event,
		boolean isEditAllowed) {
		return addComponentToLayout(
			targetLayout,
			editComponent,
			I18nProperties.getString(Strings.entityEvent),
			I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_confirmSendEvent),
			I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_confirmDeleteEvent),
			isEditAllowed ? () -> {
				FacadeProvider.getExternalSurveillanceToolFacade().sendEvents(Collections.singletonList(event.getUuid()), false);
			} : null,
			isEditAllowed ? () -> {
				FacadeProvider.getExternalSurveillanceToolFacade().deleteEvents(Collections.singletonList(event));
			} : null,
			new ExternalShareInfoCriteria().event(event.toReference()));
	}

	private static ExternalSurveillanceShareComponent addComponentToLayout(
		LayoutWithSidePanel targetLayout,
		DirtyStateComponent editComponent,
		String entityName,
		String confirmationText,
		String deletionText,
		@Nullable GatewayCall gatewaySendCall,
		@Nullable GatewayCall gatewayDeleteCall,
		ExternalShareInfoCriteria shareInfoCriteria) {
		if (!FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled()
			|| !UserProvider.getCurrent().hasUserRight(UserRight.EXTERNAL_SURVEILLANCE_SHARE)) {
			return null;
		}

		ExternalSurveillanceShareComponent shareComponent = new ExternalSurveillanceShareComponent(entityName, gatewaySendCall != null ? () -> {
			sendToExternalSurveillanceTool(
				confirmationText,
				gatewaySendCall,
				I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationEntrySent),
				SormasUI::refreshView,
				true);
		} : null, gatewayDeleteCall != null ? () -> {
			deleteInExternalSurveillanceTool(deletionText, gatewayDeleteCall, SormasUI::refreshView);
		} : null, shareInfoCriteria, editComponent);
		targetLayout.addSidePanelComponent(shareComponent, EXTERANEL_SURVEILLANCE_TOOL_GATEWAY_LOC);

		return shareComponent;
	}

	public static void sendCasesToExternalSurveillanceTool(List<String> uuids, Runnable callback, boolean shouldConfirm) {
		sendToExternalSurveillanceTool(I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_confirmSendCases), () -> {
			FacadeProvider.getExternalSurveillanceToolFacade().sendCases(uuids, false);
		}, I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationEntriesSent), callback, shouldConfirm);
	}

	public static void sendEventsToExternalSurveillanceTool(List<String> uuids, Runnable callback, boolean shouldConfirm) {
		sendToExternalSurveillanceTool(I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_confirmSendEvents), () -> {
			FacadeProvider.getExternalSurveillanceToolFacade().sendEvents(uuids, false);
		}, I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationEntriesSent), callback, shouldConfirm);
	}

	private static void sendToExternalSurveillanceTool(
		String confirmationText,
		GatewayCall gatewayCall,
		String successMessage,
		Runnable callback,
		boolean shouldConfirm) {
		Runnable doSend = () -> {
			handleGatewayCall(gatewayCall, successMessage);
			callback.run();
		};
		if (shouldConfirm) {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_confirmSend),
				new Label(confirmationText),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				confirmed -> {
					if (confirmed) {
						doSend.run();
					}
				});
		} else {
			doSend.run();
		}
	}

	private static void handleGatewayCall(GatewayCall gatewayCall, String successMessage) {

		Notification.Type notificationType;
		String notificationMessage;

		try {
			gatewayCall.call();

			notificationType = Notification.Type.HUMANIZED_MESSAGE;
			notificationMessage = successMessage;
		} catch (ExternalSurveillanceToolException e) {
			if (StringUtils.isNotBlank(e.getErrorCode()) && "timeout_anticipated".equals(e.getErrorCode())) {
				notificationType = Notification.Type.WARNING_MESSAGE;
			} else {
				notificationType = Notification.Type.ERROR_MESSAGE;
			}
			notificationMessage = e.getMessage();
		}

		Notification.show(I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_title), notificationMessage, notificationType);
	}

	public static void deleteInExternalSurveillanceTool(String deletionText, GatewayCall gatewayCall, Runnable callback) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_confirmDelete),
			new Label(deletionText),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			confirmed -> {
				if (confirmed) {
					handleGatewayCall(gatewayCall, I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationEntriesDeleted));
					callback.run();
				}
			});
	}

	public interface GatewayCall {

		void call() throws ExternalSurveillanceToolException;
	}
}
