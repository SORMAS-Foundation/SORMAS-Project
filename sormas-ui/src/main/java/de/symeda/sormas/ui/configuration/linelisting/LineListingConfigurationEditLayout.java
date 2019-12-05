package de.symeda.sormas.ui.configuration.linelisting;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private Set<FeatureConfigurationIndexDto> changedConfigurations;

	private Runnable saveCallback;
	private Runnable discardCallback;

	public LineListingConfigurationEditLayout(List<FeatureConfigurationIndexDto> configurations, Disease disease, String regionName) {
		this.configurations = configurations;
		this.changedConfigurations = new HashSet<>();
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

		btnEnableAll = new Button(I18nProperties.getCaption(Captions.lineListingEnableAll));
		CssStyles.style(btnEnableAll, ValoTheme.BUTTON_PRIMARY);
		btnEnableAll.addClickListener(e -> {
			grid.enableAll();
		});
		controlLayout.addComponent(btnEnableAll);

		btnDisableAll = new Button(I18nProperties.getCaption(Captions.lineListingDisableAllShort));
		CssStyles.style(btnDisableAll, ValoTheme.BUTTON_PRIMARY, CssStyles.HSPACE_RIGHT_2);
		btnDisableAll.addClickListener(e -> {
			grid.disableAll();
		});
		controlLayout.addComponent(btnDisableAll);

		dfEndDate = new DateField();
		dfEndDate.setPlaceholder(I18nProperties.getCaption(Captions.lineListingEndDate));
		dfEndDate.setRangeStart(LocalDate.now());
		controlLayout.addComponent(dfEndDate);

		btnSetEndDateForAll = new Button(I18nProperties.getCaption(Captions.lineListingSetEndDateForAll));
		CssStyles.style(btnSetEndDateForAll, ValoTheme.BUTTON_PRIMARY);
		btnSetEndDateForAll.addClickListener(e -> {
			grid.setEndDateForAll(dfEndDate.getValue());
		});
		controlLayout.addComponent(btnSetEndDateForAll);

		addComponent(controlLayout);

		grid = new LineListingConfigurationsGrid(configurations, changedConfigurations, regionName == null);
		grid.setWidth(100, Unit.PERCENTAGE);
		addComponent(grid);

		HorizontalLayout buttonLayout = buildButtonLayout();
		addComponent(buttonLayout);
		setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT);
	}

	private HorizontalLayout buildButtonLayout() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setMargin(false);

		Button btnDiscard = new Button(I18nProperties.getCaption(Captions.actionDiscardChanges));
		btnDiscard.addClickListener(e -> {
			if (discardCallback != null) {
				discardCallback.run();
			}
		});
		buttonLayout.addComponent(btnDiscard);

		Button btnSave = new Button(I18nProperties.getCaption(Captions.actionSaveChanges));
		btnSave.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btnSave.addClickListener(e -> {
			if (grid.validateDates()) {
				for (FeatureConfigurationIndexDto changedConfig : changedConfigurations) {
					FacadeProvider.getFeatureConfigurationFacade().saveFeatureConfiguration(changedConfig, FeatureType.LINE_LISTING);
				}
				if (saveCallback != null) {
					saveCallback.run();
				}
			} else {
				Notification.show(null, I18nProperties.getString(Strings.messageInvalidDatesLineListing), Type.ERROR_MESSAGE);
			}
		});
		buttonLayout.addComponent(btnSave);

		return buttonLayout;
	}



}
