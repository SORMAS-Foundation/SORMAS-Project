package de.symeda.sormas.ui.environment;

import java.util.Date;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.environment.EnvironmentIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.GridHeightResizer;

public class EnvironmentSelectionGrid extends FilteredGrid<EnvironmentIndexDto, EnvironmentCriteria> {

	public EnvironmentSelectionGrid(EnvironmentCriteria criteria) {
		super(EnvironmentIndexDto.class);
		setLazyDataProvider(FacadeProvider.getEnvironmentFacade()::getIndexList, FacadeProvider.getEnvironmentFacade()::count);
		addDataSizeChangeListener(new GridHeightResizer());
		setCriteria(criteria);
		buildGrid();
	}

	private void buildGrid() {
		setSizeFull();
		setSelectionMode(SelectionMode.SINGLE);
		setHeightMode(HeightMode.ROW);

		Language userLanguage = I18nProperties.getUserLanguage();

		setColumns(
			EnvironmentIndexDto.ENVIRONMENT_LOCATION,
			EnvironmentIndexDto.ENVIRONMENT_NAME,
			EnvironmentIndexDto.ENVIRONMENT_MEDIA,
			EnvironmentIndexDto.REPORT_DATE);

		((Column<EnvironmentIndexDto, Date>) getColumn(EnvironmentIndexDto.REPORT_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(userLanguage)));

	}
}
