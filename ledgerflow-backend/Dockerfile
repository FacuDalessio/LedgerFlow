FROM eclipse-temurin:25-jre

WORKDIR /app

ARG JAR_FILE=target/ledgerflow-backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
