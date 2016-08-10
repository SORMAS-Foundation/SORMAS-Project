package de.symeda.sormas.app.backend.config;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserRoleToUser;

@Entity(name= Config.TABLE_NAME)
@DatabaseTable(tableName = Config.TABLE_NAME)
public class Config {
	
	public static final String TABLE_NAME = "config";

	public static final String KEY = "key";
	public static final String VALUE = "value";

	@Id
	private String key;

	@Column(nullable = false)
	private String value;

	public Config() { }

	public Config(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
