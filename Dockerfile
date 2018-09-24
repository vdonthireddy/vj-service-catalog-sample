FROM openjdk:11-jdk
WORKDIR /
ADD target/myClient-1.0.jar myClient.jar
EXPOSE 9124
CMD ["java", "-jar", "myClient.jar"]