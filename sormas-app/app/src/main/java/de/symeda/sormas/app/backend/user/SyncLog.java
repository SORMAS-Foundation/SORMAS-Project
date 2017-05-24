package de.symeda.sormas.app.backend.user;

import com.j256.ormlite.table.DatabaseTable;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Mate Strysewske on 23.05.2017.
 */
@Entity(name=SyncLog.TABLE_NAME)
@DatabaseTable(tableName=SyncLog.TABLE_NAME)
public class SyncLog extends AbstractDomainObject {

    public static final String TABLE_NAME = "synclog";

    public static final String ENTITY_NAME = "entityName";
    public static final String ENTITY_UUID = "entityUuid";
    public static final String CONFLICT_TEXT = "conflictText";

    @Column
    private String entityName;

    @Column
    private String entityUuid;

    @Column
    private String conflictText;

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
