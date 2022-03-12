package de.symeda.sormas.api.travelentry;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.deletionconfiguration.AutomaticDeletionInfoDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface TravelEntryFacade extends CoreFacade<TravelEntryDto, TravelEntryIndexDto, TravelEntryReferenceDto, TravelEntryCriteria> {

	void validate(TravelEntryDto travelEntryDto);

	boolean isDeleted(String eventUuid);

	Boolean isTravelEntryEditAllowed(String travelEntryUuid);

	long count(TravelEntryCriteria criteria, boolean ignoreUserFilter);

	List<DeaContentEntry> getDeaContentOfLastTravelEntry();

	List<TravelEntryListEntryDto> getEntriesList(TravelEntryListCriteria criteria, Integer first, Integer max);

	Page<TravelEntryIndexDto> getIndexPage(TravelEntryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);
}
