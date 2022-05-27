package de.symeda.sormas.api.infrastructure.continent;

import org.apache.commons.lang3.StringUtils;

public class ContinentIndexDto extends ContinentDto {

    public static final String DISPLAY_NAME = "displayName";

    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getCaption() {
        return getDefaultName();
    }

    @Override
    public String toString() {
        return I18N_PREFIX + StringUtils.SPACE + getUuid();
    }
}
