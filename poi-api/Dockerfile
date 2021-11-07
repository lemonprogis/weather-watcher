FROM openjdk:11-alpine AS builder
WORKDIR /tmp/
ADD target /tmp/target
RUN java -Djarmode=layertools -jar /tmp/target/weather-watcher.jar extract

FROM gcr.io/distroless/java:11
EXPOSE 8080
WORKDIR application
COPY --from=builder /tmp/dependencies/ ./
COPY --from=builder /tmp/snapshot-dependencies/ ./
COPY --from=builder /tmp/spring-boot-loader/ ./
COPY --from=builder /tmp/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
