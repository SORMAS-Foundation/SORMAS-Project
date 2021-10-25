package de.symeda.sormas.api.infrastructure.area;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;

import java.util.Date;

public class AreaDto extends EntityDto {

	public static final String I18N_PREFIX = "Area";
	public static final String NAME = "name";
	public static final String EXTERNAL_ID = "externalId";

	private static final long serialVersionUID = -6241927331721175673L;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String name;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalId;
	private boolean archived;

	public AreaDto() {
	}

	public AreaDto(Date creationDate, Date changeDate, String uuid, String name, String externalId, boolean archived) {
		super(creationDate, changeDate, uuid);
		this.name = name;
		this.externalId = externalId;
		this.archived = archived;
	}

	public static AreaDto build() {
		AreaDto area = new AreaDto();
		area.setUuid(DataHelper.createUuid());
		return area;
	}

	public AreaReferenceDto toReference() {
		return new AreaReferenceDto(getUuid());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}
}
