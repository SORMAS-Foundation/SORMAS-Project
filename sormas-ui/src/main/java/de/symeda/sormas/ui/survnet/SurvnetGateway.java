package de.symeda.sormas.ui.survnet;

import java.util.List;
import java.util.function.Supplier;

import com.vaadin.icons.VaadinIcons;
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
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.LayoutUtil.FluidColumn;

/**
 * Provides UI components to integrate with the SurvNet gateway
 */
public class SurvnetGateway {

	private static final String SURVNET_GATEWAY_LOC = "survnetGateway";

	private SurvnetGateway() {
		//NOOP
	}

	public static FluidColumn layoutFragment() {
		//TODO only add it if the feature is active? Then LayoutUtil.fluidRow would have to ignore null values.
		return LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SURVNET_GATEWAY_LOC);
	}

	public static void addComponentToLayout(CustomLayout targetLayout, Supplier<List<String>> caseUuids) {
		if (!FacadeProvider.getSurvnetGatewayFacade().isFeatureEnabled()) {
			return;
		}

		Label header = new Label(I18nProperties.getCaption(Captions.SurvnetGateway_title));
		header.addStyleName(CssStyles.H3);

		Button button = ButtonHelper
			.createIconButton(Captions.SurvnetGateway_send, VaadinIcons.OUTBOX, e -> sendToSurvnet(caseUuids.get()), ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout l = new HorizontalLayout(header, button);
		l.setExpandRatio(button, 1);
		l.setComponentAlignment(header, Alignment.MIDDLE_LEFT);
		l.setComponentAlignment(button, Alignment.MIDDLE_RIGHT);
		l.setSizeFull();

		l.addStyleNames(CssStyles.SIDE_COMPONENT);
		targetLayout.addComponent(l, SURVNET_GATEWAY_LOC);
	}

	private static void sendToSurvnet(List<String> caseUuids) {

		int statusCode = FacadeProvider.getSurvnetGatewayFacade().sendCases(caseUuids);

		Notification.Type type;
		String message;

		switch (statusCode) {
		case 200://HttpStatus.OK:
		case 204://HttpStatus.SC_NO_CONTENT:
			type = Notification.Type.HUMANIZED_MESSAGE;
			message = I18nProperties.getString(Strings.SurvnetGateway_notificationEntrySent);
			break;
		case 400://HttpStatus.SC_BAD_REQUEST
			type = Notification.Type.ERROR_MESSAGE;
			message = I18nProperties.getString(Strings.SurvnetGateway_notificationEntryNotSent);
			break;
		default://HttpStatus.SC_BAD_REQUEST
			type = Notification.Type.ERROR_MESSAGE;
			message = I18nProperties.getString(Strings.SurvnetGateway_notificationErrorSending);
		}

		Notification.show(I18nProperties.getCaption(Captions.SurvnetGateway_title), message, type);
	}

}
