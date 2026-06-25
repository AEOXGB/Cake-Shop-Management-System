FROM ubuntu:20.04

RUN apt-get update && apt-get install -y openjdk-8-jre-headless && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/CakeShop-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8090

ENV TZ=Asia/Shanghai

ENTRYPOINT ["java", "-jar", "app.jar"]
