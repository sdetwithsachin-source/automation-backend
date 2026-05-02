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

    // 🔥 NEW: store video path before closing
    private static String videoPath = "Video not available";

    public static Page init() {

        try {
            System.out.println("===== BROWSER INIT STARTED =====");

            System.setProperty("java.io.tmpdir", "/tmp");

            playwright = Playwright.create();

            browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(true)
                            .setArgs(Arrays.asList(
                                    "--no-sandbox",
                                    "--disable-dev-shm-usage",
                                    "--use-gl=swiftshader"
                            ))
            );

            System.out.println("Browser launched successfully");

            context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setViewportSize(1280, 720)
                            .setRecordVideoDir(Paths.get("videos/"))
                            .setRecordVideoSize(1280, 720)
            );

            page = context.newPage();

            System.out.println("===== BROWSER INIT COMPLETED =====");

            return page;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Browser initialization failed: " + e.getMessage());
        }
    }

    public static void openUrl(String url) {
        try {
            System.out.println("Navigating to: " + url);

            page.navigate(url);

            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.waitForLoadState(LoadState.NETWORKIDLE);

            page.waitForTimeout(3000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ RETURN STORED PATH
    public static String getVideoPath() {
        return videoPath;
    }

    public static void quit() {
        try {
            System.out.println("Closing browser resources...");

            Thread.sleep(3000);

            // 🔥 STEP 4 FIX: capture video path BEFORE closing context
            if (page != null && page.video() != null) {
                videoPath = page.video().path().toString();
                System.out.println("Captured Video Path: " + videoPath);
            }

            if (page != null) page.close();
            if (context != null) context.close();
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();

            System.out.println("===== BROWSER CLOSED SUCCESSFULLY =====");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}