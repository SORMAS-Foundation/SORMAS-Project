package de.symeda.sormas.backend.travelentry;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryFacade;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.person.PersonService;

@Stateless(name = "TravelEntryFacade")
public class TravelEntryFacadeEjb implements TravelEntryFacade {

	@EJB
	TravelEntryService travelEntryService;
	@EJB
	private PersonService personService;

	@Override
	public TravelEntryDto getByUuid(String uuid) {
		return null;
	}

	@Override
	public void archive(String uuid) {
		TravelEntry travelEntry = travelEntryService.getByUuid(uuid);
		if (travelEntry != null) {
			travelEntry.setArchived(true);
			travelEntryService.ensurePersisted(travelEntry);
		}
	}

	@Override
	public void dearchive(String uuid) {
		//to be implemented
	}

	@Override
	public List<TravelEntryDto> getAllAfter(Date date) {
		return null;
	}

	@Override
	public List<TravelEntryDto> getByUuids(List<String> uuids) {
		return null;
	}

	@Override
	public List<String> getAllUuids() {
		return null;
	}

	@Override
	public long count(TravelEntryCriteria criteria) {
		return 0;
	}

	@Override
	public TravelEntryDto save(TravelEntryDto dto) {
		return null;
	}

	@Override
	public List<TravelEntryIndexDto> getIndexList(TravelEntryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		return null;
	}

	@Override
	public void validate(TravelEntryDto travelEntryDto) {

	}

	@LocalBean
	@Stateless
	public static class TravelEntryFacadeEjbLocal extends TravelEntryFacadeEjb {

	}
}
