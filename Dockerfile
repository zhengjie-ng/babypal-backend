# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17-alpine as builder
WORKDIR /app

# Copy Maven wrapper files
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd ./
COPY pom.xml ./

# Make maven wrapper executable
RUN chmod +x mvnw

# Copy source code
COPY src ./src/

# Show contents for debugging
RUN echo "Contents of /app:" && ls -la

# Build the application using Maven wrapper
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Make port configurable via environment variable
ENV PORT=8080

# Expose the port
EXPOSE ${PORT}

# Command to run the application
CMD ["sh", "-c", "java -Dserver.port=$PORT -Dspring.profiles.active=prod $JAVA_OPTS -jar app.jar"]
