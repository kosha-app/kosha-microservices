FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY app.jar app.jar
COPY dd-java-agent.jar dd-java-agent.jar

EXPOSE 8080

ENTRYPOINT ["java", "-javaagent:/app/dd-java-agent.jar", \
            "-Ddd.env=dev", \
            "-Ddd.service=kosha-microservices", \
            "-Ddd.version=1.0.0", \
            "-jar", "/app/app.jar"]
