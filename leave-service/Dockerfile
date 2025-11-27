# Stage 1: Build the application
FROM gradle:8.5-jdk21 AS build
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle clean bootJar

# Stage 2: Run the application
FROM azul/zulu-openjdk-alpine:21-jre-headless
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
