package de.symeda.sormas.app.util;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.YesNoUnknown;

public enum YesNo {
        YES,
        NO;

        private YesNo() {
        }

        public String toString() {
            return I18nProperties.getEnumCaption(this);
        }

        public static YesNo valueOf(Boolean value) {
            if (value == null) {
                return null;
            } else {
                return Boolean.TRUE.equals(value) ? YES : NO;
            }
        }
    }
