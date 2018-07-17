package de.symeda.sormas.app.core.enumeration;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.app.R;

public class ContactClassificationElaborator implements IStatusElaborator {

    private ContactClassification status = null;

    public ContactClassificationElaborator(ContactClassification status) {
        this.status = status;
    }

    @Override
    public String getFriendlyName() {
        if (status != null) {
            return status.toShortString();
        }
        return "";
    }

    @Override
    public int getColorIndicatorResource() {
        if (status == ContactClassification.UNCONFIRMED) {
            return R.color.indicatorPossibleContact;
        } else if (status == ContactClassification.CONFIRMED) {
            return R.color.indicatorConfirmedContact;
        } else if (status == ContactClassification.NO_CONTACT) {
            return R.color.indicatorNoContact;
        }
        return R.color.noColor;
    }

    @Override
    public String getStatekey() {
        return ARG_CONTACT_CLASSIFICATION_STATUS;
    }

    @Override
    public Enum getValue() {
        return this.status;
    }
}
