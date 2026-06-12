package edu.eci.arsw.microservices;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.HashMap;
import java.util.Map;

public class RecreationGrpcServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50055)
                .addService(new RecreationServiceImpl()).build();
        server.start();
        System.out.println("RecreationService gRPC started on port 50055");
        server.awaitTermination();
    }

    static class RecreationServiceImpl extends RecreationServiceGrpc.RecreationServiceImplBase {
        private Map<String, ResourceInfo> resources = new HashMap<>();

        public RecreationServiceImpl() {
            resources.put("FUTBOL-1", ResourceInfo.newBuilder()
                    .setResourceId("FUTBOL-1").setName("Football Field 1").setAvailable(true).build());
            resources.put("FUTBOL-2", ResourceInfo.newBuilder()
                    .setResourceId("FUTBOL-2").setName("Football Field 2").setAvailable(true).build());
            resources.put("PING-PONG", ResourceInfo.newBuilder()
                    .setResourceId("PING-PONG").setName("Ping Pong Table").setAvailable(true).build());
            resources.put("BIBLIOTECA", ResourceInfo.newBuilder()
                    .setResourceId("BIBLIOTECA").setName("Study Room").setAvailable(true).build());
        }

        @Override
        public void getResources(EmptyRequest request,
                                 StreamObserver<ResourceList> responseObserver) {
            responseObserver.onNext(ResourceList.newBuilder()
                    .addAllResources(resources.values()).build());
            responseObserver.onCompleted();
        }

        @Override
        public void reserveResource(ReservationRequest request,
                                    StreamObserver<ReservationResponse> responseObserver) {
            ResourceInfo resource = resources.get(request.getResourceId());
            ReservationResponse response;

            if (resource == null) {
                response = ReservationResponse.newBuilder().setSuccess(false)
                        .setMessage("Resource not found").build();
            } else if (!resource.getAvailable()) {
                response = ReservationResponse.newBuilder().setSuccess(false)
                        .setMessage("Resource not available").build();
            } else {
                resources.put(request.getResourceId(),
                        resource.toBuilder().setAvailable(false).build());
                response = ReservationResponse.newBuilder().setSuccess(true)
                        .setMessage("Reserved for student " + request.getStudentId()).build();
            }
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}