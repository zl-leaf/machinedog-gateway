FROM maven

VOLUME ["/opt/htdoc/gateway"]

WORKDIR /opt/htdoc/gateway

EXPOSE 8080

CMD mvn install && java -jar ./target/gateway.jar