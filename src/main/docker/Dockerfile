FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD newsgis-0.0.1-SNAPSHOT.jar newsgis.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/newsgis.jar"]
ENV LANG en_US.UTF-8
RUN apk add --update ttf-dejavu fontconfig && rm -rf /var/cache/apk/*