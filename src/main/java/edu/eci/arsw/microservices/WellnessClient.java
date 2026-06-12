package edu.eci.arsw.microservices;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Scanner;

public class WellnessClient {

    public static void main(String[] args) {
        ManagedChannel medicalChannel = ManagedChannelBuilder
                .forAddress("localhost", 50053).usePlaintext().build();
        ManagedChannel gymChannel = ManagedChannelBuilder
                .forAddress("localhost", 50054).usePlaintext().build();
        ManagedChannel recreationChannel = ManagedChannelBuilder
                .forAddress("localhost", 50055).usePlaintext().build();

        MedicalServiceGrpc.MedicalServiceBlockingStub medicalStub =
                MedicalServiceGrpc.newBlockingStub(medicalChannel);
        GymServiceGrpc.GymServiceBlockingStub gymStub =
                GymServiceGrpc.newBlockingStub(gymChannel);
        RecreationServiceGrpc.RecreationServiceBlockingStub recreationStub =
                RecreationServiceGrpc.newBlockingStub(recreationChannel);

        Scanner scanner = new Scanner(System.in);
        System.out.println("1. List medical specialties");
        System.out.println("2. Reserve gym session");
        System.out.println("3. List recreation resources");
        System.out.println("4. Reserve recreation resource");
        System.out.print("Option: ");
        String option = scanner.nextLine().trim();

        switch (option) {
            case "1":
                medicalStub.getSpecialties(EmptyRequest.newBuilder().build())
                        .getSpecialtiesList().forEach(s -> System.out.println(
                                "[" + s.getSpecialtyId() + "] " + s.getName()
                                + " - Slots: " + s.getAvailableSlots()));
                break;
            case "2":
                System.out.print("Student ID: "); String sid = scanner.nextLine().trim();
                System.out.print("Time slot (e.g. 07:00-08:00): "); String slot = scanner.nextLine().trim();
                GymResponse gr = gymStub.reserveSession(GymRequest.newBuilder()
                        .setStudentId(sid).setTimeSlot(slot).build());
                System.out.println(gr.getSuccess() ? "Reserved! ID: " + gr.getSessionId() : "Error: " + gr.getMessage());
                break;
            case "3":
                recreationStub.getResources(EmptyRequest.newBuilder().build())
                        .getResourcesList().forEach(r -> System.out.println(
                                "[" + r.getResourceId() + "] " + r.getName()
                                + " - " + (r.getAvailable() ? "AVAILABLE" : "TAKEN")));
                break;
            case "4":
                System.out.print("Student ID: "); String s2 = scanner.nextLine().trim();
                System.out.print("Resource ID: "); String rid = scanner.nextLine().trim();
                ReservationResponse rr = recreationStub.reserveResource(ReservationRequest.newBuilder()
                        .setStudentId(s2).setResourceId(rid).build());
                System.out.println(rr.getSuccess() ? rr.getMessage() : "Error: " + rr.getMessage());
                break;
        }

        medicalChannel.shutdown();
        gymChannel.shutdown();
        recreationChannel.shutdown();
    }
}