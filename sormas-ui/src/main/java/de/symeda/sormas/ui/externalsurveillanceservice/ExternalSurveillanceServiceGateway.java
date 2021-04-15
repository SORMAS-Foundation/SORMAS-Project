package de.symeda.sormas.ui.externalsurveillanceservice;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.CustomLayout;
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
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.DirtyStateComponent;
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
		CustomLayout targetLayout,
		DirtyStateComponent editComponent,
		CaseDataDto caze) {
		return addComponentToLayout(targetLayout, editComponent, I18nProperties.getString(Strings.entityCase), () -> {
			FacadeProvider.getExternalSurveillanceToolFacade().sendCases(Collections.singletonList(caze.getUuid()));
		}, () -> {
			FacadeProvider.getExternalSurveillanceToolFacade().deleteCases(Collections.singletonList(caze));
		}, new ExternalShareInfoCriteria().caze(caze.toReference()));
	}

	public static ExternalSurveillanceShareComponent addComponentToLayout(
		CustomLayout targetLayout,
		DirtyStateComponent editComponent,
		EventDto event) {
		return addComponentToLayout(targetLayout, editComponent, I18nProperties.getString(Strings.entityEvent), () -> {
			FacadeProvider.getExternalSurveillanceToolFacade().sendEvents(Collections.singletonList(event.getUuid()));
		}, () -> {
			FacadeProvider.getExternalSurveillanceToolFacade().deleteEvents(Collections.singletonList(event));
		}, new ExternalShareInfoCriteria().event(event.toReference()));
	}

	private static ExternalSurveillanceShareComponent addComponentToLayout(
		CustomLayout targetLayout,
		DirtyStateComponent editComponent,
		String entityName,
		GatewayCall gatewaySendCall,
		GatewayCall gatewayDeleteCall,
		ExternalShareInfoCriteria shareInfoCriteria) {
		if (!FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled()) {
			return null;
		}

		ExternalSurveillanceShareComponent shareComponent = new ExternalSurveillanceShareComponent(entityName, () -> {
			sendToExternalSurveillanceTool(
				entityName,
				gatewaySendCall,
				I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationEntrySent),
				SormasUI::refreshView);
		}, () -> {
			deleteInExternalSurveillanceTool(entityName, gatewayDeleteCall, SormasUI::refreshView);
		}, shareInfoCriteria, editComponent);
		targetLayout.addComponent(shareComponent, EXTERANEL_SURVEILLANCE_TOOL_GATEWAY_LOC);

		return shareComponent;
	}

	public static void sendCasesToExternalSurveillanceTool(List<String> uuids, Runnable callback) {
		sendToExternalSurveillanceTool(I18nProperties.getString(Strings.entityCases), () -> {
			FacadeProvider.getExternalSurveillanceToolFacade().sendCases(uuids);
		}, I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationEntriesSent), callback);
	}

	public static void sendEventsToExternalSurveillanceTool(List<String> uuids, Runnable callback) {
		sendToExternalSurveillanceTool(I18nProperties.getString(Strings.entityEvents), () -> {
			FacadeProvider.getExternalSurveillanceToolFacade().sendEvents(uuids);
		}, I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationEntriesSent), callback);
	}

	private static void sendToExternalSurveillanceTool(String entityString, GatewayCall gatewayCall, String successMessage, Runnable callback) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_confirmSend),
			new Label(String.format(I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_confirmSend), entityString.toLowerCase())),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			confirmed -> {
				if (confirmed) {
					handleGatewayCall(gatewayCall, successMessage);
					callback.run();
				}
			});
	}

	private static void handleGatewayCall(GatewayCall gatewayCall, String successMessage) {

		Notification.Type notificationType;
		String notificationMessage;

		try {
			gatewayCall.call();

			notificationType = Notification.Type.HUMANIZED_MESSAGE;
			notificationMessage = successMessage;
		} catch (ExternalSurveillanceToolException e) {
			notificationType = Notification.Type.ERROR_MESSAGE;
			notificationMessage = I18nProperties.getString(e.getMessage());
		}

		Notification.show(I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_title), notificationMessage, notificationType);
	}

	public static void deleteInExternalSurveillanceTool(String entityString, GatewayCall gatewayCall, Runnable callback) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_confirmDelete),
			new Label(String.format(I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_confirmDelete), entityString.toLowerCase())),
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
