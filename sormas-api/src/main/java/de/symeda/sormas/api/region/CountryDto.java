package de.symeda.sormas.api.region;

import java.util.Date;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;

public class CountryDto extends EntityDto {

	private static final long serialVersionUID = 8309822957203823162L;

	public static final String I18N_PREFIX = "Country";
	public static final String DEFAULT_NAME = "defaultName";
	public static final String EXTERNAL_ID = "externalId";
	public static final String ISO_CODE = "isoCode";
	public static final String UNO_CODE = "unoCode";

	private String defaultName;
	private String externalId;
	@Size(min = 2, max = 3)
	private String isoCode;
	@Size(min = 1, max = 3)
	private String unoCode;
	private boolean archived;

	public CountryDto(
		Date creationDate,
		Date changeDate,
		String uuid,
		boolean archived,
		String defaultName,
		String externalId,
		String isoCode,
		String unoCode) {

		super(creationDate, changeDate, uuid);
		this.archived = archived;
		this.defaultName = defaultName;
		this.externalId = externalId;
		this.isoCode = isoCode;
		this.unoCode = unoCode;
	}

	public CountryDto() {
		super();
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

	public void setExternalId(String externalID) {
		this.externalId = externalID;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	public String getUnoCode() {
		return unoCode;
	}

	public void setUnoCode(String unoCode) {
		this.unoCode = unoCode;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	@Override
	public String toString() {
		return this.defaultName;
	}

	public static CountryDto build() {
		CountryDto dto = new CountryDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}
}
