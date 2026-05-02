package com.sachin.automation.automation_backend.engine;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import java.nio.file.Paths;
import java.util.Arrays;

public class BrowserManager {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;

    public static Page init() {

        try {
            System.out.println("===== BROWSER INIT STARTED =====");

            System.setProperty("java.io.tmpdir", "/tmp");

            // 1. Create Playwright
            playwright = Playwright.create();

            // 2. Launch browser (FIXED CONFIG)
            browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false) // ✅ IMPORTANT: disable headless for proper video
                            .setArgs(Arrays.asList(
                                    "--no-sandbox",
                                    "--disable-dev-shm-usage"
                            ))
            );

            System.out.println("Browser launched successfully");

            // 3. Create context with VIDEO + VIEWPORT (CRITICAL)
            context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setViewportSize(1280, 720) // ✅ REQUIRED
                            .setRecordVideoDir(Paths.get("videos/"))
                            .setRecordVideoSize(1280, 720)
            );

            System.out.println("Context created");

            // 4. Create page
            page = context.newPage();

            System.out.println("Page created successfully");
            System.out.println("===== BROWSER INIT COMPLETED =====");

            return page;

        } catch (Exception e) {
            System.err.println("===== BROWSER INIT FAILED =====");
            e.printStackTrace();
            throw new RuntimeException("Browser initialization failed: " + e.getMessage());
        }
    }

    // ✅ Navigate with proper wait (VERY IMPORTANT)
    public static void openUrl(String url) {
        try {
            System.out.println("Navigating to: " + url);

            page.navigate(url);

            // Wait for full load (NO MORE BLANK VIDEO)
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Small buffer for rendering
            page.waitForTimeout(3000);

        } catch (Exception e) {
            System.err.println("Error during navigation:");
            e.printStackTrace();
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

            // 🔥 IMPORTANT: allow video to flush properly
            Thread.sleep(3000);

            if (page != null) {
                page.close();
                System.out.println("Page closed");
            }

            if (context != null) {
                context.close(); // ✅ this saves video
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