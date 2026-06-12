package edu.eci.arsw.microservices;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.HashMap;
import java.util.Map;

public class MedicalGrpcServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50053)
                .addService(new MedicalServiceImpl()).build();
        server.start();
        System.out.println("MedicalService gRPC started on port 50053");
        server.awaitTermination();
    }

    static class MedicalServiceImpl extends MedicalServiceGrpc.MedicalServiceImplBase {
        private Map<String, SpecialtyResponse> specialties = new HashMap<>();

        public MedicalServiceImpl() {
            specialties.put("MED", SpecialtyResponse.newBuilder()
                    .setSpecialtyId("MED").setName("General Medicine")
                    .setDescription("General health consultations")
                    .setAvailableSlots(10).setFound(true).build());
            specialties.put("PSY", SpecialtyResponse.newBuilder()
                    .setSpecialtyId("PSY").setName("Psychology")
                    .setDescription("Mental health support")
                    .setAvailableSlots(5).setFound(true).build());
            specialties.put("DEN", SpecialtyResponse.newBuilder()
                    .setSpecialtyId("DEN").setName("Dentistry")
                    .setDescription("Dental care services")
                    .setAvailableSlots(8).setFound(true).build());
        }

        @Override
        public void getSpecialties(EmptyRequest request,
                                   StreamObserver<SpecialtyList> responseObserver) {
            responseObserver.onNext(SpecialtyList.newBuilder()
                    .addAllSpecialties(specialties.values()).build());
            responseObserver.onCompleted();
        }

        @Override
        public void getSpecialty(SpecialtyRequest request,
                                 StreamObserver<SpecialtyResponse> responseObserver) {
            SpecialtyResponse response = specialties.get(request.getSpecialtyId());
            if (response == null) response = SpecialtyResponse.newBuilder()
                    .setSpecialtyId(request.getSpecialtyId()).setFound(false).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}