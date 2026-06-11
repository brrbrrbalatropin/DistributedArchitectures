package edu.eci.arsw.http;

import com.sun.net.httpserver.*;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class MovieHttpServer {

    public static void main(String[] args) throws Exception {
        MovieRepository repository = new MovieRepository();
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/movie", new MovieHandler(repository));
        server.setExecutor(null);
        server.start();
        System.out.println("MovieHttpServer listening on http://localhost:8080/movie?id=1");
    }

    static class MovieHandler implements HttpHandler {
        private MovieRepository repository;

        public MovieHandler(MovieRepository repository) {
            this.repository = repository;
        }

        @Override
        public void handle(HttpExchange exchange) {
            try {
                String query = exchange.getRequestURI().getQuery();
                int id = extractId(query);
                Movie movie = repository.findById(id);

                String response = movie == null
                    ? "<html><body><h1>Movie not found</h1></body></html>"
                    : "<html><body><h1>" + movie.toText() + "</h1></body></html>";

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private int extractId(String query) {
            if (query == null || !query.startsWith("id=")) return -1;
            try { return Integer.parseInt(query.substring(3)); }
            catch (NumberFormatException e) { return -1; }
        }
    }
}