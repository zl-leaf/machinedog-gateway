FROM maven

RUN mkdir -p /opt/htdoc/app
ADD ./target/gateway.jar /opt/htdoc/app/
WORKDIR /opt/htdoc/app

EXPOSE 8080

CMD java -jar ./gateway.jar