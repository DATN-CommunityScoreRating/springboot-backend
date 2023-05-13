FROM openjdk:11

COPY /target/springboot-backend.jar springboot-backend.jar

ENTRYPOINT ["java", "-jar", "springboot-backend.jar"]