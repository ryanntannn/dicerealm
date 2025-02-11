# Server

This folder contains the server-side code for the project. The server is built using [Spring Boot](https://spring.io/) with its Websocket support

## Running the server

To run the server, you need to have Java 23 installed on your machine. You can run the server using the following command:

PC:

```
.\gradlew.bat bootRun
```

Mac/Linux:

```bash
./gradlew bootRun
```

## Testing

To test the server, you can connect to the server using a websocket client. The server runs on `ws://localhost:8080/connect`.
You can connect to this URL using a websocket client and send messages to the server, which will be broadcasted to all other connected clients.
