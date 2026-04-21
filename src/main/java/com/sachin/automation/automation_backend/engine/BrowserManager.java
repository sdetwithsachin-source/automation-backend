package com.sachin.automation.automation_backend.engine;


import com.microsoft.playwright.*;

public class BrowserManager {

    public static Page init() {

        Playwright playwright = Playwright.create();

        Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );

        BrowserContext context = browser.newContext();
        return context.newPage();
    }
}
