FROM eclipse-temurin:21
COPY target/github-info-0.0.1-SNAPSHOT.jar github-info-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/github-info-0.0.1-SNAPSHOT.jar"]