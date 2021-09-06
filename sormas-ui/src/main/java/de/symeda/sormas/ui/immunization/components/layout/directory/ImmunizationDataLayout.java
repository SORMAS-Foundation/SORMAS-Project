package de.symeda.sormas.ui.immunization.components.layout.directory;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.ui.immunization.components.grid.ImmunizationGrid;
import de.symeda.sormas.ui.utils.CssStyles;

public class ImmunizationDataLayout extends VerticalLayout {

	private final Label noDiseaseInfoLabel;
	private final ImmunizationGrid grid;

	public ImmunizationDataLayout(ImmunizationCriteria criteria) {
		noDiseaseInfoLabel = new Label(I18nProperties.getString(Strings.infoNoDiseaseSelected));
		noDiseaseInfoLabel.addStyleNames(CssStyles.LAYOUT_MINIMAL, CssStyles.H3, CssStyles.VSPACE_TOP_0, CssStyles.ALIGN_CENTER);
		addComponent(noDiseaseInfoLabel);

		grid = new ImmunizationGrid(criteria);
		addComponent(grid);

		setMargin(false);
		setSpacing(false);
		setSizeFull();
		setExpandRatio(grid, 1);
	}

	public void toggleView(boolean shouldShowGrid) {
		grid.setVisible(shouldShowGrid);
		noDiseaseInfoLabel.setVisible(!shouldShowGrid);
	}

	public void refreshGrid() {
		grid.reload();
	}
}
