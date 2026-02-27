FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN apk add --no-cache docker-cli

COPY build/libs/*.jar app.jar

RUN mkdir -p /app/containizer-data/uploads /app/containizer-data/extracted

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
