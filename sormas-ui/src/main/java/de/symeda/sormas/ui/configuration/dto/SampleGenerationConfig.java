package de.symeda.sormas.ui.configuration.dto;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import java.time.LocalDate;

public class SampleGenerationConfig extends BaseGenerationConfig{
  private SamplePurpose samplePurpose;
  private SampleMaterial sampleMaterial;
  private String sampleMaterialText;
  private FacilityReferenceDto laboratory;

  private boolean externalLabOrInternalInHouseTesting = false;
  private boolean requestPathogenTestsToBePerformed = false;
  private boolean requestAdditionalTestsToBePerformed = false;
  private boolean sendDispatch = false;
  private boolean received = false;
  private String comment;

  private SampleGenerationConfig() {
  }

  public static SampleGenerationConfig getDefaultConfig() {
    SampleGenerationConfig sampleGenerationConfig = new SampleGenerationConfig();
    sampleGenerationConfig.entityCount = "10";
    sampleGenerationConfig.startDate = LocalDate.now().minusDays(90);
    sampleGenerationConfig.endDate = LocalDate.now();
    sampleGenerationConfig.disease = null;
    sampleGenerationConfig.region = null;
    sampleGenerationConfig.district = null;
    sampleGenerationConfig.samplePurpose = SamplePurpose.INTERNAL;
    sampleGenerationConfig.sampleMaterial = SampleMaterial.BLOOD;
    return sampleGenerationConfig;
  }

  public static SampleGenerationConfig getPerformanceTestConfig() {
    SampleGenerationConfig sampleGenerationConfig = new SampleGenerationConfig();
    sampleGenerationConfig.entityCount = "50";
    sampleGenerationConfig.startDate = LocalDate.now().minusDays(90);
    sampleGenerationConfig.endDate = LocalDate.now();
    sampleGenerationConfig.disease = Disease.CORONAVIRUS;
    sampleGenerationConfig.region = null;
    sampleGenerationConfig.district = null;
    sampleGenerationConfig.samplePurpose = SamplePurpose.EXTERNAL;
    sampleGenerationConfig.sampleMaterial = SampleMaterial.BLOOD;
    sampleGenerationConfig.laboratory = FacadeProvider.getFacilityFacade().getAllActiveLaboratories(false).get(0);
    return sampleGenerationConfig;
  }


  public SamplePurpose getSamplePurpose() {
    return samplePurpose;
  }

  public void setSamplePurpose(SamplePurpose samplePurpose) {
    this.samplePurpose = samplePurpose;
  }

  public SampleMaterial getSampleMaterial() {
    return sampleMaterial;
  }

  public void setSampleMaterial(SampleMaterial sampleMaterial) {
    this.sampleMaterial = sampleMaterial;
  }

  public String getSampleMaterialText() {
    return sampleMaterialText;
  }

  public void setSampleMaterialText(String sampleMaterialText) {
    this.sampleMaterialText = sampleMaterialText;
  }

  public FacilityReferenceDto getLaboratory() {
    return laboratory;
  }

  public void setLaboratory(FacilityReferenceDto laboratory) {
    this.laboratory = laboratory;
  }

  public boolean isExternalLabOrInternalInHouseTesting() {
    return externalLabOrInternalInHouseTesting;
  }

  public void setExternalLabOrInternalInHouseTesting(boolean externalLabOrInternalInHouseTesting) {
    this.externalLabOrInternalInHouseTesting = externalLabOrInternalInHouseTesting;
  }

  public boolean isRequestPathogenTestsToBePerformed() {
    return requestPathogenTestsToBePerformed;
  }

  public void setRequestPathogenTestsToBePerformed(boolean requestPathogenTestsToBePerformed) {
    this.requestPathogenTestsToBePerformed = requestPathogenTestsToBePerformed;
  }

  public boolean isRequestAdditionalTestsToBePerformed() {
    return requestAdditionalTestsToBePerformed;
  }

  public void setRequestAdditionalTestsToBePerformed(boolean requestAdditionalTestsToBePerformed) {
    this.requestAdditionalTestsToBePerformed = requestAdditionalTestsToBePerformed;
  }

  public boolean isSendDispatch() {
    return sendDispatch;
  }

  public void setSendDispatch(boolean sendDispatch) {
    this.sendDispatch = sendDispatch;
  }

  public boolean isReceived() {
    return received;
  }

  public void setReceived(boolean received) {
    this.received = received;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
