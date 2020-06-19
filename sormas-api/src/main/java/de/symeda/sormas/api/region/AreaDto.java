package de.symeda.sormas.api.region;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;

public class AreaDto extends EntityDto {

	public static final String I18N_PREFIX = "Area";
	public static final String NAME = "name";
	public static final String EXTERNAL_ID = "externalId";

	private static final long serialVersionUID = -6241927331721175673L;

	private String name;
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
}
