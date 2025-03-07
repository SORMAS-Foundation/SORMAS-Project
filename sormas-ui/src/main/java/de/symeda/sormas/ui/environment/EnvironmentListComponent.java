package de.symeda.sormas.ui.environment;

import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class EnvironmentListComponent extends VerticalLayout {

	private final Consumer<Runnable> actionCallback;

	public EnvironmentListComponent(EventReferenceDto eventReferenceDto, boolean isEditAllowed, Consumer<Runnable> actionCallback) {
		this.actionCallback = actionCallback;
		createEnvironmentListComponent(
			new EnvironmentList(eventReferenceDto, isEditAllowed),
			I18nProperties.getString(Strings.entityEnvironment),
			false,
			() -> {
				EnvironmentCriteria criteria = new EnvironmentCriteria();
				criteria.setEvent(eventReferenceDto);
				//check if there are active environments in the database
				long events = FacadeProvider.getEnvironmentFacade().count(criteria);
				EnvironmentReferenceDto dto = new EnvironmentReferenceDto();
				dto.setEvent(eventReferenceDto);
				if (events > 0) {
					ControllerProvider.getEnvironmentController().selectOrCreateEnvironment(dto);
				} else {
					ControllerProvider.getEnvironmentController().create(dto);
				}
			},
			isEditAllowed);
	}

	private void createEnvironmentListComponent(
		EnvironmentList environmentList,
		String heading,
		boolean bottomCreateButton,
		Runnable linkEnvironmentCallback,
		boolean isEditAllowed) {

		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);
		Button createButton;

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		addComponent(environmentList);
		environmentList.reload();

		Label environmentLabel = new Label(heading);
		environmentLabel.addStyleName(CssStyles.H3);
		componentHeader.addComponent(environmentLabel);

		if (UiUtil.permitted(isEditAllowed, UserRight.ENVIRONMENT_CREATE)) {
			createButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.linkEnvironment));
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(e -> actionCallback.accept(linkEnvironmentCallback));
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

			if (bottomCreateButton) {
				HorizontalLayout buttonLayout = new HorizontalLayout();
				buttonLayout.setSpacing(true);
				buttonLayout.setMargin(false);
				buttonLayout.setWidth(100, Unit.PERCENTAGE);
				buttonLayout.addComponent(createButton);
				CssStyles.style(buttonLayout, CssStyles.VSPACE_TOP_3);
				addComponent(buttonLayout);
			} else {
				componentHeader.addComponent(createButton);
				componentHeader.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
			}
		}
	}
}
