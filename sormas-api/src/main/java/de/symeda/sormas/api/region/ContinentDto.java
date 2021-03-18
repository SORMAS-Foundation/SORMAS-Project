package de.symeda.sormas.api.region;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;

public class ContinentDto extends EntityDto {

	public static final String I18N_PREFIX = "Continent";
	public static final String DEFAULT_NAME = "defaultName";
	public static final String EXTERNAL_ID = "externalId";

	private String defaultName;
	private String externalId;
	private boolean archived;

	public static ContinentDto build() {
		ContinentDto dto = new ContinentDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
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
