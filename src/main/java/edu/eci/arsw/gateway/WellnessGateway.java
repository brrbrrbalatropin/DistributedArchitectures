package edu.eci.arsw.gateway;

import edu.eci.arsw.grpc.AppointmentServiceGrpc;
import edu.eci.arsw.grpc.AppointmentRequest;
import edu.eci.arsw.grpc.AppointmentResponse;
import edu.eci.arsw.grpc.AppointmentList;
import edu.eci.arsw.grpc.CancelRequest;
import edu.eci.arsw.grpc.CancelResponse;
import edu.eci.arsw.grpc.ServiceType;
import edu.eci.arsw.grpc.StudentRequest;
import edu.eci.arsw.microservices.EmptyRequest;
import edu.eci.arsw.microservices.GymRequest;
import edu.eci.arsw.microservices.GymResponse;
import edu.eci.arsw.microservices.GymServiceGrpc;
import edu.eci.arsw.microservices.MedicalServiceGrpc;
import edu.eci.arsw.microservices.RecreationServiceGrpc;
import edu.eci.arsw.microservices.ReservationRequest;
import edu.eci.arsw.microservices.ReservationResponse;
import edu.eci.arsw.microservices.SpecialtyList;
import edu.eci.arsw.microservices.ResourceList;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class WellnessGateway {

    private final ManagedChannel appointmentChannel;
    private final ManagedChannel medicalChannel;
    private final ManagedChannel gymChannel;
    private final ManagedChannel recreationChannel;

    private final AppointmentServiceGrpc.AppointmentServiceBlockingStub appointmentStub;
    private final MedicalServiceGrpc.MedicalServiceBlockingStub medicalStub;
    private final GymServiceGrpc.GymServiceBlockingStub gymStub;
    private final RecreationServiceGrpc.RecreationServiceBlockingStub recreationStub;

    public WellnessGateway() {
        appointmentChannel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();
        medicalChannel     = ManagedChannelBuilder.forAddress("localhost", 50053).usePlaintext().build();
        gymChannel         = ManagedChannelBuilder.forAddress("localhost", 50054).usePlaintext().build();
        recreationChannel  = ManagedChannelBuilder.forAddress("localhost", 50055).usePlaintext().build();

        appointmentStub = AppointmentServiceGrpc.newBlockingStub(appointmentChannel);
        medicalStub     = MedicalServiceGrpc.newBlockingStub(medicalChannel);
        gymStub         = GymServiceGrpc.newBlockingStub(gymChannel);
        recreationStub  = RecreationServiceGrpc.newBlockingStub(recreationChannel);
    }

    public void requestAppointment(String studentId, String name, String email,
                                   int serviceType, String date) {
        AppointmentResponse response = appointmentStub.requestAppointment(
                AppointmentRequest.newBuilder()
                        .setStudentId(studentId).setStudentName(name).setEmail(email)
                        .setServiceType(ServiceType.forNumber(serviceType)).setDate(date)
                        .build());
        System.out.println(response.getSuccess()
                ? "[Gateway] Appointment created. ID: " + response.getAppointmentId()
                : "[Gateway] Error: " + response.getMessage());
    }

    public void getStudentWellnessSummary(String studentId) {
        System.out.println("\n=== Wellness Summary for: " + studentId + " ===");

        AppointmentList appointments = appointmentStub.getAppointments(
                StudentRequest.newBuilder().setStudentId(studentId).build());
        System.out.println("\n-- Active Appointments --");
        if (appointments.getAppointmentsList().isEmpty()) {
            System.out.println("  No active appointments");
        } else {
            appointments.getAppointmentsList().forEach(a ->
                    System.out.println("  [" + a.getAppointmentId() + "] "
                            + a.getServiceType() + " | " + a.getDate() + " | " + a.getStatus()));
        }

        System.out.println("\n-- Available Medical Specialties --");
        medicalStub.getSpecialties(EmptyRequest.newBuilder().build())
                .getSpecialtiesList().forEach(s ->
                        System.out.println("  " + s.getName() + " - Slots: " + s.getAvailableSlots()));

        System.out.println("\n-- Recreation Resources --");
        recreationStub.getResources(EmptyRequest.newBuilder().build())
                .getResourcesList().forEach(r ->
                        System.out.println("  [" + r.getResourceId() + "] "
                                + r.getName() + " - " + (r.getAvailable() ? "AVAILABLE" : "TAKEN")));
    }

    public void reserveGymSession(String studentId, String timeSlot) {
        GymResponse response = gymStub.reserveSession(
                GymRequest.newBuilder().setStudentId(studentId).setTimeSlot(timeSlot).build());
        System.out.println(response.getSuccess()
                ? "[Gateway] Gym session reserved. ID: " + response.getSessionId()
                : "[Gateway] Error: " + response.getMessage());
    }

    public void reserveRecreationResource(String studentId, String resourceId) {
        ReservationResponse response = recreationStub.reserveResource(
                ReservationRequest.newBuilder()
                        .setStudentId(studentId).setResourceId(resourceId).build());
        System.out.println(response.getSuccess()
                ? "[Gateway] " + response.getMessage()
                : "[Gateway] Error: " + response.getMessage());
    }

    public void shutdown() {
        appointmentChannel.shutdown();
        medicalChannel.shutdown();
        gymChannel.shutdown();
        recreationChannel.shutdown();
    }

    public static void main(String[] args) {
        WellnessGateway gateway = new WellnessGateway();
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     WELLNESS API GATEWAY             ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║ 1. Request appointment               ║");
        System.out.println("║ 2. Get student wellness summary      ║");
        System.out.println("║ 3. Reserve gym session               ║");
        System.out.println("║ 4. Reserve recreation resource       ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("Option: ");
        String option = scanner.nextLine().trim();

        switch (option) {
            case "1": {
                System.out.print("Student ID: "); String sid = scanner.nextLine().trim();
                System.out.print("Name: "); String name = scanner.nextLine().trim();
                System.out.print("Email: "); String email = scanner.nextLine().trim();
                System.out.println("Service (0=MEDICINE, 1=PSYCHOLOGY, 2=DENTISTRY): ");
                int stype = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Date (YYYY-MM-DD): "); String date = scanner.nextLine().trim();
                gateway.requestAppointment(sid, name, email, stype, date);
                break;
            }
            case "2": {
                System.out.print("Student ID: ");
                gateway.getStudentWellnessSummary(scanner.nextLine().trim());
                break;
            }
            case "3": {
                System.out.print("Student ID: "); String sid = scanner.nextLine().trim();
                System.out.println("Slots: 07:00-08:00 | 08:00-09:00 | 12:00-13:00 | 17:00-18:00");
                System.out.print("Time slot: ");
                gateway.reserveGymSession(sid, scanner.nextLine().trim());
                break;
            }
            case "4": {
                System.out.print("Student ID: "); String sid = scanner.nextLine().trim();
                System.out.print("Resource ID (FUTBOL-1, FUTBOL-2, PING-PONG, BIBLIOTECA): ");
                gateway.reserveRecreationResource(sid, scanner.nextLine().trim());
                break;
            }
            default: System.out.println("Invalid option");
        }

        gateway.shutdown();
    }
}