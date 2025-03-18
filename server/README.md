# Server

This folder contains the server-side code for the project. The server is built using [Spring Boot](https://spring.io/) with its Websocket support

## Environment Variables

## The server uses the following environment variables:

| Variable         | Description                    | Default Value |
| ---------------- | ------------------------------ | ------------- |
| `OPENAI_API_KEY` | The API key for the OpenAI API | `""`          |

Spring boot automatically detects environment variables from your system.

```bash
export OPENAI_API_KEY=<your-api-key>
```

## Running the server

Before running the server, ensure that `lib` is built. You may refer to [lib/README.md](../lib/README.md) for instructions on how to build the library.

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

## Endpoints

### Websocket

You may connect to the server via the websocket protocol, `ws://` or `wss://`. The server supports the following endpoints:

- `/connect`: Connect to the default room "0000"
- `/room/{roomId}`: Connect to a specific room. A new room will be created if it does not exist.

Once connected to a room, you can send commands in the form of JSON objects to interact with the room. The server recognizes the following commands:

- `MESSAGE`
  - Send a text message to the room. The message will be broadcasted to all connected players.
  - Schema: `{"type": "MESSAGE", "message": "<string>"}`
  - Example: `{"type": "MESSAGE", "message": "Hello, world!"}`

The server also broadcasts the following events to all connected clients:

- `OUTGOING_MESSAGE`

  - Broadcasted when a message is sent to the room.
  - Schema: `{"type": "OUTGOING_MESSAGE", "message": "<string>", "senderName": "<string>", "messageId": "<UUID string>"}`
  - Example: `{"type": "OUTGOING_MESSAGE", "message": "Hello, world!", "senderName": "Alice", "messageId": "123e4567-e89b-12d3-a456-426614174000"}`

- `MESSAGE_HISTORY`
  - Broadcasted when a client connects to a room. Contains the message history of the room.
  - Schema: `{"type": "MESSAGE_HISTORY", "messages": [{"message": "<string>", "senderName": "<string>", "messageId": "<UUID string>"}]}`
  - Example: `{"type": "MESSAGE_HISTORY", "messages": [{"message": "Hello, world!", "senderName": "Alice", "messageId": "123e4567-e89b-12d3-a456-426614174000"}]}`
