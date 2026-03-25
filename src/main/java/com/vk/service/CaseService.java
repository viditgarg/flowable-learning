package com.vk.service;


import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
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

    public ProcessInstance startCaseProcess(String processKey) {
//        Map<String, Object> variables = new HashMap<>();
//        variables.put("firstName", "Mister");
//        variables.put("lastName", "Lal");

        return runtimeService.startProcessInstanceByKey(processKey);
    }

    public List<ProcessInstance> getRunningProcesses() {
        List<ProcessInstance> piList =
                runtimeService.createProcessInstanceQuery()
                        .processDefinitionKey("case_regV5")
                        .list();
        return piList;
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
}
