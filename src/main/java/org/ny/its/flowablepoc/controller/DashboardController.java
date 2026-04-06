package org.ny.its.flowablepoc.controller;

import java.util.Arrays;
import java.util.List;

import org.ny.its.flowablepoc.dto.ProcessDTO;
import org.ny.its.flowablepoc.dto.TaskDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/ui")
public class DashboardController {

    private final RestTemplate restTemplate;

    public DashboardController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/home")
    public String dashboard(@RequestParam(required = false) String user, Model model) {

        // Fetch data from backend API
        List<ProcessDTO> processes = Arrays.asList(
            restTemplate.getForObject("http://localhost:9090/api/processes", ProcessDTO[].class)
        );

        List<TaskDTO> tasks;
        if (user != null && !user.isEmpty()) {
            tasks = Arrays.asList(
                restTemplate.getForObject("http://localhost:9090/api/tasks?user=" + user, TaskDTO[].class)
            );
        } else {
            tasks = Arrays.asList(
                restTemplate.getForObject("http://localhost:9090/api/tasks", TaskDTO[].class)
            );
        }

        List<String> users = Arrays.asList(
            restTemplate.getForObject("http://localhost:9090/api/users", String[].class)
        );

        model.addAttribute("processes", processes);
        model.addAttribute("tasks", tasks);
        model.addAttribute("users", users);

        return "casedashboard"; // Thymeleaf template in UI app
    }
}
