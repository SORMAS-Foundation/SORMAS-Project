package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name=Region.TABLE_NAME)
@DatabaseTable(tableName = Region.TABLE_NAME)
public class Region extends AbstractDomainObject {
	
	private static final long serialVersionUID = -2958216667876104358L;

	public static final String TABLE_NAME = "region";
	public static final String I18N_PREFIX = "Region";

	public static final String NAME = "name";
	public static final String EPID_CODE = "epidCode";

	@Column
	private String name;

	@Column
	private String epidCode;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getEpidCode() {
		return epidCode;
	}
	public void setEpidCode(String epidCode) {
		this.epidCode = epidCode;
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
