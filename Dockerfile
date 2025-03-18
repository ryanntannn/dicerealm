# Install nodejs
FROM node:20.15.0 AS build-web

COPY web /web
COPY server /server

# cd into web
WORKDIR /web

# install dependencies
RUN npm install

# build the app
RUN npm run build

FROM openjdk:23-jdk AS build-lib

COPY lib /lib

RUN microdnf install findutils

WORKDIR /lib
# build the app
RUN ./gradlew build

FROM openjdk:23-jdk AS build-server

COPY --from=build-web /server/src/main/resources/static /server/src/main/resources/static
COPY --from=build-lib /lib/lib/build /lib/lib/build
COPY server /server

RUN microdnf install findutils

# cd into server
WORKDIR /server

# build the app
RUN ./gradlew build

FROM openjdk:23-jdk AS final

COPY --from=build-server server/build/libs/\*.jar app.jar

# run the app
CMD ["java", "-jar", "/app.jar"]