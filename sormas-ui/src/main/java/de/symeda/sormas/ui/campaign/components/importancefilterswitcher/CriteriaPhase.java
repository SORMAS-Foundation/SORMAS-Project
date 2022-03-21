package de.symeda.sormas.ui.campaign.components.importancefilterswitcher;

import java.io.Serializable;

import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.ui.campaign.components.CampaignFormPhaseSelector;

public class CriteriaPhase extends BaseCriteria implements Serializable {

		
		public static final String FORM_PHASE = "formPhase"; //for filter
		

		private static final long serialVersionUID = 8124072093160133408L;

		private CampaignFormPhaseSelector formPhase; // for filter

		
		
		//needed for filter purpose

		public CampaignFormPhaseSelector getFormPhase() {
			return formPhase;
		}

		public void setFormPhase(CampaignFormPhaseSelector campaignFormPhaseSelector) {
			this.formPhase = campaignFormPhaseSelector;
		}
		
		public CriteriaPhase formPhase(CampaignFormPhaseSelector formPhase) {
			this.formPhase = formPhase;
			return this;
		}
		
		
	}


