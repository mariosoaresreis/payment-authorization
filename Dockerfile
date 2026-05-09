FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /build
COPY pom.xml .
RUN apk add --no-cache maven && mvn dependency:go-offline

COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add non-root user
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

COPY --from=builder /build/target/*.jar app.jar
COPY --from=builder /build/target/classes/logback-spring.xml .

RUN chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=UTC", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]

