
# Use Maven to build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Use a lightweight OpenJDK image for running the app
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
COPY --from=build /app/target/bms-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
