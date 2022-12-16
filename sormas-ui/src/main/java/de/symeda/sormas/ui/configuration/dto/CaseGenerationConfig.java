package de.symeda.sormas.ui.configuration.dto;

import de.symeda.sormas.api.Disease;
import java.time.LocalDate;

public class CaseGenerationConfig extends BaseGenerationConfig{

  public CaseGenerationConfig() {
    loadDefaultConfig();
  }

  public void loadDefaultConfig() {
    entityCount = "10";
    startDate = LocalDate.now().minusDays(90);
    endDate = LocalDate.now();
    disease = null;
    region = null;
    district = null;
  }

  public void loadPerformanceTestConfig() {
    entityCount = "50";
    startDate = LocalDate.now().minusDays(90);
    endDate = LocalDate.now();
    disease = Disease.CORONAVIRUS;
    region = null;
    district = null;
  }
}
