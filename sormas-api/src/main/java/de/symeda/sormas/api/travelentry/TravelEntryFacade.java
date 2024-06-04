package de.symeda.sormas.api.travelentry;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface TravelEntryFacade extends CoreFacade<TravelEntryDto, TravelEntryIndexDto, TravelEntryReferenceDto, TravelEntryCriteria> {

	long count(TravelEntryCriteria criteria, boolean ignoreUserFilter);

	List<DeaContentEntry> getDeaContentOfLastTravelEntry();

	TravelEntryDto getTravelEntryByUuid(String uuid);

	List<TravelEntryListEntryDto> getEntriesList(TravelEntryListCriteria criteria, Integer first, Integer max);

	Page<TravelEntryIndexDto> getIndexPage(TravelEntryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	void archiveAllArchivableTravelEntries(int daysAfterTravelEntryGetsArchived);

	List<TravelEntryDto> getByPersonUuids(List<String> uuids);
}
