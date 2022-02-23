FROM openjdk:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/xtdb-grpc-0.0.1-SNAPSHOT-standalone.jar /xtdb-grpc/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/xtdb-grpc/app.jar"]
