package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.SubcontinentCriteria;
import de.symeda.sormas.api.region.SubcontinentIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class SubcontinentsGrid extends FilteredGrid<SubcontinentIndexDto, SubcontinentCriteria> {

	private List<SubcontinentIndexDto> allSubcontinents;

	public SubcontinentsGrid(SubcontinentCriteria criteria) {
		super(SubcontinentIndexDto.class);

		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(SubcontinentsView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		super.setCriteria(criteria, true);
		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setSelectionMode(SelectionMode.MULTI);
		} else {
			setSelectionMode(SelectionMode.NONE);
		}

		setColumns(
			SubcontinentIndexDto.DISPLAY_NAME,
			SubcontinentIndexDto.CONTINENT,
			SubcontinentIndexDto.EXTERNAL_ID,
			SubcontinentIndexDto.DEFAULT_NAME);
		getColumn(SubcontinentIndexDto.DEFAULT_NAME).setHidden(true);

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editSubcontinent(e.getUuid()));
		}

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(SubcontinentIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}

		reload(true);
	}

	public void reload(boolean forceFetch) {
		if (forceFetch || allSubcontinents == null) {
			allSubcontinents = FacadeProvider.getSubcontinentFacade().getIndexList(null, null, null, null);
		}
		reload();
	}

	public void reload() {
		this.setItems(createFilteredStream());
		setSelectionMode(isInEagerMode() ? SelectionMode.MULTI : SelectionMode.NONE);
	}

	private Stream<SubcontinentIndexDto> createFilteredStream() {

		// get all filter properties
		String nameLike = getCriteria().getNameLike() != null ? getCriteria().getNameLike().toLowerCase() : null;
		String continentUuid = getCriteria().getContinent() != null ? getCriteria().getContinent().getUuid() : null;
		EntityRelevanceStatus relevanceStatus = getCriteria().getRelevanceStatus();

		Predicate<SubcontinentIndexDto> filters = x -> true; // "empty" basefilter

		// name filter
		if (!StringUtils.isEmpty(nameLike)) {
			filters = filters.and(
				subcontinent -> (subcontinent.getDefaultName().toLowerCase().contains(nameLike)
					|| subcontinent.getDisplayName().toLowerCase().contains(nameLike)));
		}
		// continent filter
		if (continentUuid != null) {
			filters =
				filters.and(subcontinent -> (subcontinent.getContinent() != null && subcontinent.getContinent().getUuid().equals(continentUuid)));
		}
		// relevancestatus filter (active/archived/all)
		if (relevanceStatus != null) {
			switch (relevanceStatus) {
			case ACTIVE:
				filters = filters.and(subcontinent -> (!subcontinent.isArchived()));
				break;
			case ARCHIVED:
				filters = filters.and(subcontinent -> (subcontinent.isArchived()));
				break;
			}
		}

		// apply filters
		return allSubcontinents.stream().filter(filters);

	}
}
