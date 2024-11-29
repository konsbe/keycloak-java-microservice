# Using a Maven image with OpenJDK 17
FROM maven:3.8.4-openjdk-17 as build

# Set the working directory in the Docker image
WORKDIR /app

# Copy the Java source code, pom.xml, and other build files into the image
COPY . /app

# Run Maven to compile the code and package it in a JAR file
RUN mvn clean install -DskipTests

# Use openjdk:17 base image for the final stage
FROM openjdk:17

# Expose the openjdk port
EXPOSE 8081

# Copy your application JAR file to the openjdk webapps directory
ADD target/keycloak-microservice-0.0.1-SNAPSHOT.jar app.jar

# Set the default entry point to run your application within openjdk
ENTRYPOINT ["java","-jar","/app.jar"]
