package de.symeda.sormas.api.infrastructure.area;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;

public class AreaDto extends InfrastructureDto {

	public static final String I18N_PREFIX = "Area";
	public static final String NAME = "name";
	public static final String EXTERNAL_ID = "externalId";

	private static final long serialVersionUID = -6241927331721175673L;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String name;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalId;
	private boolean archived;

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

	@Override
	public String toString() {
		return getName();
	}
}
