package com.vk;

import org.flowable.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessController {

    @Autowired
    RuntimeService runtimeService;

    @GetMapping("/start")
    public String startProcess() {
        runtimeService.startProcessInstanceByKey("helloProcess");
        return "Process Started";
    }
}
