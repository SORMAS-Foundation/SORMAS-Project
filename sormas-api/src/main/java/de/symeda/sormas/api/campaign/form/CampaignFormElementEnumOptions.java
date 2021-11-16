package de.symeda.sormas.api.campaign.form;

public enum CampaignFormElementEnumOptions {
	
	OPTION1(CampaignFormElementOptions.getOpt1()),
	OPTION2(CampaignFormElementOptions.getOpt2()),
	OPTION3(CampaignFormElementOptions.getOpt3()),
	OPTION4(CampaignFormElementOptions.getOpt4()),
	OPTION5(CampaignFormElementOptions.getOpt5()),
	OPTION6(CampaignFormElementOptions.getOpt6()),
	OPTION7(CampaignFormElementOptions.getOpt7()),
	OPTION8(CampaignFormElementOptions.getOpt8()),
	OPTION9(CampaignFormElementOptions.getOpt9()),
	OPTION10(CampaignFormElementOptions.getOpt10()),
	;

	public final String label;

    private CampaignFormElementEnumOptions(String label) {
        this.label = label;
    }	
    public static CampaignFormElementEnumOptions valueOfLabel(String label) {
        for (CampaignFormElementEnumOptions e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
            
        }
        return null;
    }
    
    @Override 
    public String toString() { 
        return this.label; 
    }
	
	/*
	public String toString() {
		return name().toLowerCase().replaceAll("_", "-");
	}

	public static CampaignFormElementEnumOptions fromString(String stringValue) {
		return valueOf(stringValue.toUpperCase().replaceAll("-", "_"));
	}
*/
}
