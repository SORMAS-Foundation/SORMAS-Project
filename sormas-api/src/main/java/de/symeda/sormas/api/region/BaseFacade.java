package de.symeda.sormas.api.region;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

public interface BaseFacade<DTO extends EntityDto, INDEX_DTO extends EntityDto, REF_DTO extends ReferenceDto, CRITERIA extends BaseCriteria> {

	DTO getByUuid(String uuid);

	List<INDEX_DTO> getIndexList(CRITERIA criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(CRITERIA criteria);

	void save(DTO dto);

	void archive(String uuid);

	void dearchive(String uuid);

	List<DTO> getAllAfter(Date date);

	List<DTO> getByUuids(List<String> uuids);

	List<String> getAllUuids();

	List<REF_DTO> getAllActiveAsReference();
}
