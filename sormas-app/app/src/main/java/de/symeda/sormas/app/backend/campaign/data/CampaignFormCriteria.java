package de.symeda.sormas.app.backend.campaign.data;

import java.io.Serializable;

import de.symeda.sormas.app.backend.campaign.Campaign;

public class CampaignFormCriteria implements Serializable {

    private Campaign campaign;

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }
}
