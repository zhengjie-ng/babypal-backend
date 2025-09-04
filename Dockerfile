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

# Declare environment variables without values
ENV DATABASE_URL=""
ENV JWT_SECRET=""
ENV FRONTEND_DEV_URL=""
ENV FRONTEND_PROD_URL=""
ENV MAIL_USERNAME=""
ENV MAIL_PASSWORD=""

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
