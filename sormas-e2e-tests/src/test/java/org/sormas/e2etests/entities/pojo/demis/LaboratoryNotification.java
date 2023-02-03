package org.sormas.e2etests.entities.pojo.demis;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.sormas.e2etests.entities.pojo.demis.labNotificationsChunks.Parameter;

import java.util.ArrayList;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LaboratoryNotification {
    public String resourceType;
    public ArrayList<Parameter> parameter;
}
