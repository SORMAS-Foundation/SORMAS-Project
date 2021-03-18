package de.symeda.sormas.api.region;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;

public class SubContinentDto extends EntityDto {

	public static final String I18N_PREFIX = "SubContinent";
	public static final String DEFAULT_NAME = "defaultName";
	public static final String EXTERNAL_ID = "externalId";
	public static final String CONTINENT = "continent";

	private String defaultName;
	private String externalId;
	private boolean archived;
	private ContinentReferenceDto continent;

	public static SubContinentDto build() {
		SubContinentDto dto = new SubContinentDto();
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

	public ContinentReferenceDto getContinent() {
		return continent;
	}

	public void setContinent(ContinentReferenceDto continent) {
		this.continent = continent;
	}
}
