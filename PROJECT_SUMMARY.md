# Automation Backend Project Summary

## 1. What This Project Does

This project is a Spring Boot backend that executes browser automation steps using Playwright.

Its current purpose is simple:

- Accept a list of automation steps from an API request
- Open a Chromium browser through Playwright
- Execute each step in sequence on a browser page
- Return a success message after the flow completes

In short, this backend acts as an execution engine for UI automation instructions sent from a client application.

## 2. Tech Stack

- Java 17
- Spring Boot 4.0.5
- Maven
- Microsoft Playwright Java 1.43.0
- `org.json` library included in dependencies
- JUnit 5 via `spring-boot-starter-test`

## 3. High-Level Architecture

The codebase is small and currently organized into three main layers:

### Application Entry

- `AutomationBackendApplication`
- Starts the Spring Boot application

### API Layer

- `ExecutionController`
- Exposes the REST endpoint that receives test steps from the client

### Automation Engine Layer

- `BrowserManager`
- Creates the Playwright instance, launches Chromium, and returns a new page

- `ActionEngine`
- Interprets each step and maps it to a Playwright action such as open URL, click, type, wait, get text, or take screenshot

## 4. Current Project Structure

```text
automation-backend/
|-- src/
|   |-- main/
|   |   |-- java/com/sachin/automation/automation_backend/
|   |   |   |-- AutomationBackendApplication.java
|   |   |   |-- controller/
|   |   |   |   |-- ExecutionController.java
|   |   |   |-- engine/
|   |   |       |-- ActionEngine.java
|   |   |       |-- BrowserManager.java
|   |   |-- resources/
|   |       |-- application.properties
|   |-- test/
|       |-- java/com/sachin/automation/automation_backend/
|           |-- AutomationBackendApplicationTests.java
|-- pom.xml
|-- mvnw
|-- mvnw.cmd
|-- HELP.md
```

## 5. Request Flow

The main flow today is:

1. A client sends a `POST` request to `/api/test/run`
2. The request body contains a `steps` array
3. `ExecutionController` reads the steps
4. `BrowserManager.init()` launches a non-headless Chromium browser
5. `ActionEngine` is created with the Playwright `Page`
6. Each step is executed one by one
7. The API returns the string: `Test Executed Successfully 🚀`

There is no job queue, persistence layer, authentication, or async execution at this stage. Everything happens in a single request lifecycle.

## 6. Main API Endpoint

### `POST /api/test/run`

This is the only functional endpoint in the project right now.

Expected request body shape:

```json
{
  "steps": [
    {
      "action": "OPEN_URL",
      "locatorType": "N/A",
      "locatorValue": null,
      "data": "https://example.com",
      "wait": null
    },
    {
      "action": "TYPE",
      "locatorType": "css",
      "locatorValue": "#username",
      "data": "admin",
      "wait": null
    },
    {
      "action": "CLICK",
      "locatorType": "css",
      "locatorValue": "#loginBtn",
      "data": null,
      "wait": null
    },
    {
      "action": "WAIT",
      "locatorType": "N/A",
      "locatorValue": null,
      "data": null,
      "wait": "2000"
    }
  ]
}
```

Notes:

- The request is currently handled as `Map<String, Object>` instead of typed DTO classes
- There is no validation for missing or malformed fields
- Any runtime failure during Playwright execution will surface directly from the request

## 7. Supported Actions in `ActionEngine`

The `executeStep()` method currently supports these actions from API input:

- `OPEN_URL`
- `CLICK`
- `TYPE`
- `WAIT`
- `ASSERT_TEXT`
- `SCREENSHOT`

### How each action behaves

- `OPEN_URL`: navigates the browser to the URL provided in `data`
- `CLICK`: clicks the resolved selector
- `TYPE`: fills the resolved selector with the value from `data`
- `WAIT`: waits for the number of milliseconds provided in `wait`
- `ASSERT_TEXT`: reads text from the selector and prints it to console
- `SCREENSHOT`: saves a screenshot as `screenshot.png`

Important detail:

- `ASSERT_TEXT` is not a real assertion yet. It only prints the actual text and does not compare against an expected value or fail the request.

## 8. Selector Resolution Logic

`ActionEngine` converts request locator definitions into Playwright selectors.

Supported locator types:

- `css` -> uses value as-is
- `xpath` -> converts to `xpath=<value>`
- `id` -> converts to `#<value>`
- `text` -> converts to `text=<value>`

If `locatorType` is `null`, `N/A`, or `locatorValue` is `null`, an empty selector string is returned.

## 9. Additional Capabilities Already Present in Code

Even though the API only uses a few actions today, `ActionEngine` already contains helper methods for more browser interactions:

- refresh, back, forward
- get title, get current URL
- double click, right click, hover, drag and drop
- clear, press key, focus
- check, uncheck
- select dropdown option
- upload file
- get value, check visible, enabled, checked
- wait for element
- scroll to element, top, bottom
- take screenshot

These methods are available in code but are not yet wired into the API `switch` statement for incoming step execution.

## 10. Browser Lifecycle Behavior

`BrowserManager` currently does the following:

- Creates a new Playwright instance for each request
- Launches Chromium in non-headless mode with `setHeadless(false)`
- Creates a new browser context
- Returns a fresh page

Current limitation:

- The code does not close the `Page`, `BrowserContext`, `Browser`, or `Playwright` objects after execution
- Over time, repeated requests can leave browser resources open and cause memory or process leaks

## 11. Configuration

Current application configuration is minimal.

`src/main/resources/application.properties` contains:

```properties
spring.application.name=automation-backend
```

There are currently no custom environment-specific properties for:

- server port
- browser mode
- timeout configuration
- logging behavior
- CORS restrictions
- file storage paths

## 12. Build and Run

### Run locally

Using Maven Wrapper on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Using Maven Wrapper on macOS/Linux:

```bash
./mvnw spring-boot:run
```

### Run tests

```powershell
.\mvnw.cmd test
```

## 13. Testing Status

The project currently has one basic Spring Boot test:

- `AutomationBackendApplicationTests`
- It only checks whether the application context loads successfully

What is missing:

- controller tests
- API contract tests
- Playwright execution tests
- negative/error-path tests
- selector parsing tests
- action coverage tests

## 14. Current Strengths

- Simple and easy to understand codebase
- Clear separation between controller and execution engine
- Good starting point for browser automation orchestration
- Playwright integration is already working at a basic level
- Additional browser action helper methods are already prepared for expansion

## 15. Current Gaps and Risks

These are the main things a new developer should know before extending the project:

- No typed request/response models; raw `Map<String, Object>` is used
- No input validation
- No exception handling strategy
- No resource cleanup for Playwright/browser objects
- No logging framework usage beyond `System.out.println`
- `ASSERT_TEXT` does not actually assert
- Screenshot file name is hardcoded to `screenshot.png`
- Only one endpoint exists
- No authentication or authorization
- CORS is fully open because of `@CrossOrigin`
- No persistence or execution history
- No async/background execution
- No retry, timeout, or failure-reporting strategy

## 16. Recommended Next Improvements

If someone joins this project and wants to improve it safely, these are the most valuable next steps:

1. Replace raw request maps with DTO classes
2. Add request validation using Spring validation annotations
3. Introduce proper response objects instead of plain strings
4. Close Playwright resources reliably in `finally` blocks or with a lifecycle wrapper
5. Add structured logging
6. Make assertions real and return failures clearly to the client
7. Add support for more actions through a cleaner action-dispatch design
8. Add tests for controller behavior and action execution
9. Externalize configuration such as headless mode, timeouts, and screenshot location
10. Add execution result reporting with step-level success/failure details

## 17. Quick Understanding for a New Joiner

If you only need the short version, this project is:

- a Spring Boot REST backend
- that receives browser automation steps
- runs them using Playwright in Chromium
- and currently executes them sequentially in a single request

The most important files to read first are:

- `src/main/java/com/sachin/automation/automation_backend/controller/ExecutionController.java`
- `src/main/java/com/sachin/automation/automation_backend/engine/ActionEngine.java`
- `src/main/java/com/sachin/automation/automation_backend/engine/BrowserManager.java`
- `pom.xml`

Those files explain almost the entire behavior of the current system.
