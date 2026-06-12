package edu.eci.arsw.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class MovieRmiClient {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 23000);
        MovieService service = (MovieService) registry.lookup("movieService");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter movie ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        Movie movie = service.getMovie(id);
        System.out.println(movie == null ? "Movie not found" : "Movie received: " + movie);
    }
}