package de.symeda.sormas.ui.travelentry;

import java.util.Date;

import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.campaign.campaigns.CampaignsView;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class TravelEntryGrid extends FilteredGrid<TravelEntryIndexDto, TravelEntryCriteria> {

	public TravelEntryGrid(TravelEntryCriteria criteria) {
		super(TravelEntryIndexDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(CampaignsView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode()) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		Column<TravelEntryIndexDto, String> deleteColumn = addColumn(entry -> {
			if (entry.getDeletionReason() != null) {
				return entry.getDeletionReason() + (entry.getOtherDeletionReason() != null ? ": " + entry.getOtherDeletionReason() : "");
			} else {
				return "-";
			}
		});
		deleteColumn.setId(DELETE_REASON_COLUMN);
		deleteColumn.setSortable(false);
		deleteColumn.setCaption(I18nProperties.getCaption(Captions.deletionReason));

		setCriteria(criteria);
		initColumns();
		addItemClickListener(
			new ShowDetailsListener<>(
				TravelEntryIndexDto.UUID,
				e -> ControllerProvider.getTravelEntryController().navigateToTravelEntry(e.getUuid())));
	}

	private void initColumns() {
		setColumns(
			TravelEntryIndexDto.UUID,
			TravelEntryIndexDto.EXTERNAL_ID,
			TravelEntryIndexDto.PERSON_FIRST_NAME,
			TravelEntryIndexDto.PERSON_LAST_NAME,
			TravelEntryIndexDto.HOME_DISTRICT_NAME,
			TravelEntryIndexDto.POINT_OF_ENTRY_NAME,
			TravelEntryIndexDto.RECOVERED,
			TravelEntryIndexDto.VACCINATED,
			TravelEntryIndexDto.TESTED_NEGATIVE,
			TravelEntryIndexDto.QUARANTINE_TO,
			DELETE_REASON_COLUMN);

		((Column<TravelEntryIndexDto, String>) getColumn(TravelEntryIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<TravelEntryIndexDto, Boolean>) getColumn(TravelEntryIndexDto.RECOVERED)).setRenderer(new BooleanRenderer());
		((Column<TravelEntryIndexDto, Boolean>) getColumn(TravelEntryIndexDto.VACCINATED)).setRenderer(new BooleanRenderer());
		((Column<TravelEntryIndexDto, Boolean>) getColumn(TravelEntryIndexDto.TESTED_NEGATIVE)).setRenderer(new BooleanRenderer());
		((Column<TravelEntryIndexDto, Date>) getColumn(TravelEntryIndexDto.QUARANTINE_TO))
			.setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));

		for (Column<TravelEntryIndexDto, ?> column : getColumns()) {
			column.setCaption(
				I18nProperties
					.findPrefixCaptionWithDefault(column.getId(), column.getCaption(), TravelEntryIndexDto.I18N_PREFIX, PersonDto.I18N_PREFIX));
			column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(getBeanType(), column.getId()));
		}
	}

	public void setLazyDataProvider() {

		setLazyDataProvider(FacadeProvider.getTravelEntryFacade()::getIndexList, FacadeProvider.getTravelEntryFacade()::count);
	}

	public void setEagerDataProvider() {
		setEagerDataProvider(FacadeProvider.getTravelEntryFacade()::getIndexList);
	}

	public void reload() {
		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		ViewConfiguration viewConfiguration = ViewModelProviders.of(TravelEntriesView.class).get(ViewConfiguration.class);
		if (viewConfiguration.isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}
}
