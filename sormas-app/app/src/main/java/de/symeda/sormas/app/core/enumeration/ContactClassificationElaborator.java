package de.symeda.sormas.app.core.enumeration;

import android.content.res.Resources;

import de.symeda.sormas.app.R;

import de.symeda.sormas.api.contact.ContactClassification;

/**
 * Created by Orson on 07/01/2018.
 */

public class ContactClassificationElaborator implements IStatusElaborator {

    private Resources resources = null;
    private ContactClassification status = null;

    public ContactClassificationElaborator(ContactClassification status, Resources resources) {
        this.status = status;
        this.resources = resources;
    }

    @Override
    public String getFriendlyName() {
        if (status == ContactClassification.POSSIBLE) {
            return resources.getString(R.string.status_contact_classification_possible);
        } else if (status == ContactClassification.CONFIRMED) {
            return resources.getString(R.string.status_contact_classification_confirmed);
        } else if (status == ContactClassification.NO_CONTACT) {
            return resources.getString(R.string.status_contact_classification_no_contact);
        } else if (status == ContactClassification.CONVERTED) {
            return resources.getString(R.string.status_contact_classification_converted);
        } else if (status == ContactClassification.DROPPED) {
            return resources.getString(R.string.status_contact_classification_dropped);
        }

        return "";
    }

    @Override
    public int getColorIndicatorResource() {
        if (status == ContactClassification.POSSIBLE) {
            return R.color.indicatorPossibleContact;
        } else if (status == ContactClassification.CONFIRMED) {
            return R.color.indicatorConfirmedContact;
        } else if (status == ContactClassification.NO_CONTACT) {
            return R.color.indicatorNoContact;
        } else if (status == ContactClassification.CONVERTED) {
            return R.color.indicatorConvertedContact;
        } else if (status == ContactClassification.DROPPED) {
            return R.color.indicatorDroppedContact;
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
