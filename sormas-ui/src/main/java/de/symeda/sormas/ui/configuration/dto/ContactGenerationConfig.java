package de.symeda.sormas.ui.configuration.dto;

import de.symeda.sormas.api.Disease;
import java.time.LocalDate;

public class ContactGenerationConfig extends BaseGenerationConfig{

  private boolean createWithoutSourceCases;
  private boolean createMultipleContactsPerPerson;
  private boolean createWithVisits;

  public ContactGenerationConfig() {
    loadDefaultConfig();
  }

  public void loadDefaultConfig() {
    entityCount = "10";
    startDate = LocalDate.now().minusDays(90);
    endDate = LocalDate.now();
    disease = null;
    region = null;
    district = null;
    createWithoutSourceCases = false;
    createMultipleContactsPerPerson = false;
    createWithVisits = false;
  }

  public void loadPerformanceTestConfig() {
    entityCount = "50";
    startDate = LocalDate.now().minusDays(90);
    endDate = LocalDate.now();
    disease = Disease.CORONAVIRUS;
    region = null;
    district = null;
    createWithoutSourceCases = false;
    createMultipleContactsPerPerson = false;
    createWithVisits = false;
  }

  public boolean isCreateWithoutSourceCases() {
    return createWithoutSourceCases;
  }

  public void setCreateWithoutSourceCases(boolean createWithoutSourceCases) {
    this.createWithoutSourceCases = createWithoutSourceCases;
  }

  public boolean isCreateMultipleContactsPerPerson() {
    return createMultipleContactsPerPerson;
  }

  public void setCreateMultipleContactsPerPerson(boolean createMultipleContactsPerPerson) {
    this.createMultipleContactsPerPerson = createMultipleContactsPerPerson;
  }

  public boolean isCreateWithVisits() {
    return createWithVisits;
  }

  public void setCreateWithVisits(boolean createWithVisits) {
    this.createWithVisits = createWithVisits;
  }
}
