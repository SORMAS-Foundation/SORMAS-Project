package de.symeda.sormas.app.backend.synclog;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Mate Strysewske on 23.05.2017.
 */
@Entity(name=SyncLog.TABLE_NAME)
@DatabaseTable(tableName=SyncLog.TABLE_NAME)
public class SyncLog {

    public static final String TABLE_NAME = "synclog";

    public static final String ENTITY_NAME = "entityName";
    public static final String ENTITY_UUID = "entityUuid";
    public static final String CONFLICT_TEXT = "conflictText";

    @Id
    @GeneratedValue
    private Long id;

    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private Date creationDate;

    @Column
    private String entityName;

    @Column
    private String entityUuid;

    @Column
    private String conflictText;

    public SyncLog() { }

    public SyncLog(String entityName, String entityUuid, String conflictText) {
        this.entityName = entityName;
        this.entityUuid = entityUuid;
        this.conflictText = conflictText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityUuid() {
        return entityUuid;
    }

    public void setEntityUuid(String entityUuid) {
        this.entityUuid = entityUuid;
    }

    public String getConflictText() {
        return conflictText;
    }

    public void setConflictText(String conflictText) {
        this.conflictText = conflictText;
    }

}
