package de.symeda.sormas.backend.common;

import java.util.Date;

public interface DomainObject {

	String UUID = "uuid";
	String CREATION = "creation";
	String VERSION = "version";

	String getUuid();

	Date getCreation();

	Date getVersion();
}
