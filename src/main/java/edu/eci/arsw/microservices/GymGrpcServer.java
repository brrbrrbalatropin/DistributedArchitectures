package edu.eci.arsw.microservices;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.*;

public class GymGrpcServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50054)
                .addService(new GymServiceImpl()).build();
        server.start();
        System.out.println("GymService gRPC started on port 50054");
        server.awaitTermination();
    }

    static class GymServiceImpl extends GymServiceGrpc.GymServiceImplBase {
        private List<SessionInfo> sessions = new ArrayList<>();
        private Map<String, Integer> slots = new HashMap<>();

        public GymServiceImpl() {
            slots.put("07:00-08:00", 20);
            slots.put("08:00-09:00", 20);
            slots.put("12:00-13:00", 20);
            slots.put("17:00-18:00", 20);
        }

        @Override
        public void reserveSession(GymRequest request,
                                   StreamObserver<GymResponse> responseObserver) {
            String timeSlot = request.getTimeSlot();
            Integer available = slots.get(timeSlot);
            GymResponse response;

            if (available == null) {
                response = GymResponse.newBuilder().setSuccess(false)
                        .setMessage("Invalid time slot: " + timeSlot).build();
            } else if (available == 0) {
                response = GymResponse.newBuilder().setSuccess(false)
                        .setMessage("No slots available for " + timeSlot).build();
            } else {
                String sessionId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                slots.put(timeSlot, available - 1);
                sessions.add(SessionInfo.newBuilder()
                        .setSessionId(sessionId).setStudentId(request.getStudentId())
                        .setTimeSlot(timeSlot).build());
                response = GymResponse.newBuilder().setSuccess(true)
                        .setSessionId(sessionId).setMessage("Session reserved successfully").build();
            }
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void getSessions(EmptyRequest request,
                                StreamObserver<SessionList> responseObserver) {
            responseObserver.onNext(SessionList.newBuilder().addAllSessions(sessions).build());
            responseObserver.onCompleted();
        }
    }
}