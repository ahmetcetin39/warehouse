FROM maven:3.6.3-jdk-11-slim

# Copy all files into container
COPY . /

# Build maven project
RUN mvn clean install

# Refer to jar that maven built
ARG JAR_FILE=target/warehouse-0.0.1.jar

# cd /opt/app
WORKDIR /opt/app

# cp target/warehouse-0.0.1.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]