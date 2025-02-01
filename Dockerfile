FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

RUN apt-get update && apt-get install -y maven

COPY . .

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

