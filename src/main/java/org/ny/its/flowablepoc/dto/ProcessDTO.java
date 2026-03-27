package org.ny.its.flowablepoc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDTO {
    private String processDefinitionKey;
    private String processInstanceId;
    private String processDefinitionName;
    private String processDefinitionId;
    private String applicantName;
    private Date startTime;

}
