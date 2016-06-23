package de.symeda.sormas.ui.utils;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Component;

import de.symeda.sormas.api.DataTransferObject;

public interface DtoEditForm<DTO extends DataTransferObject> extends Component {

	DTO getDto();

	void setDto(DTO dto);

	FieldGroup getFieldGroup();

}
