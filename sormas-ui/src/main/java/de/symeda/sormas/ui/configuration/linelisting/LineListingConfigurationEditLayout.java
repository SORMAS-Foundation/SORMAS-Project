package de.symeda.sormas.ui.configuration.linelisting;

import java.time.LocalDate;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class LineListingConfigurationEditLayout extends VerticalLayout {

	private Button btnEnableAll;
	private Button btnDisableAll;
	private Button btnSetEndDateForAll;
	private DateField dfEndDate;

	private HorizontalLayout controlLayout;
	private LineListingConfigurationsGrid grid;

	private Disease disease;
	private String regionName;
	private List<FeatureConfigurationIndexDto> configurations;

	private Runnable saveCallback;
	private Runnable discardCallback;

	public LineListingConfigurationEditLayout(List<FeatureConfigurationIndexDto> configurations, Disease disease, String regionName) {
		this.configurations = configurations;
		this.disease = disease;
		this.regionName = regionName;
		buildLayout();
	}

	public void setSaveCallback(Runnable saveCallback) {
		this.saveCallback = saveCallback;
	}

	public void setDiscardCallback(Runnable discardCallback) {
		this.discardCallback = discardCallback;
	}

	private void buildLayout() {
		Label lblInfo = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + String.format(I18nProperties.getString(regionName != null ? Strings.infoLineListingConfigurationRegionEdit : Strings.infoLineListingConfigurationNationEdit), 
				disease.toString(), regionName), ContentMode.HTML);
		CssStyles.style(lblInfo, CssStyles.VSPACE_4);
		addComponent(lblInfo);
		
		controlLayout = new HorizontalLayout();
		controlLayout.setMargin(false);

		btnEnableAll = ButtonHelper.createButton(Captions.lineListingEnableAll, e -> {
			grid.enableAll();
		}, ValoTheme.BUTTON_PRIMARY);

		controlLayout.addComponent(btnEnableAll);

		btnDisableAll = ButtonHelper.createButton(Captions.lineListingDisableAllShort, e -> {
			grid.disableAll();
		}, ValoTheme.BUTTON_PRIMARY, CssStyles.HSPACE_RIGHT_2);

		controlLayout.addComponent(btnDisableAll);

		dfEndDate = new DateField();
		dfEndDate.setId("endDate");
		dfEndDate.setPlaceholder(I18nProperties.getCaption(Captions.lineListingEndDate));
		dfEndDate.setRangeStart(LocalDate.now());
		controlLayout.addComponent(dfEndDate);

		btnSetEndDateForAll = ButtonHelper.createButton(Captions.lineListingSetEndDateForAll, e -> {
			grid.setEndDateForAll(dfEndDate.getValue());
		}, ValoTheme.BUTTON_PRIMARY);

		controlLayout.addComponent(btnSetEndDateForAll);

		addComponent(controlLayout);

		grid = new LineListingConfigurationsGrid(configurations, regionName == null);
		grid.setWidth(100, Unit.PERCENTAGE);
		addComponent(grid);

		HorizontalLayout buttonLayout = buildButtonLayout();
		addComponent(buttonLayout);
		setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT);
	}

	private HorizontalLayout buildButtonLayout() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setMargin(false);

		Button btnDiscard = ButtonHelper.createButton(Captions.actionDiscardChanges, e -> {
			if (discardCallback != null) {
				discardCallback.run();
			}
		});

		buttonLayout.addComponent(btnDiscard);

		Button btnSave = ButtonHelper.createButton(Captions.actionSaveChanges, e -> {
			if (grid.validateDates()) {
				FacadeProvider.getFeatureConfigurationFacade().saveFeatureConfigurations(grid.getChangedConfigurations(), FeatureType.LINE_LISTING);
				if (saveCallback != null) {
					saveCallback.run();
				}
			} else {
				Notification.show(null, I18nProperties.getString(Strings.messageInvalidDatesLineListing), Type.ERROR_MESSAGE);
			}
		}, ValoTheme.BUTTON_PRIMARY);

		buttonLayout.addComponent(btnSave);

		return buttonLayout;
	}



}
