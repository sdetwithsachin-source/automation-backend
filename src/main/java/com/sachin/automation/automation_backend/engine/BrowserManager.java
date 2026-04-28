package com.sachin.automation.automation_backend.engine;

import com.microsoft.playwright.*;

import java.nio.file.Paths;

public class BrowserManager {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;

    public static Page init() {

        try {
            System.out.println("===== BROWSER INIT STARTED =====");

            System.setProperty("java.io.tmpdir", "/tmp");
            // Step 1: Create Playwright
            System.out.println("Creating Playwright instance...");
            playwright = Playwright.create();

            // Step 2: Launch browser (headless is mandatory in cloud)
            System.out.println("Launching Chromium browser...");

            browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(true)
                            .setArgs(java.util.Arrays.asList(
                                    "--no-sandbox",
                                    "--disable-setuid-sandbox",
                                    "--disable-dev-shm-usage",
                                    "--disable-gpu",
                                    "--disable-software-rasterizer",
                                    "--disable-extensions",
                                    "--disable-background-networking",
                                    "--disable-background-timer-throttling",
                                    "--disable-client-side-phishing-detection",
                                    "--disable-default-apps",
                                    "--disable-hang-monitor",
                                    "--disable-popup-blocking",
                                    "--disable-sync",
                                    "--metrics-recording-only",
                                    "--no-first-run",
                                    "--no-zygote",
                                    "--single-process"
                            ))
            );

            System.out.println("Browser launched successfully");

            // Step 3: Create context with video recording
            System.out.println("Creating browser context...");

            context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setRecordVideoDir(Paths.get("videos/"))
                            .setRecordVideoSize(1280, 720)
            );

            System.out.println("Context created");

            // Step 4: Open new page
            page = context.newPage();

            System.out.println("Page created successfully");
            System.out.println("===== BROWSER INIT COMPLETED =====");

            return page;

        } catch (Exception e) {

            System.err.println("===== BROWSER INIT FAILED =====");
            e.printStackTrace(); // 🔥 THIS WILL SHOW REAL ROOT CAUSE

            throw new RuntimeException("Browser initialization failed: " + e.getMessage());
        }
    }

    // ✅ Get video path
    public static String getVideoPath() {
        try {
            if (page != null && page.video() != null) {
                return page.video().path().toString();
            }
        } catch (Exception e) {
            System.err.println("Error fetching video path:");
            e.printStackTrace();
        }
        return "Video not available";
    }

    // ✅ Quit method (VERY IMPORTANT for saving video)
    public static void quit() {
        try {
            System.out.println("Closing browser resources...");

            if (page != null) {
                page.close();
                System.out.println("Page closed");
            }

            if (context != null) {
                context.close(); // ⚠️ saves video
                System.out.println("Context closed (video saved)");
            }

            if (browser != null) {
                browser.close();
                System.out.println("Browser closed");
            }

            if (playwright != null) {
                playwright.close();
                System.out.println("Playwright closed");
            }

            System.out.println("===== BROWSER CLOSED SUCCESSFULLY =====");

        } catch (Exception e) {
            System.err.println("Error during browser quit:");
            e.printStackTrace();
        }
    }
}