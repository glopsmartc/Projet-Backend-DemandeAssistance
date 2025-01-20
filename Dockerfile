# Étape de build
FROM maven:3.8.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier les sources
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image and install psql
FROM eclipse-temurin:21-jdk AS runtime
WORKDIR /app

# Install psql (PostgreSQL client)
RUN apt-get update && apt-get install -y postgresql-client && rm -rf /var/lib/apt/lists/*

# Copy the built jar file from the build stage
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]