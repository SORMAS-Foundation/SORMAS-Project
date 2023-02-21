package de.symeda.sormas.ui.configuration.generate.config;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.ui.configuration.generate.config.BaseGenerationConfig;
import java.time.LocalDate;

public class EventGenerationConfig extends BaseGenerationConfig {

  private String minParticipantsPerEvent;
  private String maxParticipantsPerEvent;
  private String minContactsPerParticipant;
  private String maxContactsPerParticipant;
  private String percentageOfCases;

  public EventGenerationConfig() {
    loadDefaultConfig();
  }

  public void loadDefaultConfig() {
    entityCount = "10";
    startDate = LocalDate.now().minusDays(90);
    endDate = LocalDate.now();
    disease = null;
    region = null;
    district = null;
    minParticipantsPerEvent = "3";
    maxParticipantsPerEvent = "10";
    minContactsPerParticipant = "0";
    maxContactsPerParticipant = "3";
    percentageOfCases = "20";
  }

  public void loadPerformanceTestConfig() {
    entityCount = "30";
    startDate = LocalDate.now().minusDays(90);
    endDate = LocalDate.now();
    disease = Disease.CORONAVIRUS;
    // region?
    // district?
    minParticipantsPerEvent = "3";
    maxParticipantsPerEvent = "8";
    minContactsPerParticipant = "0";
    maxContactsPerParticipant = "2";
    percentageOfCases = "15";
  }

  public String getMinParticipantsPerEvent() {
    return minParticipantsPerEvent;
  }

  public void setMinParticipantsPerEvent(String minParticipantsPerEvent) {
    this.minParticipantsPerEvent = minParticipantsPerEvent;
  }

  public String getMaxParticipantsPerEvent() {
    return maxParticipantsPerEvent;
  }

  public void setMaxParticipantsPerEvent(String maxParticipantsPerEvent) {
    this.maxParticipantsPerEvent = maxParticipantsPerEvent;
  }

  public String getMinContactsPerParticipant() {
    return minContactsPerParticipant;
  }

  public void setMinContactsPerParticipant(String minContactsPerParticipant) {
    this.minContactsPerParticipant = minContactsPerParticipant;
  }

  public String getMaxContactsPerParticipant() {
    return maxContactsPerParticipant;
  }

  public void setMaxContactsPerParticipant(String maxContactsPerParticipant) {
    this.maxContactsPerParticipant = maxContactsPerParticipant;
  }

  public String getPercentageOfCases() {
    return percentageOfCases;
  }

  public void setPercentageOfCases(String percentageOfCases) {
    this.percentageOfCases = percentageOfCases;
  }
}
