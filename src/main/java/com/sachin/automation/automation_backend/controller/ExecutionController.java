package com.sachin.automation.automation_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ScreenshotType;
import com.sachin.automation.automation_backend.engine.ActionEngine;
import com.sachin.automation.automation_backend.engine.BrowserManager;
import com.sachin.automation.automation_backend.reporting.ExecutionReport;
import com.sachin.automation.automation_backend.reporting.StepResult;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class ExecutionController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @PostMapping("/run")
    public ResponseEntity<?> runTest(@RequestBody Map<String, Object> request) {

        String videoPath = "Video not available";

        // ✅ REPORT OBJECT
        ExecutionReport report = new ExecutionReport();
        report.setTestName("Automation Test");

        long totalStart = System.currentTimeMillis();

        try {

            System.out.println("===== TEST EXECUTION STARTED =====");

            List<Map<String, Object>> steps =
                    (List<Map<String, Object>>) request.get("steps");

            if (steps == null || steps.isEmpty()) {
                return ResponseEntity.badRequest().body("No steps provided");
            }

            // ✅ INIT BROWSER
            Page page = BrowserManager.init();

            ActionEngine engine = new ActionEngine(page);

            int stepNo = 1;

            // ✅ EXECUTE EACH STEP
            for (Map<String, Object> step : steps) {

                StepResult stepResult = new StepResult();

                stepResult.setStepNo(stepNo);

                String action =
                        step.get("action") != null
                                ? step.get("action").toString()
                                : "UNKNOWN";

                stepResult.setAction(action);

                long stepStart = System.currentTimeMillis();

                try {

                    System.out.println("Executing step: " + step);

                    engine.executeStep(step);

                    long stepEnd = System.currentTimeMillis();

                    stepResult.setStatus("PASSED");

                    stepResult.setExecutionTime(
                            (stepEnd - stepStart) / 1000.0
                    );

                    stepResult.setMessage(
                            "Step executed successfully"
                    );

                } catch (Exception stepException) {

                    long stepEnd = System.currentTimeMillis();

                    stepResult.setStatus("FAILED");

                    stepResult.setExecutionTime(
                            (stepEnd - stepStart) / 1000.0
                    );

                    stepResult.setMessage(
                            stepException.getMessage()
                    );

                    // ✅ SCREENSHOT ON FAILURE
                    try {

                        File screenshotDir =
                                new File("screenshots");

                        if (!screenshotDir.exists()) {
                            screenshotDir.mkdirs();
                        }

                        String screenshotPath =
                                "screenshots/step-" +
                                        stepNo +
                                        ".png";

                        page.screenshot(
                                new Page.ScreenshotOptions()
                                        .setPath(
                                                Paths.get(
                                                        screenshotPath
                                                )
                                        )
                                        .setType(
                                                ScreenshotType.PNG
                                        )
                        );

                        stepResult.setScreenshot(
                                screenshotPath
                        );

                    } catch (Exception screenshotError) {
                        screenshotError.printStackTrace();
                    }

                    // ❌ ADD FAILED STEP TO REPORT
                    report.getSteps().add(stepResult);

                    // 🔥 SOFT ASSERT SUPPORT
                    String actionType = action.toUpperCase();

                    if (!actionType.contains("VERIFY")) {
                        throw stepException;
                    }

                }

                // ✅ ADD PASSED STEP
                report.getSteps().add(stepResult);

                stepNo++;
            }

            report.setStatus("PASSED");

            System.out.println(
                    "===== TEST EXECUTION COMPLETED ====="
            );

        } catch (Exception e) {

            report.setStatus("FAILED");

            System.err.println(
                    "===== ERROR DURING TEST EXECUTION ====="
            );

            e.printStackTrace();

        } finally {

            // ✅ CLOSE BROWSER
            BrowserManager.quit();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // ✅ VIDEO PATH
            videoPath = BrowserManager.getVideoPath();

            long totalEnd = System.currentTimeMillis();

            report.setTotalExecutionTime(
                    (totalEnd - totalStart) / 1000.0
            );

            // ✅ SAVE REPORT JSON
            try {

                File reportDir = new File("reports");

                if (!reportDir.exists()) {
                    reportDir.mkdirs();
                }

                ObjectMapper mapper =
                        new ObjectMapper();

                mapper.writerWithDefaultPrettyPrinter()
                      .writeValue(
                              new File(
                                      "reports/report.json"
                              ),
                              report
                      );

                System.out.println(
                        "Report generated successfully"
                );

            } catch (Exception reportError) {
                reportError.printStackTrace();
            }
        }

        // ✅ RESPONSE JSON
        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "message",
                "Test Execution Completed 🚀"
        );

        response.put(
                "videoPath",
                videoPath
        );

        response.put(
                "reportPath",
                "reports/report.json"
        );

        return ResponseEntity.ok(response);
    }

    // 🎥 VIDEO API
    @GetMapping("/video")
    public ResponseEntity<Resource> getVideo(
            @RequestParam String path
    ) {

        try {

            File file = new File(path);

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource =
                    new UrlResource(file.toURI());

            return ResponseEntity.ok()
                                 .header(
                                         "Content-Disposition",
                                         "attachment; filename="
                                                 + file.getName()
                                 )
                                 .header(
                                         "Content-Type",
                                         "video/webm"
                                 )
                                 .body(resource);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    // 📊 REPORT API
    @GetMapping("/report")
    public ResponseEntity<?> getReport() {

        try {

            ObjectMapper mapper =
                    new ObjectMapper();

            File reportFile =
                    new File("reports/report.json");

            if (!reportFile.exists()) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            Object report =
                    mapper.readValue(
                            reportFile,
                            Object.class
                    );

            return ResponseEntity.ok(report);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body("Failed to load report");
        }
    }
}