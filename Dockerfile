FROM openjdk:17

WORKDIR /app

COPY target/main_microservice.jar /app

ENTRYPOINT ["java","-jar","/app/main_microservice.jar"]