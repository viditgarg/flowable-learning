package org.ny.its.flowablepoc.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private String id;
    private String name;
    private String assignee;
    private String processInstanceId;
    private String processDefinitionId;
    private String taskDefinitionKey;
    private String applicantName;
}
