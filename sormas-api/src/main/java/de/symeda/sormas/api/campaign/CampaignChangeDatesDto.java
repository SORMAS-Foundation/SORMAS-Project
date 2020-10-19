package de.symeda.sormas.api.campaign;

import java.io.Serializable;
import java.util.Date;

public class CampaignChangeDatesDto implements Serializable {

    private Date campaignChangeDate;
    private Date campaignFormMetaChangeDate;

    public Date getCampaignChangeDate() {
        return campaignChangeDate;
    }

    public void setCampaignChangeDate(Date campaignChangeDate) {
        this.campaignChangeDate = campaignChangeDate;
    }

    public Date getCampaignFormMetaChangeDate() {
        return campaignFormMetaChangeDate;
    }

    public void setCampaignFormMetaChangeDate(Date campaignFormMetaChangeDate) {
        this.campaignFormMetaChangeDate = campaignFormMetaChangeDate;
    }
}
