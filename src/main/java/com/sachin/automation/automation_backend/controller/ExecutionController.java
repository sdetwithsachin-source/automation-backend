package com.sachin.automation.automation_backend.controller;

import com.microsoft.playwright.Page;
import com.sachin.automation.automation_backend.engine.ActionEngine;
import com.sachin.automation.automation_backend.engine.BrowserManager;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*") // ✅ allow frontend access (can restrict later)
public class ExecutionController {

    // ✅ NEW: Health API (VERY IMPORTANT)
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    // 🚀 RUN TEST
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

            // 🔥 CLOSE browser FIRST (this saves video)
            System.out.println("Closing browser...");
            BrowserManager.quit();

            try {
                // ✅ Wait briefly to ensure video is written
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // ✅ Get video path AFTER closing
            videoPath = BrowserManager.getVideoPath();
            System.out.println("Video Path: " + videoPath);
        }

        // ✅ Return response JSON
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Test Executed Successfully 🚀");
        response.put("videoPath", videoPath);

        return ResponseEntity.ok(response);
    }

    // 🎥 DOWNLOAD VIDEO API
    @GetMapping("/video")
    public ResponseEntity<Resource> getVideo(@RequestParam String path) {
        try {
            File file = new File(path);

            if (!file.exists()) {
                System.err.println("Video file not found: " + path);
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(file.toURI());

            return ResponseEntity.ok()
                                 .header("Content-Disposition", "attachment; filename=" + file.getName())
                                 .header("Content-Type", "video/webm")
                                 .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}