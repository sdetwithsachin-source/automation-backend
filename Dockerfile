# Use Playwright official image (BEST for stability)
FROM mcr.microsoft.com/playwright/java:v1.44.0-jammy

WORKDIR /app

# 🔥 VERY IMPORTANT: prevent runtime downloads
ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright

# Copy project
COPY . .

# Build project
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/automation-backend-0.0.1-SNAPSHOT.jar"]