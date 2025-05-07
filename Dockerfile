FROM maven:3.9.9-eclipse-temurin-24 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:24-jdk
COPY --from=build /target/RecipeManager-0.0.1-SNAPSHOT.jar RecipeManager.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","RecipeManager.jar"]