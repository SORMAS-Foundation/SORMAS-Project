package de.symeda.sormas.api.report;

import java.sql.Date;
import java.util.List;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;

public class CompletionAnalysisModelDto extends EntityDto {
	


		public static final String I18N_PREFIX = "CampaignFormData";

		public static final String UUID = "uuid";
		public static final String CAMPAIGN = "campaign";
		public static final String FORM = "form";
		public static final String AREA = "area";
		public static final String RCODE = "rcode";
		public static final String REGION = "region";
		public static final String PCODE = "pcode";
		public static final String DISTRICT = "district";
		public static final String DCODE = "dcode";
		public static final String COMMUNITY = "community";
		public static final String COMMUNITYNUMBER = "clusternumber";
		public static final String CCODE = "ccode";
		public static final String FORM_DATE = "formDate";
		public static final String FORM_TYPE = "formType";
		public static final String ANALYSIS_1 = "analysis1";
		public static final String ANALYSIS_2 = "analysis2";
		public static final String ANALYSIS_3 = "analysis3";
		public static final String ANALYSIS_4 = "analysis4";
		
		
		private static final long serialVersionUID = -6672198324526771162L;

		private String uuid;
		private String campaign;
		private String form;
		private List<CampaignFormDataEntry> formValues;
		private String area;
		private Long rcode;
		private String region;
		private Long pcode;
		private String district;
		private Long dcode;
		private String community;
		private Integer clusternumber;
		private Long ccode;
		private Date formDate;
		private String formType;
		private String analysis1;
		private String analysis2;
		private String analysis3;
		private String analysis4;
		

	

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public String getCampaign() {
			return campaign;
		}

		public void setCampaign(String campaign) {
			this.campaign = campaign;
		}

		public String getForm() {
			return form;
		}

		public void setForm(String form) {
			this.form = form;
		}

		public List<CampaignFormDataEntry> getFormValues() {
			return formValues;
		}

		public void setFormValues(List<CampaignFormDataEntry> formValues) {
			this.formValues = formValues;
		}

		public String getArea() {
			return area;
		}

		public void setArea(String area) {
			this.area = area;
		}

		public String getRegion() {
			return region;
		}

		public void setRegion(String region) {
			this.region = region;
		}

		public String getDistrict() {
			return district;
		}

		public void setDistrict(String district) {
			this.district = district;
		}

		public String getCommunity() {
			return community;
		}

		public void setCommunity(String community) {
			this.community = community;
		}
		
		

		public Integer getClusternumber() {
			return clusternumber;
		}

		public void setClusternumber(Integer clusternumber) {
			this.clusternumber = clusternumber;
		}

		public Date getFormDate() {
			return formDate;
		}

		public void setFormDate(Date formDate) {
			this.formDate = formDate;
		}

		public String getFormType() {
			return formType;
		}

		public void setFormType(String formType) {
			this.formType = formType;
		}

		public Long getRcode() {
			return rcode;
		}

		public void setRcode(Long rcode) {
			this.rcode = rcode;
		}

		public int getPcode() {
			return pcode.intValue();
		}

		public void setPcode(Long pcode) {
			this.pcode = pcode;
		}

		public int getDcode() {
			return dcode.intValue();
		}

		public void setDcode(Long dcode) {
			this.dcode = dcode;
		}

		public Long getCcode() {
			return ccode;//Integer.parseInt(ccode+"");
		}

		public void setCcode(Long ccode) {
			this.ccode = ccode;
		}

		public String getAnalysis1() {
			return analysis1;
		}

		public void setAnalysis1(String analysis1) {
			this.analysis1 = analysis1;
		}

		public String getAnalysis2() {
			return analysis2;
		}

		public void setAnalysis2(String analysis2) {
			this.analysis2 = analysis2;
		}

		public String getAnalysis3() {
			return analysis3;
		}

		public void setAnalysis3(String analysis3) {
			this.analysis3 = analysis3;
		}

		public String getAnalysis4() {
			return analysis4;
		}

		public void setAnalysis4(String analysis4) {
			this.analysis4 = analysis4;
		}
		
		
	
}
