# Usa un'immagine Maven per buildare l'applicazione
FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Compila l'applicazione e genera il file JAR
RUN mvn clean package

# Usa un'immagine più leggera per il runtime
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app


# Copia il file JAR dal container di build

# COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/target/*.jar /app/app.jar

# Esponi la porta su cui l'applicazione sarà disponibile
EXPOSE 8082

# Comando per avviare l'applicazione
# CMD ["java", "-jar", "app.jar"]
CMD ["java", "-jar", "/app/app.jar"]
