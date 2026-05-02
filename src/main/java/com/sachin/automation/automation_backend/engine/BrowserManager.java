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

    public static void quit() {
        try {
            System.out.println("Closing browser resources...");

            Thread.sleep(3000);

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