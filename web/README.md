# Web client

React implementation of the dicerealm web client.

## Environment Variables

The web client uses the following environment variables:

| Variable          | Description                     | Default Value         |
| ----------------- | ------------------------------- | --------------------- |
| `VITE_SERVER_URL` | The URL of the dicerealm server | `ws://localhost:8080` |

You may copy the `.env.example` file to a new file named `.env.local` and set the `VITE_SERVER_URL` variable to the URL of the websocket server.

````bash

## Running the client

To run the client, you need to have Node.js installed on your machine. You can run the client using the following commands:

```bash
npm install
npm run dev
````

## Building the client

To build the client, you can run the following command:

```bash
npm run build
```

This will build the client and output the files to the `server` package's `resources/static` directory.
