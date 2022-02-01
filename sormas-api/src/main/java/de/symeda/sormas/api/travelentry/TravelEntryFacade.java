package de.symeda.sormas.api.travelentry;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.deletionconfiguration.AutomaticDeletionInfoDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface TravelEntryFacade extends BaseFacade<TravelEntryDto, TravelEntryIndexDto, TravelEntryReferenceDto, TravelEntryCriteria> {

	void validate(TravelEntryDto travelEntryDto);

	boolean isDeleted(String eventUuid);

	boolean isArchived(String travelEntryUuid);

	void archiveOrDearchiveTravelEntry(String travelEntryUuid, boolean archive);

	Boolean isTravelEntryEditAllowed(String travelEntryUuid);

	long count(TravelEntryCriteria criteria, boolean ignoreUserFilter);

	boolean exists(String uuid);

	void deleteTravelEntry(String travelEntryUuid);

	List<DeaContentEntry> getDeaContentOfLastTravelEntry();

	List<TravelEntryListEntryDto> getEntriesList(TravelEntryListCriteria criteria, Integer first, Integer max);

	Page<TravelEntryIndexDto> getIndexPage(TravelEntryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	AutomaticDeletionInfoDto getAutomaticDeletionInfo(String uuid);
}
