package edu.eci.arsw.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class LabInventoryRmiClient {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 23001);
        LabInventoryService service =
                (LabInventoryService) registry.lookup("labInventoryService");

        Scanner scanner = new Scanner(System.in);
        System.out.println("1. List all  2. Query  3. Reserve  4. Release");
        System.out.print("Option: ");
        String option = scanner.nextLine().trim();

        switch (option) {
            case "1": service.consultarEquipos().forEach(System.out::println); break;
            case "2":
                System.out.print("Code: ");
                System.out.println(service.consultarEquipo(scanner.nextLine().trim())); break;
            case "3":
                System.out.print("Code: ");
                System.out.println(service.reservarEquipo(scanner.nextLine().trim())
                        ? "RESERVA_EXITOSA" : "ERROR: could not reserve"); break;
            case "4":
                System.out.print("Code: ");
                System.out.println(service.liberarEquipo(scanner.nextLine().trim())
                        ? "LIBERACION_EXITOSA" : "ERROR: could not release"); break;
            default: System.out.println("Invalid option");
        }
    }
}