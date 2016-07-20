package de.symeda.sormas.app.backend.common;

import java.util.Date;

public interface DomainObject {

	String UUID = "uuid";
	String MODIFIED = "modified";
	String CREATION = "creation";
	String VERSION = "version";

	String getUuid();

	boolean isModified();

	Date getCreation();

	Date getVersion();
}
