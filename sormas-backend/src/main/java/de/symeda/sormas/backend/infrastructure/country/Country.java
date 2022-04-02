package de.symeda.sormas.backend.infrastructure.country;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import de.symeda.sormas.backend.common.InfrastructureAdo;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;

@Entity
public class Country extends InfrastructureAdo {

	private static final long serialVersionUID = -6050390899060395940L;

	public static final String TABLE_NAME = "country";

	public static final String DEFAULT_NAME = "defaultName";
	public static final String EXTERNAL_ID = "externalId";
	public static final String ISO_CODE = "isoCode";
	public static final String UNO_CODE = "unoCode";
	public static final String SUBCONTINENT = "subcontinent";

	private String defaultName;
	private Long externalId;
	private String isoCode;
	private String unoCode;
	private Subcontinent subcontinent;

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
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

	@ManyToOne(cascade = CascadeType.REFRESH)
	public Subcontinent getSubcontinent() {
		return subcontinent;
	}

	public void setSubcontinent(Subcontinent subcontinent) {
		this.subcontinent = subcontinent;
	}

	@Override
	public String toString() {
		return this.defaultName;
	}
}
