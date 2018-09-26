package de.symeda.sormas.app.backend.config;

import com.j256.ormlite.table.DatabaseTable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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
