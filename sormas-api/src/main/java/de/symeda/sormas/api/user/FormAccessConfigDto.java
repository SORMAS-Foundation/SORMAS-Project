package de.symeda.sormas.api.user;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;

public class FormAccessConfigDto extends EntityDto{

	private static final long serialVersionUID = -547459523041494446L;

	public static final String I18N_PREFIX = "FormAccess";

	public static final String USER_TYPE = "formAccess";
	
	private FormAccess formAccess;
	
	public static FormAccessConfigDto build(FormAccess formAccess) {
		FormAccessConfigDto dto = new FormAccessConfigDto();
		dto.setUuid(DataHelper.createUuid());
		dto.setFormAccess(formAccess);
		
		return dto;
	}

	public FormAccess getFormAccess() {
		return formAccess;
	}

	public void setFormAccess(FormAccess formAccess) {
		this.formAccess = formAccess;
	}
	
	
	
}
