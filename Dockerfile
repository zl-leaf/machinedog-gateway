FROM maven

RUN mkdir -p /opt/htdoc/app
ADD ./target/gateway.jar /opt/app/
WORKDIR /opt/app

EXPOSE 8080

CMD java -jar ./gateway.jar