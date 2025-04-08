FROM amazoncorretto:21-alpine

WORKDIR /app

COPY target/api-0.0.2-SNAPSHOT.jar /app/api-0.0.2-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "api-0.0.2-SNAPSHOT.jar"]