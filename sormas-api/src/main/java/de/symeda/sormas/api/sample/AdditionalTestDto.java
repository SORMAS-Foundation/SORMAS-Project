package de.symeda.sormas.api.sample;

import java.util.Date;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;

public class AdditionalTestDto extends EntityDto {

	private static final long serialVersionUID = -7306267901413644171L;

	public static final String I18N_PREFIX = "AdditionalTest";
	
	public static final String SAMPLE = "sample";
	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String HAEMOGLOBINURIA = "haemoglobinuria";
	public static final String PROTEINURIA = "proteinuria";
	public static final String HEMATURIA = "hematuria";
	public static final String ARTERIAL_VENOUS_BLOOD_GAS = "arterialVenousBloodGas";
	public static final String ARTERIAL_VENOUS_GAS_PH = "arterialVenousGasPH";
	public static final String ARTERIAL_VENOUS_GAS_PCO2 = "arterialVenousGasPco2";
	public static final String ARTERIAL_VENOUS_GAS_PAO2 = "arterialVenousGasPao2";
	public static final String ARTERIAL_VENOUS_GAS_HCO3 = "arterialVenousGasHco3";
	public static final String GAS_OXYGEN_THERAPY = "gasOxygenTherapy";
	public static final String ALT_SGPT = "altSgpt";
	public static final String AST_SGOT = "astSgot";
	public static final String CREATININE = "creatinine";
	public static final String POTASSIUM = "potassium";
	public static final String UREA = "urea";
	public static final String HAEMOGLOBIN = "haemoglobin";
	public static final String TOTAL_BILIRUBIN = "totalBilirubin";
	public static final String CONJ_BILIRUBIN = "conjBilirubin";
	public static final String WBC_COUNT = "wbcCount";
	public static final String PLATELETS = "platelets";
	public static final String PROTHROMBIN_TIME = "prothrombinTime";
	public static final String OTHER_TEST_RESULTS = "otherTestResults";
	
	private SampleReferenceDto sample;
	private Date testDateTime;
	private SimpleTestResultType haemoglobinuria;
	private SimpleTestResultType proteinuria;
	private SimpleTestResultType hematuria;
	private Integer arterialVenousGasPH;
	private Integer arterialVenousGasPco2;
	private Integer arterialVenousGasPao2;
	private Integer arterialVenousGasHco3;
	private Integer gasOxygenTherapy;
	private Integer altSgpt;
	private Integer astSgot;
	private Integer creatinine;
	private Integer potassium;
	private Integer urea;
	private Integer haemoglobin;
	private Integer totalBilirubin;
	private Integer conjBilirubin;
	private Integer wbcCount;
	private Integer platelets;
	private Integer prothrombinTime;
	private String otherTestResults;
	
	public static AdditionalTestDto build(SampleReferenceDto sample) {
		AdditionalTestDto additionalTest = new AdditionalTestDto();
		additionalTest.setUuid(DataHelper.createUuid());
		additionalTest.setSample(sample);
		return additionalTest;
	}
	
	public boolean hasArterialVenousGasValue() {
		return arterialVenousGasPH != null || arterialVenousGasPco2 != null
				|| arterialVenousGasPao2 != null || arterialVenousGasHco3 != null;
	}
	
	public String buildArterialVenousGasValuesString() {
		StringBuilder sb = new StringBuilder();
		if (arterialVenousGasPH != null) {
			sb.append(I18nProperties.getPrefixCaption(I18N_PREFIX, ARTERIAL_VENOUS_GAS_PH)).append(": ").append(arterialVenousGasPH);
		}
		if (arterialVenousGasPco2 != null) {
			String pCo2String = I18nProperties.getPrefixCaption(I18N_PREFIX, ARTERIAL_VENOUS_GAS_PCO2);
			sb.append(sb.length() > 0 ? " - " + pCo2String + ": " : pCo2String + ": ").append(arterialVenousGasPco2);
		}
		if (arterialVenousGasPao2 != null) {
			String paO2String = I18nProperties.getPrefixCaption(I18N_PREFIX, ARTERIAL_VENOUS_GAS_PAO2);
			sb.append(sb.length() > 0 ? " - " + paO2String + ": " : paO2String+ ": ").append(arterialVenousGasPao2);
		}
		if (arterialVenousGasHco3 != null) {
			String hCo3String = I18nProperties.getPrefixCaption(I18N_PREFIX, ARTERIAL_VENOUS_GAS_HCO3);
			sb.append(sb.length() > 0 ? " - " + hCo3String + ": " : hCo3String + ": ").append(arterialVenousGasHco3);
		}
		return sb.toString();
	}
	
	public SampleReferenceDto getSample() {
		return sample;
	}
	public void setSample(SampleReferenceDto sample) {
		this.sample = sample;
	}
	public Date getTestDateTime() {
		return testDateTime;
	}
	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}
	public SimpleTestResultType getHaemoglobinuria() {
		return haemoglobinuria;
	}
	public void setHaemoglobinuria(SimpleTestResultType haemoglobinuria) {
		this.haemoglobinuria = haemoglobinuria;
	}
	public SimpleTestResultType getProteinuria() {
		return proteinuria;
	}
	public void setProteinuria(SimpleTestResultType proteinuria) {
		this.proteinuria = proteinuria;
	}
	public SimpleTestResultType getHematuria() {
		return hematuria;
	}
	public void setHematuria(SimpleTestResultType hematuria) {
		this.hematuria = hematuria;
	}
	public Integer getArterialVenousGasPH() {
		return arterialVenousGasPH;
	}
	public void setArterialVenousGasPH(Integer arterialVenousGasPH) {
		this.arterialVenousGasPH = arterialVenousGasPH;
	}
	public Integer getArterialVenousGasPco2() {
		return arterialVenousGasPco2;
	}
	public void setArterialVenousGasPco2(Integer arterialVenousGasPco2) {
		this.arterialVenousGasPco2 = arterialVenousGasPco2;
	}
	public Integer getArterialVenousGasPao2() {
		return arterialVenousGasPao2;
	}
	public void setArterialVenousGasPao2(Integer arterialVenousGasPao2) {
		this.arterialVenousGasPao2 = arterialVenousGasPao2;
	}
	public Integer getArterialVenousGasHco3() {
		return arterialVenousGasHco3;
	}
	public void setArterialVenousGasHco3(Integer arterialVenousGasHco3) {
		this.arterialVenousGasHco3 = arterialVenousGasHco3;
	}
	public Integer getGasOxygenTherapy() {
		return gasOxygenTherapy;
	}
	public void setGasOxygenTherapy(Integer gasOxygenTherapy) {
		this.gasOxygenTherapy = gasOxygenTherapy;
	}
	public Integer getAltSgpt() {
		return altSgpt;
	}
	public void setAltSgpt(Integer altSgpt) {
		this.altSgpt = altSgpt;
	}
	public Integer getAstSgot() {
		return astSgot;
	}
	public void setAstSgot(Integer astSgot) {
		this.astSgot = astSgot;
	}
	public Integer getCreatinine() {
		return creatinine;
	}
	public void setCreatinine(Integer creatinine) {
		this.creatinine = creatinine;
	}
	public Integer getPotassium() {
		return potassium;
	}
	public void setPotassium(Integer potassium) {
		this.potassium = potassium;
	}
	public Integer getUrea() {
		return urea;
	}
	public void setUrea(Integer urea) {
		this.urea = urea;
	}
	public Integer getHaemoglobin() {
		return haemoglobin;
	}
	public void setHaemoglobin(Integer haemoglobin) {
		this.haemoglobin = haemoglobin;
	}
	public Integer getTotalBilirubin() {
		return totalBilirubin;
	}
	public void setTotalBilirubin(Integer totalBilirubin) {
		this.totalBilirubin = totalBilirubin;
	}
	public Integer getConjBilirubin() {
		return conjBilirubin;
	}
	public void setConjBilirubin(Integer conjBilirubin) {
		this.conjBilirubin = conjBilirubin;
	}
	public Integer getWbcCount() {
		return wbcCount;
	}
	public void setWbcCount(Integer wbcCount) {
		this.wbcCount = wbcCount;
	}
	public Integer getPlatelets() {
		return platelets;
	}
	public void setPlatelets(Integer platelets) {
		this.platelets = platelets;
	}
	public Integer getProthrombinTime() {
		return prothrombinTime;
	}
	public void setProthrombinTime(Integer prothrombinTime) {
		this.prothrombinTime = prothrombinTime;
	}
	public String getOtherTestResults() {
		return otherTestResults;
	}
	public void setOtherTestResults(String otherTestResults) {
		this.otherTestResults = otherTestResults;
	}
	
}
