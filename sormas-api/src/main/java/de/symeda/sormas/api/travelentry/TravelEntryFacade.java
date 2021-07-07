package de.symeda.sormas.api.travelentry;

import javax.ejb.Remote;

import de.symeda.sormas.api.region.BaseFacade;

@Remote
public interface TravelEntryFacade extends BaseFacade<TravelEntryDto, TravelEntryIndexDto, TravelEntryReferenceDto, TravelEntryCriteria> {

	void validate(TravelEntryDto travelEntryDto);
}
