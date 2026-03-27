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

@Controller
@RequestMapping("/case")
public class CaseController {
    private final Logger log = LoggerFactory.getLogger(CaseController.class);
    @Autowired
    private CaseService caseService;
    @Autowired
    private WorkflowTaskService workflowTaskService;

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
        return "redirect:/case/home";

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
            // model.addAttribute("genderOptions", List.of("Male", "Female", "Other")); // If needed
            return "new_registration";
        }
        caseService.submitCaseRegistration(person);

        return "redirect:/case/home";

    }

    @PostMapping("/registration")
    public String startCaseRegistration() {
        caseService.startCaseProcess("case_regV5");
        log.info("case registration V5 started");
        return "redirect:/case/home";

    }


    @PostMapping("/ssnValidation")
    @ResponseBody
    public String validateSSN(@RequestBody Map<String, Object> map) {

        //log.info("validateSSN started");
        String ssn = (String) map.get("ssn");
        log.info("SSN:" + ssn + " successfully validated");
        return "true";

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
        //call DOCCS & Rikers services by passing ssn, dob and name
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
        String view = "redirect:/case/home";


        log.info("Task Key :" + task.getTaskDefinitionKey());
        switch (task.getTaskDefinitionKey()) {
            case "caseDetailsTask":
                populateCaseDetailsForValidation(task, model);
                view = "validateCaseForm";
                break;
            case "doccsManualTask":
                populateCaseDetailsForValidation(task, model);
                view = "reviewIncarcerationForm";
                break;
            case "finalReviewTask":


                break;
            case "ssnManualTask":


                break;
            default:
                log.info(task.getTaskDefinitionKey());

        }
        return view;
        //  workflowTaskService.completeTask(taskId, processInstanceId, model);
    }

    public void populateCaseDetailsForValidation(Task task, Model model) {
        model.addAttribute("person", caseService.getPersonData(task.getProcessInstanceId()));
        model.addAttribute("taskId", task.getId());
    }

    @PostMapping("/completeValidation")
    public String completeValidation(@RequestParam String taskId, Model model) {

        taskService.complete(taskId);
        return "redirect:/case/home";
    }

    @GetMapping("/cleanup")
    @ResponseBody
    public String cleanAllData() {
        return caseService.cleanAllData();
    }

    @PostMapping("/completeIncarcerationReview")
    public String completeIncarcerationReview(@RequestParam String taskId, Model model, @ModelAttribute("person") Person person) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        runtimeService.setVariable(task.getProcessInstanceId(), "incarcerationStatus", person.getUpdateIncarcerationStatus());
        //  taskService.complete(taskId);
        return "redirect:/case/home";
    }
    @PostMapping("/searchPerson")
    @ResponseBody
    public boolean searchPerson(@RequestBody Map<String, Object> map) {
        //log.info("searchPerson started for " + map.get("firstName") + " " + map.get("lastName"));
        return false;
    }

    @PostMapping("/case/completeAddressReview")
    public String completeAddressReview(@RequestParam String taskId,
                                        @RequestParam String street,
                                        @RequestParam String city,
                                        @RequestParam String state,
                                        @RequestParam(required = false) String updatePostalCode) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();

        // Update the process variables with the possibly edited fields
        runtimeService.setVariable(processInstanceId, "street", street);
        runtimeService.setVariable(processInstanceId, "city", city);
        runtimeService.setVariable(processInstanceId, "state", state);

        if (updatePostalCode != null && !updatePostalCode.isEmpty()) {
            runtimeService.setVariable(processInstanceId, "postalCode", updatePostalCode);
        }

        // Complete the review task
        taskService.complete(taskId);

        return "redirect:/case/dashboard";
    }

}
