# Distributed Architectures in Java

This project explores how distributed systems evolve — starting from a raw TCP socket and ending up with a full microservices architecture behind an API Gateway. Each part builds on the last, using the same kind of problem (look something up, reserve something) to show how the underlying communication style changes everything.

Built for the Software Architecture course (ARSW) at Escuela Colombiana de Ingeniería Julio Garavito.

---

## What's inside

The project is split into six parts, each one a different architectural style:

| Part | Style | What it does |
|------|-------|-------------|
| I | TCP Sockets | Client sends a text message to a server over a raw TCP connection |
| II | HTTP Server | Same idea, but now you can hit it from a browser or curl |
| III | Java RMI | Call a method on a remote object as if it were local |
| IV | gRPC | Modern RPC with typed contracts defined in `.proto` files |
| V | Microservices | Split one big service into smaller independent ones |
| VI | API Gateway | One entry point that routes to all the microservices |

---

## Project layout

```
src/main/java/edu/eci/arsw/
├── tcp/              # Part I
├── http/             # Part II
├── rmi/              # Part III
├── grpc/             # Part IV
├── microservices/    # Part V
└── gateway/          # Part VI

src/main/proto/       # gRPC contracts (.proto files)
```

---

## How to build

You need Java 21 and Maven. Open in IntelliJ and run:

```bash
mvn clean compile
```

If you're using gRPC parts (IV–VI), after compiling mark these folders as **Generated Sources Root** in IntelliJ:
- `target/generated-sources/protobuf/java`
- `target/generated-sources/protobuf/grpc-java`

---

## How to run each part

Every part has a server and a client. Start the server first, then run the client in a separate terminal.

### Part I — TCP Sockets
```
MovieServer   → port 35000    then run MovieClient
RoomServer    → port 35001    then run RoomClient
```

### Part II — HTTP Server
```
MovieHttpServer  → port 8080
  GET http://localhost:8080/movie?id=1

RoomHttpServer   → port 8081
  GET  http://localhost:8081/rooms
  GET  http://localhost:8081/rooms?id=E303
  POST http://localhost:8081/rooms/reserve?id=E303
  POST http://localhost:8081/rooms/release?id=E303
```

### Part III — Java RMI
```
MovieRmiServer         → port 23000    then run MovieRmiClient
LabInventoryRmiServer  → port 23001    then run LabInventoryRmiClient
```

### Part IV — gRPC
```
MovieGrpcServer        → port 50051    then run MovieGrpcClient
AppointmentGrpcServer  → port 50052    then run AppointmentGrpcClient
```

### Part V — Microservices
Start all three servers, then run the client:
```
MedicalGrpcServer     → port 50053
GymGrpcServer         → port 50054
RecreationGrpcServer  → port 50055
WellnessClient        → talks to all three directly
```

### Part VI — API Gateway
Start the backend services, then run the gateway:
```
AppointmentGrpcServer  → port 50052
MedicalGrpcServer      → port 50053
GymGrpcServer          → port 50054
RecreationGrpcServer   → port 50055

WellnessGateway        → single entry point for the client
```

---

## The big picture

Each style exists because the previous one had a limitation:

- **TCP** gives you full control but you have to invent your own protocol
- **HTTP** makes it accessible to any client without custom code
- **RMI** lets you call remote methods like local ones, but only works in Java
- **gRPC** keeps the RPC feel but adds typed contracts and works across languages
- **Microservices** splits responsibilities so each service can evolve independently
- **API Gateway** hides the complexity — the client only needs one address

---

## Author

Daniel Rayo — Escuela Colombiana de Ingeniería Julio Garavito, ARSW 2026-I