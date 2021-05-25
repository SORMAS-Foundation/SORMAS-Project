package org.sormas.e2etests.pojo.api.cases;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder(toBuilder = true, builderClassName = "Builder")
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
@NonNull
public class SormasToSormasOriginInfo {
  public Date creationDate;
  public Date changeDate;
  public String uuid;
  public String organizationId;
  public String senderName;
  public String senderEmail;
  public String senderPhoneNumber;
  public boolean ownershipHandedOver;
  public String comment;
}
