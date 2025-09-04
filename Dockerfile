FROM amazoncorretto:21 AS builder
WORKDIR /workspace

COPY . .
RUN chmod +x ./gradlew || true
RUN ./gradlew clean build -x test --no-daemon

FROM amazoncorretto:21-alpine
WORKDIR /app

COPY --from=builder /workspace/applications/app-service/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
