# dicerealm Monorepo

This is the monorepo for the dicerealm project. It contains the following packages:

- [server](server/README.md) - The websocket server
- [android-client](android-client/README.md) - The android-client application
- [web](web/README.md) - The web client application written in React

## Contributing

Please refer to the [CONTRIBUTING.md](CONTRIBUTING.md) file for information on how to contribute to this project.

## Deploying with docker

To build and run the static frontend and the server with docker, you can use the following commands:

```bash
docker build -t dicerealm/server .
```

To start the server, you can run the following command:

```bash
docker run -p 8080:8080 -e OPENAI_API_KEY=<your-api-key> dicerealm/server
```
