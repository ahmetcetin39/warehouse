# BUILD STAGE
FROM maven:3.6.3-jdk-11-slim as MAVEN_BUILD

MAINTAINER Ahmet Cetin

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/

RUN mvn clean install

# RUN STAGE
FROM openjdk:11-jre-slim

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/warehouse-0.0.1.jar /app/

EXPOSE 8080

ENTRYPOINT ["java","-jar","warehouse-0.0.1.jar"]
