package de.symeda.sormas.app.backend.region;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.app.backend.common.InfrastructureAdo;

@Entity(name = Country.TABLE_NAME)
@DatabaseTable(tableName = Country.TABLE_NAME)
public class Country extends InfrastructureAdo {

	private static final long serialVersionUID = -2958216667876104351L;

	public static final String TABLE_NAME = "country";
	public static final String I18N_PREFIX = "Country";

	public static final String NAME = "name";
	public static final String ISO_CODE = "isoCode";

	@Column
	private String name;

	@Column
	private String isoCode;

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

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
