FROM java:latest
MAINTAINER Pierre Laperdrix

ADD website /opt/tmp/
WORKDIR /opt/
RUN ./tmp/activator stage && cp -r tpm/target/universal/stage/ /opt/website/ && ./tmp/activator clean && rm -r /opt/tmp/ /root/.sbt/ /root/.ivy2/

WORKDIR /opt/website/

CMD ["./bin/website"]
