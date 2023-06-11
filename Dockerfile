FROM openjdk:11

COPY /target/springboot-backend.jar springboot-backend.jar

ENTRYPOINT ["java","-Dspring.profiles.active=prod", "-jar", "springboot-backend.jar"]