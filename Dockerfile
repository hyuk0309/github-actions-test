FROM openjdk:11-alpine
EXPOSE 8080
ARG JAR_FILE=/build/libs/springboot-url-shortener-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]