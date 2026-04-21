package com.sachin.automation.automation_backend.controller;

import com.microsoft.playwright.Page;
import com.sachin.automation.automation_backend.engine.ActionEngine;
import com.sachin.automation.automation_backend.engine.BrowserManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin
public class ExecutionController {

    @PostMapping("/run")
    public String runTest(@RequestBody Map<String, Object> request) {

        List<Map<String, Object>> steps =
                (List<Map<String, Object>>) request.get("steps");

        Page page = BrowserManager.init();
        ActionEngine engine = new ActionEngine(page);

        for (Map<String, Object> step : steps) {
            engine.executeStep(step);
        }

        return "Test Executed Successfully 🚀";
    }
}
