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
import org.springframework.ui.Model;

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
        return runtimeService.startProcessInstanceByKey(CASE_PROCESS_KEY);
    }

    public List<ProcessDTO> getRunningProcesses() {
        List<ProcessInstance> piList =
                runtimeService.createProcessInstanceQuery()
                        .processDefinitionKey(CASE_PROCESS_KEY)
                        .list();
        return piList.stream().map(
                p -> {
                    Map<String, Object> vars = runtimeService.getVariables(p.getProcessInstanceId());
                    return new ProcessDTO(
                            p.getProcessDefinitionKey(),
                            p.getProcessInstanceId(),
                            p.getProcessDefinitionName(),
                            p.getProcessDefinitionId(),
                            vars.get("firstName").toString() + " " + vars.get("lastName").toString(),
                            p.getStartTime()
                    );
                }).toList();
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

    public String completeTask(String taskId, Model model) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        String processDefinitionKey = processInstance.getProcessDefinitionKey();

        String nextView = "casedashboard";
        if (processDefinitionKey != null && processDefinitionKey.equals("case_regV5")) {
            switch (task.getTaskDefinitionKey()) {
                case "caseDetailsTask":
                    log.info("Task Key :" + task.getTaskDefinitionKey());
                    completeCaseDetailsTask(task);
                    break;
                default:
                    log.info(task.getTaskDefinitionKey());

            }
            log.info("Process def key is case_regV5 ");
        } else {
            log.info("Process def key is other ");
            taskService.complete(taskId);
        }
        return nextView;
    }

    private void completeCaseDetailsTask(Task task) {
        taskService.complete(task.getId());
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
                .orElse(null));
        ;
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
        person.setIncarcerationStatus(Optional.ofNullable(vars.get("incarcerationStatus"))
                .map(Object::toString)
                .orElse(null));

        return person;
    }


    public void submitCaseRegistration(Person person) {
        log.info("Person details: " + person);
        PersonEntity savedPerson = savePersonDataIntoRepository(person);
        log.info("Saved Person Entity ID: " + savedPerson.getId());
        // Start Flowable process with reference
        Map<String, Object> variables = new HashMap<>();
        variables.put("caseId", savedPerson.getId());
        variables.put("firstName", savedPerson.getFirstName());
        variables.put("lastName", savedPerson.getLastName());
        variables.put("ssn", savedPerson.getSsn());
        variables.put("email", savedPerson.getEmail());
        variables.put("gender", savedPerson.getGender());
        variables.put("dateofbirth", savedPerson.getDateOfBirth());

        runtimeService.startProcessInstanceByKey(CASE_PROCESS_KEY, variables);

    }

    private PersonEntity savePersonDataIntoRepository(Person person) {
        PersonEntity entity = new PersonEntity();
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setEmail(person.getEmail());
        entity.setGender(person.getGender());
        entity.setSsn(person.getSsn());
        entity.setDateOfBirth(person.getDateofbirth());
        entity.setStatus("IN_PROGRESS");

        return personRepository.save(entity);
    }
}
