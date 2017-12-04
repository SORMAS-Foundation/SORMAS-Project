package de.symeda.sormas.ui.utils;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Field;

import de.symeda.sormas.api.EntityDto;

public interface DtoEditForm<DTO extends EntityDto> extends Field<DTO> {

	DTO getDto();

	void setDto(DTO dto);

	FieldGroup getFieldGroup();

}
