# Base Alpine Linux based image with OpenJDK JRE only
# docker build -t hav/ais-simulator:latest .
# docker run -d --name ais-simulator -p 8040:8040 hav/ais-simulator:latest
FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.5_10_openj9-0.17.0-alpine-slim
USER root
RUN mkdir -p /opt/ais && adduser -D -h /opt/ais ais && apk update && apk add curl && apk add libaio && rm -rf /var/cache/apk/*

USER ais
WORKDIR /opt/ais
RUN curl -LO ftp://ftp.ais.dk/ais_data/aisdk_20190513.csv
COPY target/ais-simulator.jar ais-simulator.jar
CMD ["/opt/java/openjdk/bin/java", "-jar", "./ais-simulator.jar", "-D", "ais_nth_pos=3"]
