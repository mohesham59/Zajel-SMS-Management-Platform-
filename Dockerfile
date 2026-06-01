FROM maven:3.8.4-openjdk-11-slim AS build

WORKDIR /app

COPY TwilioSMSClient/pom.xml .
COPY TwilioSMSClient/zajel-engine zajel-engine
COPY TwilioSMSClient/zajel-webapp zajel-webapp

RUN mvn -f pom.xml clean package -DskipTests


FROM tomcat:9.0-jdk11-openjdk-slim

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/zajel-webapp/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
