FROM openjdk:11-jdk
WORKDIR /
ADD target/pgClient-1.0.jar pgClient.jar
EXPOSE 9124
CMD ["java", "-jar", "pgClient.jar"]