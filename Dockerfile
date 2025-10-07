FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/college-bot-1.0-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]