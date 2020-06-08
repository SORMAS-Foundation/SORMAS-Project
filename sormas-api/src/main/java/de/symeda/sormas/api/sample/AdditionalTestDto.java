package de.symeda.sormas.api.sample;

import java.util.Date;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Order;

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
	private Float arterialVenousGasPH;
	private Float arterialVenousGasPco2;
	private Float arterialVenousGasPao2;
	private Float arterialVenousGasHco3;
	private Float gasOxygenTherapy;
	private Float altSgpt;
	private Float astSgot;
	private Float creatinine;
	private Float potassium;
	private Float urea;
	private Float haemoglobin;
	private Float totalBilirubin;
	private Float conjBilirubin;
	private Float wbcCount;
	private Float platelets;
	private Float prothrombinTime;
	private String otherTestResults;

	public static AdditionalTestDto build(SampleReferenceDto sample) {
		AdditionalTestDto additionalTest = new AdditionalTestDto();
		additionalTest.setUuid(DataHelper.createUuid());
		additionalTest.setSample(sample);
		return additionalTest;
	}

	public boolean hasArterialVenousGasValue() {
		return arterialVenousGasPH != null || arterialVenousGasPco2 != null || arterialVenousGasPao2 != null || arterialVenousGasHco3 != null;
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
			sb.append(sb.length() > 0 ? " - " + paO2String + ": " : paO2String + ": ").append(arterialVenousGasPao2);
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

	@Order(0)
	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}

	@Order(10)
	public SimpleTestResultType getHaemoglobinuria() {
		return haemoglobinuria;
	}

	public void setHaemoglobinuria(SimpleTestResultType haemoglobinuria) {
		this.haemoglobinuria = haemoglobinuria;
	}

	@Order(11)
	public SimpleTestResultType getProteinuria() {
		return proteinuria;
	}

	public void setProteinuria(SimpleTestResultType proteinuria) {
		this.proteinuria = proteinuria;
	}

	@Order(12)
	public SimpleTestResultType getHematuria() {
		return hematuria;
	}

	public void setHematuria(SimpleTestResultType hematuria) {
		this.hematuria = hematuria;
	}

	@Order(13)
	public Float getArterialVenousGasPH() {
		return arterialVenousGasPH;
	}

	public void setArterialVenousGasPH(Float arterialVenousGasPH) {
		this.arterialVenousGasPH = arterialVenousGasPH;
	}

	@Order(14)
	public Float getArterialVenousGasPco2() {
		return arterialVenousGasPco2;
	}

	public void setArterialVenousGasPco2(Float arterialVenousGasPco2) {
		this.arterialVenousGasPco2 = arterialVenousGasPco2;
	}

	@Order(15)
	public Float getArterialVenousGasPao2() {
		return arterialVenousGasPao2;
	}

	public void setArterialVenousGasPao2(Float arterialVenousGasPao2) {
		this.arterialVenousGasPao2 = arterialVenousGasPao2;
	}

	@Order(16)
	public Float getArterialVenousGasHco3() {
		return arterialVenousGasHco3;
	}

	public void setArterialVenousGasHco3(Float arterialVenousGasHco3) {
		this.arterialVenousGasHco3 = arterialVenousGasHco3;
	}

	@Order(17)
	public Float getGasOxygenTherapy() {
		return gasOxygenTherapy;
	}

	public void setGasOxygenTherapy(Float gasOxygenTherapy) {
		this.gasOxygenTherapy = gasOxygenTherapy;
	}

	@Order(20)
	public Float getAltSgpt() {
		return altSgpt;
	}

	public void setAltSgpt(Float altSgpt) {
		this.altSgpt = altSgpt;
	}

	@Order(21)
	public Float getAstSgot() {
		return astSgot;
	}

	public void setAstSgot(Float astSgot) {
		this.astSgot = astSgot;
	}

	@Order(22)
	public Float getCreatinine() {
		return creatinine;
	}

	public void setCreatinine(Float creatinine) {
		this.creatinine = creatinine;
	}

	@Order(23)
	public Float getPotassium() {
		return potassium;
	}

	public void setPotassium(Float potassium) {
		this.potassium = potassium;
	}

	@Order(24)
	public Float getUrea() {
		return urea;
	}

	public void setUrea(Float urea) {
		this.urea = urea;
	}

	@Order(25)
	public Float getHaemoglobin() {
		return haemoglobin;
	}

	public void setHaemoglobin(Float haemoglobin) {
		this.haemoglobin = haemoglobin;
	}

	@Order(26)
	public Float getTotalBilirubin() {
		return totalBilirubin;
	}

	public void setTotalBilirubin(Float totalBilirubin) {
		this.totalBilirubin = totalBilirubin;
	}

	@Order(27)
	public Float getConjBilirubin() {
		return conjBilirubin;
	}

	public void setConjBilirubin(Float conjBilirubin) {
		this.conjBilirubin = conjBilirubin;
	}

	@Order(30)
	public Float getWbcCount() {
		return wbcCount;
	}

	public void setWbcCount(Float wbcCount) {
		this.wbcCount = wbcCount;
	}

	@Order(31)
	public Float getPlatelets() {
		return platelets;
	}

	public void setPlatelets(Float platelets) {
		this.platelets = platelets;
	}

	@Order(32)
	public Float getProthrombinTime() {
		return prothrombinTime;
	}

	public void setProthrombinTime(Float prothrombinTime) {
		this.prothrombinTime = prothrombinTime;
	}

	@Order(33)
	public String getOtherTestResults() {
		return otherTestResults;
	}

	public void setOtherTestResults(String otherTestResults) {
		this.otherTestResults = otherTestResults;
	}
}
