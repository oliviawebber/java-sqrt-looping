FROM openjdk:8-jdk
COPY ./src/main/java /app
EXPOSE 9999
RUN javac /app/*.java
WORKDIR /app
CMD java Client