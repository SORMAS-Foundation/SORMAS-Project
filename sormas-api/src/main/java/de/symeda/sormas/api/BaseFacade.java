package de.symeda.sormas.api;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public interface BaseFacade<DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends ReferenceDto, CRITERIA extends BaseCriteria> {

	DTO save(@Valid DTO dto);

	DTO save(@Valid DTO dtoToSave, boolean allowMerge);

	void archive(String uuid);

	void dearchive(String uuid);

	long count(CRITERIA criteria);

	DTO getByUuid(String uuid);

	REF_DTO getReferenceByUuid(String uuid);

	List<DTO> getByUuids(List<String> uuids);

	List<String> getAllUuids();

	List<INDEX_DTO> getIndexList(CRITERIA criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	List<DTO> getAllAfter(Date date);
}
