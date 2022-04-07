package org.sormas.e2etests.entities.pojo.web;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class SampleAdditionalTest {
  LocalDate dateOfResult;
  LocalTime timeOfResult;
  String haemoglobinInUrine;
  String proteinInUrine;
  String redBloodCellsInUrine;
  String ph;
  String pCO2;
  String pAO2;
  String hCO3;
  String oxygen;
  String sgpt;
  String totalBilirubin;
  String sgot;
  String conjBilirubin;
  String creatine;
  String wbc;
  String potassium;
  String platelets;
  String urea;
  String prothrombin;
  String haemoglobin;
  String otherResults;
}
