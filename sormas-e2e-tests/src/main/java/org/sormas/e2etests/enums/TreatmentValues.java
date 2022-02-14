package org.sormas.e2etests.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum TreatmentValues {
  DRUG_INTAKE("Drug intake"),
  ORAL_REHYDRATION_SALTS("Oral rehydration salts"),
  BLOOD_TRANSFUSION("Blood transfusion"),
  RENAL_REPLACEMENT_THERAPY("Renal replacement therapy"),
  IV_FLUID_THERAPY("IV fluid therapy"),
  OXYGEN_THERAPY("Oxygen therapy"),
  INVASIVE_MECHANICAL_VENTILATION("Invasive mechanical ventilation"),
  VASOPRESSORS_INOTROPES("Vasopressors/Inotropes"),
  OTHER("Other");

  private final String valueType;

  TreatmentValues(String valueType) {

    this.valueType = valueType;
  }

  /** Returns values used for UI tests */
  public static String getRandomTherapyTreatmentValues() {
    Random random = new Random();
    return String.valueOf(TreatmentValues.values()[random.nextInt(values().length)].valueType);
  }

  /** Returns values used for UI tests */
  public static String getRandomTherapyTreatmentValuesWithoutOther() {
    Random random = new Random();
    List<TreatmentValues> tValues =
        Arrays.stream(TreatmentValues.values())
            .filter(x -> !StringUtils.equals(x.getValueType(), "Other"))
            .collect(Collectors.toList());
    return tValues.get(random.nextInt(tValues.size())).valueType;
  }
}
