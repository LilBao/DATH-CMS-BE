# =========================================
# Stage 1: Build
# =========================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# =========================================
# Stage 2: Runtime
# =========================================
FROM eclipse-temurin:17-jre-jammy

LABEL maintainer="CGV Cinema Team"
LABEL version="1.0.0"

WORKDIR /app

# Create non-root user for security
RUN groupadd -r cgv && useradd -r -g cgv cgv

# Copy JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# Set ownership
RUN chown cgv:cgv app.jar

USER cgv

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.profiles.active=docker", \
  "-jar", "app.jar"]
