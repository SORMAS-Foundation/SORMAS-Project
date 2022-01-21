package org.sormas.e2etests.pojo.web;

import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class EventParticipant {
  String involvementDescription;
  String firstName;
  String lastName;
  String sex;
  String responsibleRegion;
  String responsibleDistrict;
}
