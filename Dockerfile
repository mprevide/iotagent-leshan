FROM zenika/alpine-maven:3-jdk8


RUN mkdir -p /usr/src/app/data
ADD . /usr/src/app
WORKDIR /usr/src/app/californium

RUN ["mvn", "install"]

WORKDIR /usr/src/app

RUN ["mvn", "install"]

CMD ["java", "-jar", "target/iotagent-leshan-1.0-SNAPSHOT-jar-with-dependencies.jar"]
