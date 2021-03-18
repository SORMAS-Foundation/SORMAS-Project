package de.symeda.sormas.backend.region;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import de.symeda.sormas.backend.common.InfrastructureAdo;

@Entity
public class Country extends InfrastructureAdo {

	private static final long serialVersionUID = -6050390899060395940L;

	public static final String TABLE_NAME = "country";

	public static final String DEFAULT_NAME = "defaultName";
	public static final String EXTERNAL_ID = "externalId";
	public static final String ISO_CODE = "isoCode";
	public static final String UNO_CODE = "unoCode";
	public static final String SUB_CONTINENT = "subContinent";

	private String defaultName;
	private String externalId;
	private String isoCode;
	private String unoCode;
	private SubContinent subContinent;

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

	@ManyToOne
	public SubContinent getSubContinent() {
		return subContinent;
	}

	public void setSubContinent(SubContinent subContinent) {
		this.subContinent = subContinent;
	}

	@Override
	public String toString() {
		return this.defaultName;
	}
}
