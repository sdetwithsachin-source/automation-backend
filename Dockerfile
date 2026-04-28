# Use full Ubuntu-based Java image (important)
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Install basic tools + Node.js
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    gnupg \
    ca-certificates \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Install Node.js (required for Playwright)
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get install -y nodejs

# Install Playwright browsers + dependencies
RUN npx playwright install --with-deps

# Copy project
COPY . .

# Build project
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/automation-backend-0.0.1-SNAPSHOT.jar"]