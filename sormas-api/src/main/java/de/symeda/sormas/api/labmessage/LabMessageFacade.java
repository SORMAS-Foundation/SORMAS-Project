package de.symeda.sormas.api.labmessage;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface LabMessageFacade {

	void save(LabMessageDto dto);

	LabMessageDto getByUuid(String uuid);

	long count(LabMessageCriteria criteria);

	List<LabMessageIndexDto> getIndexList(LabMessageCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	void fetchExternalLabMessages();
}
