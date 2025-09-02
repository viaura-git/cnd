FROM eclipse-temurin:21.0.8_9-jre-ubi9-minimal
LABEL authors="fusionsoft"

ENV TZ=Asia/Seoul

WORKDIR /app

ENV JAR_FILE=cnd-com-gateway-0.0.1-SNAPSHOT.jar
COPY target/${JAR_FILE} /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]