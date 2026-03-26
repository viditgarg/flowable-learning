package org.ny.its.service;


import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.ny.its.dto.Person;
import org.ny.its.dto.ProcessDTO;
import org.ny.its.dto.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CaseService {
    private final Logger log = LoggerFactory.getLogger(CaseService.class);
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final IdentityService identityService;
    private final HistoryService historyService;

    public static final String CASE_PROCESS_KEY = "case_regV5";

    public ProcessInstance startCaseProcess(String processKey) {
//        Map<String, Object> variables = new HashMap<>();
//        variables.put("firstName", "Mister");
//        variables.put("lastName", "Lal");

        return runtimeService.startProcessInstanceByKey(CASE_PROCESS_KEY);
    }

    public List<ProcessDTO> getRunningProcesses() {
        List<ProcessInstance> piList =
                runtimeService.createProcessInstanceQuery()
                        .processDefinitionKey(CASE_PROCESS_KEY)
                        .list();
        return piList.stream().map(
                p -> new ProcessDTO(
                        p.getProcessDefinitionKey(),
                        p.getProcessInstanceId(),
                        p.getProcessDefinitionName(),
                        p.getProcessDefinitionId(),
                        "Test Name",
                        p.getStartTime()
                )).toList();
    }

    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = taskService.createTaskQuery().list();
        return tasks.stream().map(
                t -> new TaskDTO(
                        t.getId(),
                        t.getName(),
                        t.getAssignee(),
                        t.getProcessInstanceId(),
                        t.getProcessDefinitionId(),
                        t.getTaskDefinitionKey()
                )).toList();
        // return taskService.createTaskQuery().list();
    }

    public String cleanAllData() {
        runtimeService.createProcessInstanceQuery()
                .list()
                .forEach(pi -> runtimeService.deleteProcessInstance(
                        pi.getId(), "cleanup"
                ));


        historyService.createHistoricProcessInstanceQuery()
                .list()
                .forEach(hpi -> historyService.deleteHistoricProcessInstance(hpi.getId()));
        return "cleaned";
    }

    public void submitCaseRegistration(Person person) {
        log.info("Person details: " + person);
        runtimeService.startProcessInstanceByKey(CASE_PROCESS_KEY);
    }
}
