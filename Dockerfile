# Use Java 17 base image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Install required Linux dependencies for Playwright
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    gnupg \
    ca-certificates \
    libnss3 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libcups2 \
    libxkbcommon0 \
    libxcomposite1 \
    libxdamage1 \
    libxrandr2 \
    libgbm1 \
    libasound2 \
    libpangocairo-1.0-0 \
    libpango-1.0-0 \
    libgtk-3-0 \
    libx11-xcb1 \
    libxcb1 \
    libxext6 \
    libxfixes3 \
    libx11-6 \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Install Node.js (required for Playwright CLI)
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get install -y nodejs

# Verify node install (optional debug)
RUN node -v && npm -v

# Install Playwright browsers + dependencies
RUN npx playwright install --with-deps

# Copy project files
COPY . .

# Give permission to Maven wrapper
RUN chmod +x mvnw

# Build Spring Boot app
RUN ./mvnw clean package -DskipTests

# Expose port (Render expects this)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/automation-backend-0.0.1-SNAPSHOT.jar"]