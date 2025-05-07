FROM maven:3.9.9-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
COPY --from=build /target/*.jar recipemanager.jar
EXPOSE 8080
ENV MONGODB_URI=mongodb+srv://ramylearns:8J2nebHs4XFR1UI4@recipesharecluster.zajeynt.mongodb.net/RecipeManager
ENTRYPOINT ["java","-jar","recipemanager.jar"]