package de.symeda.sormas.api.travelentry;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.CoreBaseFacade;

@Remote
public interface TravelEntryFacade extends CoreBaseFacade<TravelEntryDto, TravelEntryIndexDto, TravelEntryReferenceDto, TravelEntryCriteria> {

	void validate(TravelEntryDto travelEntryDto);

	boolean isDeleted(String eventUuid);

	boolean isArchived(String travelEntryUuid);

	void archiveOrDearchiveTravelEntry(String travelEntryUuid, boolean archive);

	Boolean isTravelEntryEditAllowed(String travelEntryUuid);

	long count(TravelEntryCriteria criteria, boolean ignoreUserFilter);

	void deleteTravelEntry(String travelEntryUuid);

	List<DeaContentEntry> getDeaContentOfLastTravelEntry();

	List<TravelEntryListEntryDto> getEntriesList(TravelEntryListCriteria criteria, Integer first, Integer max);
}
