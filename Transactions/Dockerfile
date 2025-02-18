FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar Transactions.jar


EXPOSE 8082:8082
ENTRYPOINT ["java","-jar","Transactions.jar"]