package com.sachin.automation.automation_backend.engine;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.MouseButton;

import java.nio.file.Paths;
import java.util.Map;

public class ActionEngine {

    private final Page page;

    public ActionEngine(Page page) {
        this.page = page;
    }

    // ===================== CORE EXECUTION =====================

    public void executeStep(Map<String, Object> step) {

        String action = (String) step.get("action");
        String locatorType = (String) step.get("locatorType");
        String locatorValue = (String) step.get("locatorValue");
        String data = (String) step.get("data");
        String wait = (String) step.get("wait");

        String selector = buildSelector(locatorType, locatorValue);

        System.out.println("Executing Step: " + action);

        switch (action) {

            case "OPEN_URL":
                openUrl(data);
                break;

            case "CLICK":
                click(selector);
                break;

            case "TYPE":
                type(selector, data);
                break;

            case "WAIT":
                if (wait != null && !wait.isEmpty()) {
                    waitForTimeout(Integer.parseInt(wait));
                }
                break;

            case "ASSERT_TEXT":
                String actual = getText(selector);
                System.out.println("ASSERT TEXT: " + actual);
                break;

            case "SCREENSHOT":
                takeScreenshot("screenshot.png");
                break;

            default:
                System.out.println("Unknown action: " + action);
        }
    }

    // ===================== SELECTOR BUILDER =====================

    private String buildSelector(String type, String value) {

        if (type == null || type.equals("N/A") || value == null) {
            return "";
        }

        switch (type) {
            case "css":
                return value;

            case "xpath":
                return "xpath=" + value;

            case "id":
                return "#" + value;

            case "text":
                return "text=" + value;

            default:
                return value;
        }
    }

    // ===================== INTERNAL HELPERS =====================

    private Locator locator(String selector) {
        return page.locator(selector);
    }

    // ===================== NAVIGATION =====================

    public void openUrl(String url) {
        page.navigate(url);
    }

    public void refresh() {
        page.reload();
    }

    public void goBack() {
        page.goBack();
    }

    public void goForward() {
        page.goForward();
    }

    public String getTitle() {
        return page.title();
    }

    public String getCurrentUrl() {
        return page.url();
    }

    // ===================== MOUSE =====================

    public void click(String selector) {
        locator(selector).click();
    }

    public void doubleClick(String selector) {
        locator(selector).dblclick();
    }

    public void rightClick(String selector) {
        locator(selector).click(
                new Locator.ClickOptions().setButton(MouseButton.RIGHT)
        );
    }

    public void hover(String selector) {
        locator(selector).hover();
    }

    public void dragAndDrop(String sourceSelector, String targetSelector) {
        page.dragAndDrop(sourceSelector, targetSelector);
    }

    // ===================== INPUT =====================

    public void type(String selector, String value) {
        locator(selector).fill(value);
    }

    public void clear(String selector) {
        locator(selector).clear();
    }

    public void pressKey(String selector, String key) {
        locator(selector).press(key);
    }

    public void focus(String selector) {
        locator(selector).focus();
    }

    public void check(String selector) {
        locator(selector).check();
    }

    public void uncheck(String selector) {
        locator(selector).uncheck();
    }

    public void selectByValue(String selector, String value) {
        locator(selector).selectOption(value);
    }

    public void uploadFile(String selector, String filePath) {
        locator(selector).setInputFiles(Paths.get(filePath));
    }

    // ===================== VALIDATION =====================

    public String getText(String selector) {
        return locator(selector).innerText();
    }

    public String getValue(String selector) {
        return locator(selector).inputValue();
    }

    public boolean isVisible(String selector) {
        return locator(selector).isVisible();
    }

    public boolean isEnabled(String selector) {
        return locator(selector).isEnabled();
    }

    public boolean isChecked(String selector) {
        return locator(selector).isChecked();
    }

    // ===================== WAIT =====================

    public void waitForElement(String selector) {
        locator(selector).waitFor();
    }

    public void waitForTimeout(int timeoutMillis) {
        page.waitForTimeout(timeoutMillis);
    }

    // ===================== SCROLL =====================

    public void scrollToElement(String selector) {
        locator(selector).scrollIntoViewIfNeeded();
    }

    public void scrollToTop() {
        page.evaluate("window.scrollTo(0, 0)");
    }

    public void scrollToBottom() {
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
    }

    // ===================== SCREEN =====================

    public void takeScreenshot(String filePath) {
        page.screenshot(
                new Page.ScreenshotOptions().setPath(Paths.get(filePath))
        );
    }
}