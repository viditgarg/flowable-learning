package com.vk.controller;

import com.vk.dto.TaskDTO;
import com.vk.service.CaseService;
import com.vk.service.WorkflowTaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @PostMapping("/start")
    public String startNewCaseProcess() {
        caseService.startCaseProcess("caseCreationProcess");
        log.info("caseCreationProcess started");
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
        log.info("prisonerValidation started");
        String firstName = (String) map.get("firstName");
        String lastName = (String) map.get("lastName");
        String birthDate = (String) map.get("dob");
        String ssn = (String) map.get("ssn");
        String gender = (String) map.get("gender");
        log.info("FirstName:" + firstName + " LastName:" + lastName + " BirthDate:" + birthDate + " ssn:" + ssn + " gender:" + gender);

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

        List<ProcessInstance> processes =
                caseService.getRunningProcesses();

        List<TaskDTO> tasks;

        if (user != null && !user.isEmpty()) {
            tasks = workflowTaskService.getTasksByUser(user);
        } else {
            tasks = workflowTaskService.getAllTasks();
        }

        model.addAttribute("processes", processes);
        model.addAttribute("tasks", tasks);
        model.addAttribute("users", workflowTaskService.getUsers());

        return "casedashboard";
    }

    @PostMapping("/tasks/complete")
    public String completeTask(@RequestParam String taskId, @RequestParam String processInstanceId) {

        workflowTaskService.completeTask(taskId, processInstanceId);

        return "redirect:/case/home";
    }

    @GetMapping("/cleanup")
    @ResponseBody
    public String cleanAllData() {
        return caseService.cleanAllData();
    }

    @PostMapping("/searchPerson")
    @ResponseBody
    public boolean searchPerson(@RequestBody Map<String, Object> map) {
        log.info("searchPerson started for " + map.get("firstName") + " " + map.get("lastName"));
        return false;
    }
}
