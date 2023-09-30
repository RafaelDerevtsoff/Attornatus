FROM amazoncorretto:21-alpine-jdk

WORKDIR /app

COPY build/libs/case-0.0.1-SNAPSHOT.jar /app/attornatus.jar

ENV REDISHOST=0.0.0.0 REDISPORT=6379

CMD ["java", "-jar", "attornatus.jar"]