FROM openjdk:17
USER root
LABEL maintainer="13076894376@163.com"
COPY ./shared-files /my-app/shared-files
COPY ./target/*.jar /my-app/app.jar
WORKDIR /my-app
ENV JAVA_OPTS="-Xms256M -Xmx256M"
VOLUME /my-app/shared-files
EXPOSE 8080
CMD sh -c "java $JAVA_OPTS -jar app.jar"



