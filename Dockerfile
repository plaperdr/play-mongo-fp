FROM java:latest
MAINTAINER Pierre Laperdrix

ADD prod.zip /opt/
RUN unzip /opt/prod.zip -d /opt/website && rm /opt/prod.zip
WORKDIR /opt/website/bin

CMD ["./website"]
