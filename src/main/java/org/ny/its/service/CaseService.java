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
import org.ny.its.entity.PersonEntity;
import org.ny.its.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CaseService {
    private final Logger log = LoggerFactory.getLogger(CaseService.class);
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final IdentityService identityService;
    private final HistoryService historyService;

    @Autowired
    private PersonRepository personRepository;

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

    public Person getPersonData(String processInstanceId) {
        Map<String, Object> vars = runtimeService.getVariables(processInstanceId);
        Person person = new Person();
        person.setFirstName(Optional.ofNullable(vars.get("firstName"))
                .map(Object::toString)
                .orElse(null));
        person.setLastName(Optional.ofNullable(vars.get("lastName"))
                .map(Object::toString)
                .orElse(null));
        person.setEmail(Optional.ofNullable(vars.get("email"))
                .map(Object::toString)
                .orElse(null)); ;
        person.setGender(Optional.ofNullable(vars.get("gender"))
                .map(Object::toString)
                .orElse(null));
        // SSN (null-safe)
        person.setSsn(Optional.ofNullable(vars.get("ssn"))
                .map(Object::toString)
                .orElse(null));

        // Date of Birth (null-safe)
        person.setDateofbirth(Optional.ofNullable(vars.get("dateofbirth"))
                .map(Object::toString)
                .filter(s -> !s.isEmpty())
                .map(LocalDate::parse)
                .orElse(null));

        return person;
    }





    public void submitCaseRegistration(Person person) {
        log.info("Person details: " + person);
        PersonEntity entity = new PersonEntity();
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setEmail(person.getEmail());
        entity.setGender(person.getGender());
        entity.setSsn(person.getSsn());
        entity.setDateOfBirth(person.getDateofbirth());
        entity.setStatus("IN_PROGRESS");

        PersonEntity saved = personRepository.save(entity);
        // Start Flowable process with reference
        Map<String, Object> variables = new HashMap<>();
        variables.put("caseId", saved.getId());
        variables.put("firstName", saved.getFirstName());
        variables.put("lastName", saved.getLastName());
        variables.put("ssn", saved.getSsn());
        variables.put("email", saved.getEmail());
        variables.put("gender", saved.getGender());
        variables.put("dateofbirth", saved.getDateOfBirth());

        runtimeService.startProcessInstanceByKey(CASE_PROCESS_KEY, variables);

        log.info("Saved case ID: " + saved.getId());
    }
}
