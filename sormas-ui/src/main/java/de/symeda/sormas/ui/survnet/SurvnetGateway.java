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

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Provides UI components to integrate with the SurvNet gateway
 */
public class SurvnetGateway {

	public static final String SURVNET_GATEWAY_LOC = "survnetGateway";

	private SurvnetGateway() {
		//NOOP
	}

	public static HorizontalLayout addComponentToLayout(CustomLayout targetLayout, SurvnetGatewayType gatewayType, Supplier<List<String>> uuids) {
		if (!FacadeProvider.getSurvnetGatewayFacade().isFeatureEnabled()) {
			return null;
		}

		Label header = new Label(I18nProperties.getCaption(Captions.SurvnetGateway_title));
		header.addStyleName(CssStyles.H3);

		Button button = ButtonHelper.createIconButton(
			Captions.SurvnetGateway_send,
			VaadinIcons.OUTBOX,
			e -> sendToSurvnet(gatewayType, uuids.get()),
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

}
