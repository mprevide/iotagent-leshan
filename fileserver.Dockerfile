FROM zenika/alpine-maven:3-jdk8


ADD ./californium /usr/src/app
RUN mkdir -p /usr/src/app/demo-apps/cf-simplefile-server/data
WORKDIR /usr/src/app/demo-apps/cf-simplefile-server/

RUN ["mvn", "install"]

ADD Californium.properties /usr/src/app/demo-apps/cf-simplefile-server/

CMD ["java", "-jar", "target/cf-simplefile-server-2.0.0-M8.jar"]
