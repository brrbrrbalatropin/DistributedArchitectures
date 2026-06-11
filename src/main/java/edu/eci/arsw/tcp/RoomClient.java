package edu.eci.arsw.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class RoomClient {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Operations: CONSULTAR_SALON, RESERVAR_SALON, LIBERAR_SALON");
        System.out.print("Operation: ");
        String operation = scanner.nextLine().trim();

        System.out.print("Room ID (E301-E304): ");
        String roomId = scanner.nextLine().trim();

        Socket socket = new Socket("127.0.0.1", 35001);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        out.println(operation + "," + roomId);
        String response = in.readLine();
        System.out.println("Server response: " + response);

        in.close();
        out.close();
        socket.close();
    }
}