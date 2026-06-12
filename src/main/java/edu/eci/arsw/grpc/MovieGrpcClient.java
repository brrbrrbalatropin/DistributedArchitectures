package edu.eci.arsw.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Scanner;

public class MovieGrpcClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        MovieServiceGrpc.MovieServiceBlockingStub stub =
                MovieServiceGrpc.newBlockingStub(channel);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter movie ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        MovieRequest request = MovieRequest.newBuilder().setId(id).build();
        MovieResponse response = stub.getMovie(request);

        if (response.getFound()) {
            System.out.println("Movie: " + response.getTitle()
                    + " | Director: " + response.getDirector()
                    + " | Year: " + response.getYear());
        } else {
            System.out.println("Movie not found");
        }
        channel.shutdown();
    }
}