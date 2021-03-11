FROM openjdk:11
ARG JAR_FILE=build/libs/*all.jar
COPY ${JAR_FILE} keymanager-grpc.jar
ENTRYPOINT ["java","-jar","/keymanager-grpc.jar"]