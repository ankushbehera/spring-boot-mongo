#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY student-service-app /home/app/student-service-app
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /home/app/student-service-app/target/*.jar /usr/local/lib/student-service-app-app.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/student-service-app-app.jar"]