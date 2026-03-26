package org.ny.its.service;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.ny.its.dto.Person;
import org.ny.its.dto.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class WorkflowTaskService {

    private final Logger log = LoggerFactory.getLogger(WorkflowTaskService.class);

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final IdentityService identityService;

    public ProcessInstance startProcess(String processKey) {
        return runtimeService.startProcessInstanceByKey(processKey);
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

    public List<TaskDTO> getTasksByUser(String user) {
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(user)
                .list();

        return tasks.stream().map(
                t -> new TaskDTO(
                        t.getId(),
                        t.getName(),
                        t.getAssignee(),
                        t.getProcessInstanceId(),
                        t.getProcessDefinitionId(),
                        t.getTaskDefinitionKey()
                )).toList();
    }

    public void claimTask(String taskId, String user) {
        taskService.claim(taskId, user);
    }

    public String completeTask(String taskId, String processInstanceId, Model model) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String processDefinitionKey = processInstance.getProcessDefinitionKey();
        // 2. Fetch process variables
        Map<String, Object> vars = runtimeService.getVariables(task.getProcessInstanceId());

        // 3. Populate DTO for form
        Person person = new Person();
        person.setFirstName((String) vars.get("firstName"));
        person.setLastName((String) vars.get("lastName"));
        person.setDateofbirth((LocalDate) vars.get("dateofbirth"));
        person.setSsn((String) vars.get("ssn"));
        person.setEmail((String) vars.get("email"));
        person.setGender((String) vars.get("gender"));

        // 4. Send to Thymeleaf form
        model.addAttribute("person", person);
        model.addAttribute("taskId", taskId);
        String nextView = "casedashboard";
        if (processDefinitionKey != null && processDefinitionKey.equals("case_regV5")) {
            switch (task.getTaskDefinitionKey()) {
                case "caseDetailsTask":
                    log.info("Task Key :" + task.getTaskDefinitionKey());
                    completeCaseDetailsTask(task);
                    nextView = "validateCaseForm";
                    break;
                case "doccsManualTask":
                    log.info("Task Key :" + task.getTaskDefinitionKey());
                    completeCaseCreationProcessTask(taskId);
                    break;
                case "finalReviewTask":
                    log.info("Task Key :" + task.getTaskDefinitionKey());
                    completeCaseCreationProcessTask(taskId);
                    break;
                case "ssnManualTask":
                    log.info("Task Key :" + task.getTaskDefinitionKey());
                    completeCaseCreationProcessTask(taskId);
                    break;
                default:
                    log.info(task.getTaskDefinitionKey());
                    completeCaseCreationProcessTask(taskId);

            }

            log.info("Process def key is case_regV5 ");

        } else {
            log.info("Process def key is other ");
            taskService.complete(taskId);
        }
        return nextView;
    }

    private void completeCaseDetailsTask(Task task) {
        log.info("Task key is " + task.getTaskDefinitionKey());
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("firstName", "Sister");
        variables.put("lastName", "Bean");
        variables.put("dateofbirth", "1980-01-01");
        variables.put("gender", "Female");
        variables.put("ssn", "821456987");
        log.info("First Name " + variables.get("firstName"));
        taskService.complete(task.getId(), variables);
    }

    private void completeCaseCreationProcessTask(String taskId) {
        log.info("Task id is " + taskId);

        taskService.complete(taskId);
    }

    public List<ProcessInstance> getRunningProcesses() {
        List<ProcessInstance> piList = runtimeService.createProcessInstanceQuery().list();
        piList.forEach(pi -> {
            log.info(pi.getName());
        });

        return piList;
    }

    public void createUser(String id, String firstName, String lastName, String email) {
        User user = identityService.newUser(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        identityService.saveUser(user);
    }

    public List<User> getUsers() {
        List<User> users = identityService.createUserQuery().list();
        //log.info((long) users.size());
        return users;
    }

    public ResponseEntity<String> deleteUser(String id) {

        identityService.deleteUser(id);
        return ResponseEntity.ok("User with ID " + id + " deleted successfully.");
    }
}
