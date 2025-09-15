FROM amazoncorretto:21 AS builder
WORKDIR /workspace

COPY build.gradle settings.gradle main.gradle gradlew ./
COPY gradle ./gradle
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

COPY . .
RUN ./gradlew clean build -x test --no-daemon

FROM amazoncorretto:21-alpine
WORKDIR /app

COPY --from=builder /workspace/applications/app-service/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
