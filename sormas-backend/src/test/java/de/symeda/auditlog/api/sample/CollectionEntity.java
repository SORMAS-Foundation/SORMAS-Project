package de.symeda.auditlog.api.sample;

import java.time.Month;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.HasUuid;

@Audited
public class CollectionEntity implements HasUuid {

	public static final String MONTH = "month";
	public static final String STRINGS = "strings";
	public static final String SIMPLEENTITIES = "simpleEntities";
	public static final String NULL_COLLECTION = "nullCollection";

	private final String uuid;
	private final List<String> strings = new ArrayList<>();
	private final Set<Month> month = EnumSet.noneOf(Month.class);
	private final List<SimpleBooleanFlagEntity> simpleEntities = new ArrayList<>();
	private final List<String> nullCollection = null;
	
	public CollectionEntity(String uuid) {
		this.uuid = uuid;
	}

	@Override
	@AuditedIgnore
	public String getUuid() {
		return uuid;
	}

	public List<String> getStrings() {
		return strings;
	}

	public Set<Month> getMonth() {
		return month;
	}

	public List<SimpleBooleanFlagEntity> getSimpleEntities() {
		return simpleEntities;
	}
	
	public List<String> getNullCollection() {
		return nullCollection;
	}
	
	

}
