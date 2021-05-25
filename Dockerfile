FROM openjdk:8-jre
ADD build/Client.class Client.class
ADD build/MetricHandler.class MetricHandler.class
ADD build/SqrtLooper.class SqrtLooper.class
EXPOSE 9999
ENTRYPOINT ["java","Client"]