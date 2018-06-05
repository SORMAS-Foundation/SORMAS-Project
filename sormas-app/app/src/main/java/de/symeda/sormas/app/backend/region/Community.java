package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name=Community.TABLE_NAME)
@DatabaseTable(tableName = Community.TABLE_NAME)
public class Community extends AbstractDomainObject {

	private static final long serialVersionUID = 1971053920357795693L;

	public static final String TABLE_NAME = "community";
	public static final String I18N_PREFIX = "Community";

	public static final String NAME = "name";
	public static final String DISTRICT = "district";

	@Column
	private String name;

	@DatabaseField(foreign = true, foreignAutoRefresh=true, canBeNull = false, maxForeignAutoRefreshLevel = 3)
	@ManyToOne(cascade = CascadeType.REFRESH, optional = false)
	@JoinColumn(nullable = false)
	private District district;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public District getDistrict() {
		return district;
	}
	public void setDistrict(District district) {
		this.district = district;
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
