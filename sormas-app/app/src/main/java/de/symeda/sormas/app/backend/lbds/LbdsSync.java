package de.symeda.sormas.app.backend.lbds;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.app.util.DateFormatHelper;

@Entity(name = LbdsSync.TABLE_NAME)
@DatabaseTable(tableName = LbdsSync.TABLE_NAME)
public class LbdsSync {

	public static final String TABLE_NAME = "lbdsSync";

	public static final String SENT_UUID = "sentUuid";
	public static final String LAST_SEND_DATE = "lastSendDate";
	public static final String LAST_RECEIVED_DATE = "lastReceivedDate";

	@Id
	private String sentUuid;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date lastSendDate;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date lastReceivedDate;

	public LbdsSync() {
	}

	public LbdsSync(String uuid) {
		sentUuid = uuid;
	}

	public String getSentUuid() {
		return sentUuid;
	}

	public void setSentUuid(String sentUuid) {
		this.sentUuid = sentUuid;
	}

	public Date getLastSendDate() {
		return lastSendDate;
	}

	public void setLastSendDate(Date lastSendDate) {
		this.lastSendDate = lastSendDate;
	}

	public Date getLastReceivedDate() {
		return lastReceivedDate;
	}

	public void setLastReceivedDate(Date lastReceivedDate) {
		this.lastReceivedDate = lastReceivedDate;
	}

	public String toString() {
		return "{sentUuid: " + sentUuid + ", lastSendDate: " + DateFormatHelper.formatLocalDateTime(lastSendDate) + ", lastReceivedDate: "
			+ DateFormatHelper.formatLocalDateTime(lastReceivedDate) + "}";
	}
}
