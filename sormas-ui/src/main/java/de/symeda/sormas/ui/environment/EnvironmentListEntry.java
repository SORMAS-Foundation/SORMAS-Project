package de.symeda.sormas.ui.environment;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.environment.EnvironmentIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

public class EnvironmentListEntry extends SideComponentField {

	private final EnvironmentIndexDto environment;
	private Button editButton;
	private Button unlinkEnvironmentButton;

	public EnvironmentListEntry(EnvironmentIndexDto environment) {
		this.environment = environment;

		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setMargin(false);
		topLayout.setSpacing(false);
		topLayout.setWidth(100, Unit.PERCENTAGE);
		addComponentToField(topLayout);

		VerticalLayout leftColumnLayout = new VerticalLayout();
		leftColumnLayout.setMargin(false);
		leftColumnLayout.setSpacing(false);

		// TOP LEFT
		VerticalLayout topLeftLayout = new VerticalLayout();
		{
			topLeftLayout.setMargin(false);
			topLeftLayout.setSpacing(false);
			Label environmentNameLabel = new Label(DataHelper.toStringNullable(environment.getEnvironmentName()));
			CssStyles.style(environmentNameLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			topLeftLayout.addComponent(environmentNameLabel);
			Label environmentMediaLabel = new Label(
				I18nProperties.getCaption(Captions.eventEnvironmentMedia) + ": " + DataHelper.toStringNullable(environment.getEnvironmentMedia()));
			CssStyles.style(environmentMediaLabel, CssStyles.HSPACE_RIGHT_4);
			topLeftLayout.addComponent(environmentMediaLabel);
			Label investigationStatusLabel = new Label(
				I18nProperties.getCaption(Captions.eventEnvironmentInvestigationStatus) + ": "
					+ DataHelper.toStringNullable(environment.getInvestigationStatus()));
			CssStyles.style(investigationStatusLabel);
			topLeftLayout.addComponent(investigationStatusLabel);
			Label envReportDtLabel = new Label(
				I18nProperties.getCaption(Captions.eventEnvironmentReportDate) + ": "
					+ DataHelper.toStringNullable(DateHelper.formatLocalDate(environment.getReportDate(), I18nProperties.getUserLanguage())));
			CssStyles.style(envReportDtLabel);
			envReportDtLabel.setWidth(100, Unit.PERCENTAGE);
			topLeftLayout.addComponent(envReportDtLabel);
			topLayout.addComponent(topLeftLayout);
			topLayout.setComponentAlignment(topLeftLayout, Alignment.TOP_LEFT);
			// TOP RIGHT
			VerticalLayout topRightLayout = new VerticalLayout();
			topRightLayout.addStyleName(CssStyles.ALIGN_RIGHT);
			topLayout.addComponent(topRightLayout);
			topLayout.setComponentAlignment(topRightLayout, Alignment.TOP_RIGHT);
		}

	}

	public void addEditListener(int rowIndex, Button.ClickListener editClickListener) {
		if (editButton == null) {
			editButton = ButtonHelper.createIconButtonWithCaption(
				"edit-environment-" + rowIndex,
				null,
				VaadinIcons.PENCIL,
				null,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);

			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.TOP_RIGHT);
			setExpandRatio(editButton, 0);
		}

		editButton.addClickListener(editClickListener);
	}

	public void addUnlinkEnvironmentListener(int rowIndex, Button.ClickListener unlinkEventClickListener) {
		if (unlinkEnvironmentButton == null) {
			unlinkEnvironmentButton = ButtonHelper.createIconButtonWithCaption(
				"unlink-environment-" + rowIndex,
				null,
				VaadinIcons.UNLINK,
				null,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);
			unlinkEnvironmentButton.setDescription(I18nProperties.getCaption(Captions.eventUnlinkEnvironment));

			addComponent(unlinkEnvironmentButton);
			setComponentAlignment(unlinkEnvironmentButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(unlinkEnvironmentButton, 0);
		}

		unlinkEnvironmentButton.addClickListener(unlinkEventClickListener);
	}

	public EnvironmentIndexDto getEnvironment() {
		return environment;
	}
}
