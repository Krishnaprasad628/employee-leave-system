FROM azul/zulu-openjdk-alpine:21-jre-headless

WORKDIR /app

COPY build/libs/Employee-Leave-Management-System-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]