package de.symeda.sormas.app.backend.region;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.app.backend.infrastructure.InfrastructureAdo;

@Entity(name = Country.TABLE_NAME)
@DatabaseTable(tableName = Country.TABLE_NAME)
public class Country extends InfrastructureAdo {

	private static final long serialVersionUID = -2958216667876104351L;

	public static final String TABLE_NAME = "country";
	public static final String I18N_PREFIX = "Country";

	public static final String NAME = "name";
	public static final String ISO_CODE = "isoCode";
	public static final String SUBCONTINENT = "subcontinent";

	@Column
	private String name;

	@Column
	private String isoCode;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn
	private Subcontinent subcontinent;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	public Subcontinent getSubcontinent() {
		return subcontinent;
	}

	public void setSubcontinent(Subcontinent subcontinent) {
		this.subcontinent = subcontinent;
	}

	@Override
	public String buildCaption() {
		return getName();
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
