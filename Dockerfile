# ========================
# BUILD
# ========================
FROM gradle:9.3.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build --no-daemon -x test

# ========================
# RUNTIME
# ========================
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/usuario.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/usuario.jar"]