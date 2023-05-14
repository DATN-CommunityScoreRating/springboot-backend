FROM openjdk:11

COPY /target/springboot-backend.jar springboot-backend.jar

ENTRYPOINT ["java","-Dspring.profiles.active=dev", "-jar", "springboot-backend.jar"]