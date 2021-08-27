package de.symeda.sormas.api;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public interface BaseFacade<DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends ReferenceDto, CRITERIA extends BaseCriteria> {

	DTO getByUuid(String uuid);

	List<INDEX_DTO> getIndexList(CRITERIA criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(CRITERIA criteria);

	DTO save(@Valid DTO dto);

	void archive(String uuid);

	void dearchive(String uuid);

	List<DTO> getAllAfter(Date date);

	List<DTO> getByUuids(List<String> uuids);

	List<String> getAllUuids();

}
