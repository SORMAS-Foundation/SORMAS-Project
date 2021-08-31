package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.continent.ContinentCriteria;
import de.symeda.sormas.api.infrastructure.continent.ContinentIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class ContinentsGrid extends FilteredGrid<ContinentIndexDto, ContinentCriteria> {

	List<ContinentIndexDto> allContinents;

	public ContinentsGrid(ContinentCriteria criteria) {
		super(ContinentIndexDto.class);

		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(ContinentsView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		super.setCriteria(criteria, true);
		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setSelectionMode(SelectionMode.MULTI);
		} else {
			setSelectionMode(SelectionMode.NONE);
		}

		setColumns(ContinentIndexDto.DISPLAY_NAME, ContinentIndexDto.EXTERNAL_ID, ContinentIndexDto.DEFAULT_NAME);
		getColumn(ContinentIndexDto.DEFAULT_NAME).setHidden(true);

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editContinent(e.getUuid()));
		}

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(ContinentIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}

		reload(true);
	}

	public void reload(boolean forceFetch) {
		if (forceFetch || allContinents == null) {
			allContinents = FacadeProvider.getContinentFacade().getIndexList(null, null, null, null);
		}
		reload();
	}

	public void reload() {
		this.setItems(createFilteredStream());
		setSelectionMode(isInEagerMode() ? SelectionMode.MULTI : SelectionMode.NONE);
	}

	private Stream<ContinentIndexDto> createFilteredStream() {

		// get all filter properties
		String nameLike = getCriteria().getNameLike() != null ? getCriteria().getNameLike().toLowerCase() : null;
		EntityRelevanceStatus relevanceStatus = getCriteria().getRelevanceStatus();

		Predicate<ContinentIndexDto> filters = x -> true; // "empty" basefilter

		// name filter
		if (!StringUtils.isEmpty(nameLike)) {
			filters = filters.and(
				continent -> (continent.getDefaultName().toLowerCase().contains(nameLike)
					|| continent.getDisplayName().toLowerCase().contains(nameLike)));
		}
		// relevancestatus filter (active/archived/all)
		if (relevanceStatus != null) {
			switch (relevanceStatus) {
			case ACTIVE:
				filters = filters.and(continent -> (!continent.isArchived()));
				break;
			case ARCHIVED:
				filters = filters.and(continent -> (continent.isArchived()));
				break;
			}
		}

		// apply filters
		return allContinents.stream().filter(filters);

	}
}
