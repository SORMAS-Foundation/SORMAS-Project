package gatling.restassured.dtos;

import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
public class Subcontinent {

  String uuid;
  String defaultName;
}
