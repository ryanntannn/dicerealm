./gradlew build
docker build -t dicerealm/server .
docker run -p 8080:8080 dicerealm/server