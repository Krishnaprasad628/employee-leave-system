FROM azul/zulu-openjdk-alpine:21-jre-headless

WORKDIR /app

COPY build/libs/* app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]