FROM openjdk:25-jdk

LABEL version="1.0"

EXPOSE 8081:8081

WORKDIR /app

COPY target/Users-0.0.1-SNAPSHOT.jar /app/Users-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","Users-0.0.1-SNAPSHOT.jar"]