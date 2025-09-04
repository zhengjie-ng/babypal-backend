# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17-alpine as builder
ENV PORT=8080
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Declare environment variables without values
ENV DATABASE_URL=""
ENV JWT_SECRET=""
ENV FRONTEND_URL=""
ENV MAIL_USERNAME=""
ENV MAIL_PASSWORD=""

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
