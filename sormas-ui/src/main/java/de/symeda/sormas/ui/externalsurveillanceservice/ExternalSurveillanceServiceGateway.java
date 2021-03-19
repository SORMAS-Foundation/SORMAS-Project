package de.symeda.sormas.ui.externalsurveillanceservice;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventReferenceDto;
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
		CaseReferenceDto caze) {
		return addComponentToLayout(targetLayout, editComponent, I18nProperties.getString(Strings.entityCase), () -> {
			FacadeProvider.getExternalSurveillanceToolFacade().sendCases(Collections.singletonList(caze.getUuid()));
		}, new ExternalShareInfoCriteria().caze(caze));
	}

	public static ExternalSurveillanceShareComponent addComponentToLayout(
		CustomLayout targetLayout,
		DirtyStateComponent editComponent,
		EventReferenceDto event) {
		return addComponentToLayout(targetLayout, editComponent, I18nProperties.getString(Strings.entityEvent), () -> {
			FacadeProvider.getExternalSurveillanceToolFacade().sendEvents(Collections.singletonList(event.getUuid()));
		}, new ExternalShareInfoCriteria().event(event));
	}

	private static ExternalSurveillanceShareComponent addComponentToLayout(
		CustomLayout targetLayout,
		DirtyStateComponent editComponent,
		String entityString,
		GatewayCall gatewayCall,
		ExternalShareInfoCriteria shareInfoCriteria) {
		if (!FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled()) {
			return null;
		}

		ExternalSurveillanceShareComponent shareComponent = new ExternalSurveillanceShareComponent(entityString, () -> {
			sendToExternalSurveillanceTool(
				entityString,
				gatewayCall,
				I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationEntrySent));
		}, shareInfoCriteria, editComponent);
		targetLayout.addComponent(shareComponent, EXTERANEL_SURVEILLANCE_TOOL_GATEWAY_LOC);

		return shareComponent;
	}

	public static void sendCasesToExternalSurveillanceTool(List<String> uuids) {
		sendToExternalSurveillanceTool(I18nProperties.getString(Strings.entityCases), () -> {
			FacadeProvider.getExternalSurveillanceToolFacade().sendCases(uuids);
		}, I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationEntriesSent));
	}

	public static void sendEventsToExternalSurveillanceTool(List<String> uuids) {
		sendToExternalSurveillanceTool(I18nProperties.getString(Strings.entityEvents), () -> {
			FacadeProvider.getExternalSurveillanceToolFacade().sendEvents(uuids);
		}, I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationEntriesSent));
	}

	private static void sendToExternalSurveillanceTool(String entityString, GatewayCall gatewayCall, String successMessage) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_confirmSend),
			new Label(String.format(I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_confirmSend), entityString.toLowerCase())),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			confirmed -> {
				if (confirmed) {
					handleGatewayCall(gatewayCall, successMessage);
					SormasUI.refreshView();
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

	public static <T extends EntityDto> boolean deleteInExternalSurveillanceTool(ExternalSurveillanceToolGatewayType gatewayType, List<T> entities) {
		int statusCode;

		switch (gatewayType) {
		case CASES:
			statusCode = FacadeProvider.getExternalSurveillanceToolFacade().deleteCases((List<CaseDataDto>) entities);
			break;
		case EVENTS:
			statusCode = FacadeProvider.getExternalSurveillanceToolFacade().deleteEvents((List<EventDto>) entities);
			break;
		default:
			throw new IllegalArgumentException(gatewayType.toString());
		}

		switch (statusCode) {
		case HttpServletResponse.SC_OK:
		case HttpServletResponse.SC_NO_CONTENT:
			return true;
		case HttpServletResponse.SC_BAD_REQUEST:
		default:
			logger.warn("Cannot delete entities in the reporting tool due to {} response", statusCode);
			return false;
		}
	}

	public interface GatewayCall {

		void call() throws ExternalSurveillanceToolException;
	}
}
