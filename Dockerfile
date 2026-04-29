# ✅ Playwright official Java image (comes with browsers & deps)
FROM mcr.microsoft.com/playwright/java:v1.44.0-jammy

WORKDIR /app

# ✅ Force Playwright to use preinstalled browsers
ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright

# ✅ (Optional but recommended) Skip downloading other browsers
ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1

# Copy project files
COPY . .

# Build Spring Boot app
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

# Run application
CMD ["java", "-jar", "target/automation-backend-0.0.1-SNAPSHOT.jar"]