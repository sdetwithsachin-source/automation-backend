# ✅ Playwright official Java image
FROM mcr.microsoft.com/playwright/java:v1.43.0-jammy

WORKDIR /app

# ✅ Use preinstalled browsers
ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright
ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1

# Copy project files
COPY . .

# Build Spring Boot app
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# ✅ IMPORTANT: use dynamic port from Render
CMD ["sh", "-c", "java -Dserver.port=$PORT -jar target/automation-backend-0.0.1-SNAPSHOT.jar"]