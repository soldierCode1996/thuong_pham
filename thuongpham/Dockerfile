# ===== Build stage =====
FROM maven:3.9.9-eclipse-temurin-20 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# ===== Run stage =====
FROM eclipse-temurin:20-jre
WORKDIR /app

COPY --from=build /app/target/thuongpham-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
