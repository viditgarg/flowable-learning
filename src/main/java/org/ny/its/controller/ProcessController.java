package org.ny.its.controller;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.User;
import org.ny.its.dto.TaskDTO;
import org.ny.its.service.WorkflowTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class ProcessController {

    private final WorkflowTaskService workflowTaskService;

    @GetMapping("/process/start")
    public String startProcess() {
        workflowTaskService.startProcess("approvalProcess");

        return "redirect:/";

    }

    @PostMapping("/process/start")
    public String startApprovalProcess() {
        workflowTaskService.startProcess("approvalProcess");

        return "redirect:/";
        // "Approval Process Started";

        //runtimeService.startProcessInstanceByKey("helloProcess");
        //return "Process Started";
    }

    @PostMapping("/tasks/complete")
    public String completeTask(@RequestParam String taskId, @RequestParam String processInstanceId) {

        workflowTaskService.completeTask(taskId, processInstanceId);

        return "redirect:/";
    }

    @PostMapping("/tasks/claim")
    public String claimTask(@RequestParam String taskId,
                            @RequestParam String user) {

        workflowTaskService.claimTask(taskId, user);

        return "redirect:/";
    }


    @GetMapping("/")
    public String dashboard(@RequestParam(required = false) String user, Model model) {

        List<ProcessInstance> processes =
                workflowTaskService.getRunningProcesses();

        List<TaskDTO> tasks;

        if (user != null && !user.isEmpty()) {
            tasks = workflowTaskService.getTasksByUser(user);
        } else {
            tasks = workflowTaskService.getAllTasks();
        }

        model.addAttribute("processes", processes);
        model.addAttribute("tasks", tasks);
        model.addAttribute("users", workflowTaskService.getUsers());

        return "dashboard";
    }

    @GetMapping("/tasks")
    @ResponseBody
    public List<TaskDTO> getTasks() {
        return workflowTaskService.getAllTasks();
    }

    @PostMapping("/users/create")
    @ResponseBody
    public String createUser(@RequestParam String id, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String email) {
        workflowTaskService.createUser(id, firstName, lastName, email);
        return "User created";
    }

    @GetMapping("/users")
    @ResponseBody
    public List<User> getUsers() {
        return workflowTaskService.getUsers();
    }

    @DeleteMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        return workflowTaskService.deleteUser(id);
    }
}
