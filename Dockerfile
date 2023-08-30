FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY build/libs/sage-microservices-0.0.1-SNAPSHOT.jar sage-microservices-0.0.1-SNAPSHOT-plain.jar

EXPOSE 8080

CMD ["java","-jar","/sage-microservices-0.0.1-SNAPSHOT.jar"]