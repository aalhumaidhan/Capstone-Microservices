FROM openjdk:25-jdk

LABEL version="1.0"

EXPOSE 8082:8082

WORKDIR /app

COPY target/Transactions-0.0.1-SNAPSHOT.jar /app/Transactions-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","Transactions-0.0.1-SNAPSHOT.jar"]