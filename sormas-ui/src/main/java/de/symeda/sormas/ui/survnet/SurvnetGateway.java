package de.symeda.sormas.ui.survnet;

import java.util.List;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DirtyStateComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import org.apache.commons.collections.CollectionUtils;

/**
 * Provides UI components to integrate with the SurvNet gateway
 */
public class SurvnetGateway {

	public static final String SURVNET_GATEWAY_LOC = "survnetGateway";

	private SurvnetGateway() {
		//NOOP
	}

	public static HorizontalLayout addComponentToLayout(CustomLayout targetLayout, DirtyStateComponent editComponent, SurvnetGatewayType gatewayType, Supplier<List<String>> uuids) {
		if (!FacadeProvider.getSurvnetGatewayFacade().isFeatureEnabled()) {
			return null;
		}

		Label header = new Label(I18nProperties.getCaption(Captions.SurvnetGateway_title));
		header.addStyleName(CssStyles.H3);

		Button button = ButtonHelper.createIconButton(
			Captions.SurvnetGateway_send,
			VaadinIcons.OUTBOX,
			e -> onSendButtonClick(editComponent, gatewayType, uuids),
			ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout layout = new HorizontalLayout(header, button);
		layout.setExpandRatio(button, 1);
		layout.setComponentAlignment(header, Alignment.MIDDLE_LEFT);
		layout.setComponentAlignment(button, Alignment.MIDDLE_RIGHT);
		layout.setWidth(100, Unit.PERCENTAGE);

		layout.addStyleNames(CssStyles.SIDE_COMPONENT);
		targetLayout.addComponent(layout, SURVNET_GATEWAY_LOC);

		return layout;
	}

	private static void onSendButtonClick(DirtyStateComponent editComponent, SurvnetGatewayType gatewayType, Supplier<List<String>> uuids) {

		int numberOfEntities = CollectionUtils.size(uuids.get());

		String entityString;
		if (gatewayType == SurvnetGatewayType.CASES &&  numberOfEntities == 1) {
			entityString = I18nProperties.getString(Strings.entityCase).toLowerCase();
		} else if (gatewayType == SurvnetGatewayType.CASES) {
			entityString = I18nProperties.getString(Strings.entityCases).toLowerCase();
		} else if (gatewayType == SurvnetGatewayType.EVENTS && numberOfEntities == 1) {
			entityString = I18nProperties.getString(Strings.entityEvent).toLowerCase();
		} else {
			entityString = I18nProperties.getString(Strings.entityEvents).toLowerCase();
		}

		if (editComponent.isDirty()) {
			VaadinUiUtil.showSimplePopupWindow(
					I18nProperties.getCaption(Captions.SurvnetGateway_unableToSend),
					String.format(I18nProperties.getString(Strings.SurvnetGateway_unableToSend), entityString)
					);
		} else {
			VaadinUiUtil.showConfirmationPopup(
					I18nProperties.getCaption(Captions.SurvnetGateway_confirmSend),
					new Label(String.format(I18nProperties.getString(Strings.SurvnetGateway_confirmSend), entityString)),
					I18nProperties.getString(Strings.yes),
					I18nProperties.getString(Strings.no),
					640,
					confirmed -> {
						if (confirmed) {
							sendToSurvnet(gatewayType, uuids.get());
							SormasUI.refreshView();
						}
					});
		}
	}

	public static void sendToSurvnet(SurvnetGatewayType gatewayType, List<String> uuids) {

		int statusCode;

		switch (gatewayType) {
		case CASES:
			statusCode = FacadeProvider.getSurvnetGatewayFacade().sendCases(uuids);
			break;
		case EVENTS:
			statusCode = FacadeProvider.getSurvnetGatewayFacade().sendEvents(uuids);
			break;
		default:
			throw new IllegalArgumentException(gatewayType.toString());
		}

		Notification.Type type;
		String message;

		switch (statusCode) {
		case HttpServletResponse.SC_OK:
		case HttpServletResponse.SC_NO_CONTENT:
			type = Notification.Type.HUMANIZED_MESSAGE;
			message = I18nProperties.getString(Strings.SurvnetGateway_notificationEntrySent);
			break;
		case HttpServletResponse.SC_BAD_REQUEST:
			type = Notification.Type.ERROR_MESSAGE;
			message = I18nProperties.getString(Strings.SurvnetGateway_notificationEntryNotSent);
			break;
		default:
			type = Notification.Type.ERROR_MESSAGE;
			message = I18nProperties.getString(Strings.SurvnetGateway_notificationErrorSending);
		}

		Notification.show(I18nProperties.getCaption(Captions.SurvnetGateway_title), message, type);
	}

	public static <T extends EntityDto> void deleteInSurvnet(SurvnetGatewayType gatewayType, List<T> entities) {
		int statusCode;

		switch (gatewayType) {
			case CASES:
				statusCode = FacadeProvider.getSurvnetGatewayFacade().deleteCases((List<CaseDataDto>) entities);
				break;
			case EVENTS:
				statusCode = FacadeProvider.getSurvnetGatewayFacade().deleteEvents((List<EventDto>) entities);
				break;
			default:
				throw new IllegalArgumentException(gatewayType.toString());
		}

		switch (statusCode) {
			case HttpServletResponse.SC_OK:
			case HttpServletResponse.SC_NO_CONTENT:
				return;
			case HttpServletResponse.SC_BAD_REQUEST:
				throw new RuntimeException("Invalid request for deleting " + gatewayType);
			default:
				throw new RuntimeException("Unknown exception when deleting " + gatewayType);
		}
	}

}
