FROM gradle:7-jdk11 AS build
WORKDIR /home/gradle/src

# Only copy dependency-related files
COPY build.gradle settings.gradle /home/gradle/src/

# Only download dependencies
# Eat the expected build failure since no source code has been copied yet
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

COPY ./src /home/gradle/src/src
RUN gradle shadowJar --stacktrace --no-daemon


FROM openjdk:11-jre-slim AS app
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/bot.jar
WORKDIR /app
CMD java -XX:+UseContainerSupport -jar /app/bot.jar