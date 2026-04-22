package com.sachin.automation.automation_backend.engine;

import com.microsoft.playwright.*;

import java.nio.file.Paths;

public class BrowserManager {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;

    public static Page init() {

        playwright = Playwright.create();

        // ✅ MUST be true for cloud (Render)
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
        );

        // ✅ Enable video recording
        context = browser.newContext(
                new Browser.NewContextOptions()
                        .setRecordVideoDir(Paths.get("videos/"))
                        .setRecordVideoSize(1280, 720)
        );

        page = context.newPage();

        return page;
    }

    // ✅ Get video path
    public static String getVideoPath() {
        try {
            if (page != null && page.video() != null) {
                return page.video().path().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Video not available";
    }

    // ✅ Quit method (VERY IMPORTANT for saving video)
    public static void quit() {
        try {
            if (page != null) page.close();
            if (context != null) context.close(); // ⚠️ This saves the video
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();

            System.out.println("Browser closed successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}