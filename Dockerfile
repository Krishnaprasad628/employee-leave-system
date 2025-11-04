FROM azul/zulu-openjdk-alpine:21-jre-headless

RUN apk add --no-cache shadow openssl

RUN sed -i 's/^UID_MIN.*/UID_MIN 1000/' /etc/login.defs && \
    sed -i 's/^UID_MAX.*/UID_MAX 2000000000/' /etc/login.defs && \
    sed -i 's/^GID_MIN.*/GID_MIN 1000/' /etc/login.defs && \
    sed -i 's/^GID_MAX.*/GID_MAX 2000000000/' /etc/login.defs

WORKDIR /app

COPY ./build/libs/Employee-Leave-Management-System-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "Employee-Leave-Management-System-0.0.1-SNAPSHOT.jar"]

EXPOSE 8080