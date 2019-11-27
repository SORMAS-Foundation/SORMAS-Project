package de.symeda.sormas.ui.configuration.linelisting;

import java.util.List;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class LineListingConfigurationEditLayout extends VerticalLayout {

	private Button btnEnableAll;
	private Button btnDisableAll;
	private Button btnSetEndDateForAll;
	private DateField dfEndDate;
	
	private HorizontalLayout controlLayout;
	private LineListingConfigurationsGrid grid;
	
	private final List<FeatureConfigurationIndexDto> configurations;
	
	public LineListingConfigurationEditLayout(List<FeatureConfigurationIndexDto> configurations) {
		this.configurations = configurations;
		buildLayout();
	}
	
	private void buildLayout() {
		controlLayout = new HorizontalLayout();
		controlLayout.setMargin(false);
		
		btnEnableAll = new Button(I18nProperties.getCaption(Captions.lineListingEnableAll));
		CssStyles.style(btnEnableAll, ValoTheme.BUTTON_PRIMARY);
		controlLayout.addComponent(btnEnableAll);
		
		btnDisableAll = new Button(I18nProperties.getCaption(Captions.lineListingDisableAllShort));
		CssStyles.style(btnDisableAll, ValoTheme.BUTTON_PRIMARY, CssStyles.HSPACE_RIGHT_2);
		controlLayout.addComponent(btnDisableAll);
		
		dfEndDate = new DateField();
		dfEndDate.setPlaceholder(I18nProperties.getCaption(Captions.lineListingEndDate));
		controlLayout.addComponent(dfEndDate);
		
		btnSetEndDateForAll = new Button(I18nProperties.getCaption(Captions.lineListingSetEndDateForAll));
		CssStyles.style(btnSetEndDateForAll, ValoTheme.BUTTON_PRIMARY);
		controlLayout.addComponent(btnSetEndDateForAll);
		
		addComponent(controlLayout);
		
		grid = new LineListingConfigurationsGrid(configurations);
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
//		btnDiscard.addClickListener(e -> discardCallback.run());
		buttonLayout.addComponent(btnDiscard);

		Button btnSave = new Button(I18nProperties.getCaption(Captions.actionSaveChanges));
		btnSave.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btnSave.addClickListener(e -> {
//			if (validate()) {
//				updateExportConfiguration();
//				resultCallback.accept(exportConfiguration);
//			}
		});
		buttonLayout.addComponent(btnSave);

		return buttonLayout;
	}
	
}
