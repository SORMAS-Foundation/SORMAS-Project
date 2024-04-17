package de.symeda.sormas.ui.configuration.infrastructure;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryCriteria;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class PointsOfEntryGrid extends FilteredGrid<PointOfEntryDto, PointOfEntryCriteria> {

	private static final long serialVersionUID = -650914769265620769L;

	public PointsOfEntryGrid(PointOfEntryCriteria criteria) {
		super(PointOfEntryDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(PointsOfEntryView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		setColumns(
			PointOfEntryDto.NAME,
			PointOfEntryDto.POINT_OF_ENTRY_TYPE,
			PointOfEntryDto.REGION,
			PointOfEntryDto.DISTRICT,
			PointOfEntryDto.LATITUDE,
			PointOfEntryDto.LONGITUDE,
			PointOfEntryDto.EXTERNAL_ID,
			PointOfEntryDto.ACTIVE);

		if (UiUtil.permitted(UserRight.INFRASTRUCTURE_EDIT)) {
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editPointOfEntry(e.getUuid()));
		}

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}

		getColumn(PointOfEntryDto.ACTIVE).setRenderer(new BooleanRenderer());
	}

	public void reload() {
		if (ViewModelProviders.of(PointsOfEntryView.class).get(ViewConfiguration.class).isInEagerMode()) {
			setEagerDataProvider();
		}
		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {
		setLazyDataProvider(FacadeProvider.getPointOfEntryFacade()::getIndexList, FacadeProvider.getPointOfEntryFacade()::count);
	}

	public void setEagerDataProvider() {
		setEagerDataProvider(FacadeProvider.getPointOfEntryFacade()::getIndexList);
	}
}
