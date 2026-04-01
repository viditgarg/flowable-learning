package org.ny.its.flowablepoc.controller;

import jakarta.validation.Valid;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.ny.its.flowablepoc.dto.Person;
import org.ny.its.flowablepoc.dto.ProcessDTO;
import org.ny.its.flowablepoc.dto.TaskDTO;
import org.ny.its.flowablepoc.service.CaseService;
import org.ny.its.flowablepoc.service.SimpleSoapService;
import org.ny.its.flowablepoc.service.WorkflowTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.ny.its.flowablepoc.util.ProcessFlowConstants.*;

@Controller
@RequestMapping("/case")
public class CaseController {

    private final Logger log = LoggerFactory.getLogger(CaseController.class);
    @Autowired
    private CaseService caseService;
    @Autowired
    private WorkflowTaskService workflowTaskService;

    @Autowired
    private SimpleSoapService simpleSoapService;

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final IdentityService identityService;

    public CaseController(RuntimeService runtimeService, TaskService taskService, IdentityService identityService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.identityService = identityService;
    }

    @PostMapping("/start")
    public String startNewCaseProcess() {
        caseService.startCaseProcess("caseCreationProcess");
        log.info("caseCreationProcess started");
        return REDIRECT_CASE_HOME;

    }

    @GetMapping("/new_registration")
    public String newCaseRegistration(Model model) {
        model.addAttribute("person", new Person());
        return "newCase";
    }

    @PostMapping("/submitRegistration")
    public String submitCaseRegistration(@Valid @ModelAttribute("person") Person person, // @Valid triggers validation
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            // If validation fails, return to the form page
            // Errors are automatically available in the template via #fields
            // Ensure any lists needed for dropdowns are re-added if necessary
            // model.addAttribute("genderOptions", List.of("Male", "Female", "Other")); //
            // If needed
            return "new_registration";
        }
        caseService.submitCaseRegistration(person);

        return REDIRECT_CASE_HOME;

    }

    @PostMapping("/registration")
    public String startCaseRegistration() {
        caseService.startCaseProcess("case_regV5");
        log.info("case registration V5 started");
        return REDIRECT_CASE_HOME;

    }

    /*--
    This rest endpoint is called from process flow to validate ssn
     */
    @PostMapping("/ssnValidation")
    @ResponseBody
    public boolean validateSSN(@RequestBody Map<String, Object> map) {

        // log.info("validateSSN started");
        String ssn = (String) map.get("ssn");
        int ssn_digits = Integer.parseInt(ssn);
        String ssn_words = simpleSoapService.convert(ssn_digits);

        double randomValue = Math.random();
        boolean valid = false;
        // valid = randomValue < 0.5;
        log.info("SSN:" + ssn + " successfully validated, returning SSN_VALID as " + valid);
        return valid; // randomValue < 0.5;;

    }

    @PostMapping("/prisonerValidation")
    @ResponseBody
    public String prisonerValidation(@RequestBody Map<String, Object> map) {

        String firstName = (String) map.get("firstName");
        String lastName = (String) map.get("lastName");
        log.info("Validating incarceration with Rikers, DOCCS services for " + firstName + " " + lastName);
        String birthDate = (String) map.get("dob");
        String ssn = (String) map.get("ssn");
        String gender = (String) map.get("gender");
        // call DOCCS & Rikers services by passing ssn, dob and name
        return "true";

    }

    @GetMapping("/start")
    @ResponseBody
    public String startCaseProcess() {
        caseService.startCaseProcess("case_regV5");
        log.info("case Registration Process started");
        return "Case Registration Process Started";

    }

    @GetMapping("/home")
    public String dashboard(@RequestParam(required = false) String user, Model model) {
        List<ProcessDTO> processList = caseService.getRunningProcesses();
        List<TaskDTO> tasks;

        if (user != null && !user.isEmpty()) {
            tasks = workflowTaskService.getTasksByUser(user);
        } else {
            tasks = caseService.getAllTasks();
        }

        model.addAttribute("processes", processList);
        model.addAttribute("tasks", tasks);
        model.addAttribute("users", workflowTaskService.getUsers());

        return "casedashboard";
    }

    @PostMapping("/tasks/complete")
    public String completeTask(@RequestParam String taskId, @RequestParam String processInstanceId, Model model) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String view = REDIRECT_CASE_HOME;

        log.info("Task Key :" + task.getTaskDefinitionKey());
        populateCaseDetailsForValidation(task, model);
        switch (task.getTaskDefinitionKey()) {
            case CASE_DETAILS_TASK:
                view = "validateCaseForm";
                break;
            case DOCCS_MANUAL_TASK:
                view = "reviewIncarcerationForm";
                break;
            case FINAL_REVIEW_TASK:
                populateAddressDetails(task, model);
                view = "finalReviewForm";
                break;
            case SSN_MANUAL_TASK:
                view = "reviewSSNForm";
                break;
            case ADDRESS_ENTRY_TASK:
                view = "addressForm";
                model.addAttribute("postalCode", runtimeService.getVariable(processInstanceId, "postalCode"));
                break;
            case ADDRESS_REVIEW_TASK:
                populateAddressDetails(task, model);
                view = "addressReviewForm";
                break;
            default:
                log.info("Default Switch Case, task key::" + task.getTaskDefinitionKey());

        }
        return view;
        // workflowTaskService.completeTask(taskId, processInstanceId, model);
    }

    @PostMapping("/completeCaseReviewTask")
    public String completeCaseReviewTask(@RequestParam String taskId) {
        taskService.complete(taskId);
        return REDIRECT_CASE_HOME;
    }

    private void populateCaseDetailsForValidation(Task task, Model model) {
        model.addAttribute("person", caseService.getPersonData(task.getProcessInstanceId()));
        model.addAttribute("taskId", task.getId());
    }

    private void populateAddressDetails(Task task, Model model) {
        model.addAttribute("street", runtimeService.getVariable(task.getProcessInstanceId(), "street"));
        model.addAttribute("postalCode", runtimeService.getVariable(task.getProcessInstanceId(), "postalCode"));
        model.addAttribute("city", runtimeService.getVariable(task.getProcessInstanceId(), "city"));
        model.addAttribute("state", runtimeService.getVariable(task.getProcessInstanceId(), "state"));
    }

    @PostMapping("/completeValidation")
    public String completeValidation(@RequestParam String taskId, Model model) {

        taskService.complete(taskId);
        return REDIRECT_CASE_HOME;
    }

    @GetMapping("/cleanup")
    @ResponseBody
    public String cleanAllData() {
        return caseService.cleanAllData();
    }

    @PostMapping("/completeIncarcerationReview")
    public String completeIncarcerationReview(@RequestParam String taskId, Model model,
            @ModelAttribute("person") Person person) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        runtimeService.setVariable(task.getProcessInstanceId(), "incarcerationStatus",
                person.getUpdateIncarcerationStatus());
        taskService.complete(taskId);
        log.info("Incarceration Review Completed, TaskID::" + taskId);
        return REDIRECT_CASE_HOME;
    }

    @PostMapping("/completeSSNReview")
    public String completeSSNReview(@RequestParam String taskId, Model model, @ModelAttribute("person") Person person) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        runtimeService.setVariable(task.getProcessInstanceId(), "SSN_Valid", person.getUpdateSSNValid());
        taskService.complete(taskId);
        log.info("SSN Review Completed, TaskID::" + taskId);
        return REDIRECT_CASE_HOME;
    }

    @PostMapping("/searchPerson")
    @ResponseBody
    public boolean searchPerson(@RequestBody Map<String, Object> map) {
        // log.info("searchPerson started for " + map.get("firstName") + " " +
        // map.get("lastName"));
        return false;
    }

    @PostMapping("/completeAddressReview")
    public String completeAddressReview(@RequestParam String taskId,
            @RequestParam String street,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam(required = false) String updatePostalCode) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();

        // Update the process variables with the possibly edited fields
        runtimeService.setVariable(processInstanceId, STREET, street);
        runtimeService.setVariable(processInstanceId, CITY, city);
        runtimeService.setVariable(processInstanceId, STATE, state);

        if (updatePostalCode != null && !updatePostalCode.isEmpty()) {
            runtimeService.setVariable(processInstanceId, POSTAL_CODE, updatePostalCode);
        }

        // Complete the review task
        taskService.complete(taskId);

        return REDIRECT_CASE_HOME;
    }

    @PostMapping("/completeAddressTask")
    public String completeAddressTask(@RequestParam String taskId,
            @RequestParam String street,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String postalCode,
            @RequestParam(required = false) String updatePostalCode) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();

        // Update the process variables with the possibly edited fields
        runtimeService.setVariable(processInstanceId, STREET, street);
        runtimeService.setVariable(processInstanceId, CITY, city);
        runtimeService.setVariable(processInstanceId, STATE, state);
        runtimeService.setVariable(processInstanceId, POSTAL_CODE, postalCode);

        // Complete the review task
        taskService.complete(taskId);

        return REDIRECT_CASE_HOME;
    }

}
