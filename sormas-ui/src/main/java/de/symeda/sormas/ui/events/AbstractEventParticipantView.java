package de.symeda.sormas.ui.events;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.externalmessage.ExternalMessagesView;
import de.symeda.sormas.ui.utils.AbstractEditAllowedDetailView;

public abstract class AbstractEventParticipantView extends AbstractEditAllowedDetailView<EventParticipantReferenceDto> {

	public static final String ROOT_VIEW_NAME = EventParticipantsView.VIEW_NAME;

	protected AbstractEventParticipantView(String viewName) {
		super(viewName);
	}

	@Override
	protected CoreFacade getEditPermissionFacade() {
		return FacadeProvider.getEventParticipantFacade();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {

		if (!findReferenceByParams(params)) {
			return;
		}

		EventParticipantDto eventParticipantDto = FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(getReference().getUuid());

		menu.removeAllViews();
		menu.addView(
			EventParticipantsView.VIEW_NAME,
			I18nProperties.getCaption(Captions.eventEventParticipants),
			eventParticipantDto.getEvent().getUuid(),
			true);

		if (UiUtil.permitted(FeatureType.EXTERNAL_MESSAGES, UserRight.EXTERNAL_MESSAGE_VIEW)
			&& FacadeProvider.getExternalMessageFacade().existsExternalMessageForEntity(getReference())) {
			menu.addView(ExternalMessagesView.VIEW_NAME, I18nProperties.getCaption(Captions.externalMessagesList));
		}

		menu.addView(EventParticipantDataView.VIEW_NAME, I18nProperties.getCaption(EventParticipantDto.I18N_PREFIX), params);
		menu.addView(
			EventParticipantPersonView.VIEW_NAME,
			I18nProperties.getPrefixCaption(EventParticipantDto.I18N_PREFIX, EventParticipantDto.PERSON),
			params);

		setMainHeaderComponent(ControllerProvider.getEventParticipantController().getEventParticipantViewTitleLayout(eventParticipantDto));
	}

	@Override
	protected EventParticipantReferenceDto getReferenceByUuid(String uuid) {

		final EventParticipantReferenceDto reference;
		if (FacadeProvider.getEventParticipantFacade().exists(uuid)) {
			reference = FacadeProvider.getEventParticipantFacade().getReferenceByUuid(uuid);
		} else {
			reference = null;
		}
		return reference;
	}

	@Override
	protected String getRootViewName() {
		return ROOT_VIEW_NAME;
	}

	public EventParticipantReferenceDto getEventParticipantRef() {
		return getReference();
	}

}
