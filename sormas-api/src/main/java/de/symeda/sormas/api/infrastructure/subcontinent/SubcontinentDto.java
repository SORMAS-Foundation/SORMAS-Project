package de.symeda.sormas.api.infrastructure.subcontinent;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.continent.ContinentReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;

import java.util.Date;

public class SubcontinentDto extends EntityDto {

	public static final String I18N_PREFIX = "Subcontinent";
	public static final String DEFAULT_NAME = "defaultName";
	public static final String EXTERNAL_ID = "externalId";
	public static final String CONTINENT = "continent";

	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String defaultName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String externalId;
	private boolean archived;
	private ContinentReferenceDto continent;

	public SubcontinentDto() {
	}

	public static SubcontinentDto build() {
		SubcontinentDto dto = new SubcontinentDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}

	public SubcontinentDto(
		Date creationDate,
		Date changeDate,
		String uuid,
		String defaultName,
		String externalId,
		boolean archived,
		String continentUuid,
		String continentName,
		String continentExternalId) {
		super(creationDate, changeDate, uuid);
		this.defaultName = defaultName;
		this.externalId = externalId;
		this.archived = archived;
		if (continentUuid != null) {
			this.continent = new ContinentReferenceDto(continentUuid, I18nProperties.getContinentName(continentName), continentExternalId);
		}
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
