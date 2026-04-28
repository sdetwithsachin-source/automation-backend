package com.sachin.automation.automation_backend.controller;

import com.microsoft.playwright.Page;
import com.sachin.automation.automation_backend.engine.ActionEngine;
import com.sachin.automation.automation_backend.engine.BrowserManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class ExecutionController {

    @PostMapping("/run")
    public ResponseEntity<?> runTest(@RequestBody Map<String, Object> request) {

        String videoPath = "Video not available";

        try {
            System.out.println("===== TEST EXECUTION STARTED =====");
            System.out.println("Incoming Request: " + request);

            List<Map<String, Object>> steps =
                    (List<Map<String, Object>>) request.get("steps");

            if (steps == null || steps.isEmpty()) {
                return ResponseEntity.badRequest().body("No steps provided");
            }

            System.out.println("Total Steps: " + steps.size());

            // 🔥 Initialize browser
            System.out.println("Initializing browser...");
            Page page = BrowserManager.init();
            System.out.println("Browser initialized successfully");

            ActionEngine engine = new ActionEngine(page);

            // 🔥 Execute steps
            for (Map<String, Object> step : steps) {
                System.out.println("Executing step: " + step);
                engine.executeStep(step);
            }

            System.out.println("===== TEST EXECUTION COMPLETED =====");

        } catch (Exception e) {

            System.err.println("===== ERROR DURING TEST EXECUTION =====");
            e.printStackTrace();

            return ResponseEntity.status(500).body(
                    "Error occurred: " + e.getMessage()
            );

        } finally {
            // 🔥 MUST CLOSE FIRST (this saves video)
            System.out.println("Closing browser...");
            BrowserManager.quit();

            // ✅ NOW safe to get video path
            videoPath = BrowserManager.getVideoPath();
            System.out.println("Video Path: " + videoPath);
        }

        // ✅ Return after finally
        return ResponseEntity.ok("Test Done. Video: " + videoPath);
    }
}