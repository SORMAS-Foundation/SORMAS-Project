package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.app.backend.common.InfrastructureAdo;

@Entity(name = Country.TABLE_NAME)
@DatabaseTable(tableName = Country.TABLE_NAME)
public class Country extends InfrastructureAdo {

    private static final long serialVersionUID = -2958216667876104351L;

    public static final String TABLE_NAME = "country";
    public static final String I18N_PREFIX = "Country";

    public static final String NAME = "name";
    public static final String ISO_CODE = "isoCode";

    @Column
    private String name;

    @Column
    private String isoCode;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn
    private SubContinent subContinent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public SubContinent getSubContinent() {
        return subContinent;
    }

    public void setSubContinent(SubContinent subContinent) {
        this.subContinent = subContinent;
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
