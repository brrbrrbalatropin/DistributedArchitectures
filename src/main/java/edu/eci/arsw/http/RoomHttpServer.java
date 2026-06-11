package edu.eci.arsw.http;

import com.sun.net.httpserver.*;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class RoomHttpServer {

    public static void main(String[] args) throws Exception {
        RoomRepository repository = new RoomRepository();
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

        server.createContext("/rooms", new RoomsHandler(repository));
        server.createContext("/rooms/reserve", new ReserveHandler(repository));
        server.createContext("/rooms/release", new ReleaseHandler(repository));

        server.setExecutor(null);
        server.start();
        System.out.println("RoomHttpServer on http://localhost:8081");
        System.out.println("  GET  /rooms");
        System.out.println("  GET  /rooms?id=E303");
        System.out.println("  POST /rooms/reserve?id=E303");
        System.out.println("  POST /rooms/release?id=E303");
    }

    static class RoomsHandler implements HttpHandler {
        private RoomRepository repository;
        public RoomsHandler(RoomRepository r) { this.repository = r; }

        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendResponse(exchange, 405, "Method Not Allowed"); return;
                }
                String query = exchange.getRequestURI().getQuery();
                String response;
                if (query != null && query.startsWith("id=")) {
                    Room room = repository.findById(query.substring(3));
                    if (room == null) { sendResponse(exchange, 404, "{\"error\":\"Room not found\"}"); return; }
                    response = room.toJson();
                } else {
                    StringBuilder sb = new StringBuilder("[");
                    boolean first = true;
                    for (Room room : repository.findAll()) {
                        if (!first) sb.append(",");
                        sb.append(room.toJson());
                        first = false;
                    }
                    sb.append("]");
                    response = sb.toString();
                }
                sendResponse(exchange, 200, response);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    static class ReserveHandler implements HttpHandler {
        private RoomRepository repository;
        public ReserveHandler(RoomRepository r) { this.repository = r; }

        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendResponse(exchange, 405, "Method Not Allowed"); return;
                }
                String query = exchange.getRequestURI().getQuery();
                if (query == null || !query.startsWith("id=")) {
                    sendResponse(exchange, 400, "{\"error\":\"Missing room id\"}"); return;
                }
                Room room = repository.findById(query.substring(3));
                if (room == null) { sendResponse(exchange, 404, "{\"error\":\"Room not found\"}"); return; }
                if (room.isReserved()) { sendResponse(exchange, 409, "{\"error\":\"Already reserved\"}"); return; }
                room.reserve();
                sendResponse(exchange, 200, "{\"result\":\"RESERVA_EXITOSA\"}");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    static class ReleaseHandler implements HttpHandler {
        private RoomRepository repository;
        public ReleaseHandler(RoomRepository r) { this.repository = r; }

        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendResponse(exchange, 405, "Method Not Allowed"); return;
                }
                String query = exchange.getRequestURI().getQuery();
                if (query == null || !query.startsWith("id=")) {
                    sendResponse(exchange, 400, "{\"error\":\"Missing room id\"}"); return;
                }
                Room room = repository.findById(query.substring(3));
                if (room == null) { sendResponse(exchange, 404, "{\"error\":\"Room not found\"}"); return; }
                if (!room.isReserved()) { sendResponse(exchange, 409, "{\"error\":\"Not reserved\"}"); return; }
                room.release();
                sendResponse(exchange, 200, "{\"result\":\"LIBERACION_EXITOSA\"}");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private static void sendResponse(HttpExchange exchange, int code, String body) throws Exception {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, body.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(body.getBytes());
        os.close();
    }
}