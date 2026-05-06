package com.nomba.dsaapp.grpc;

import com.nomba.dsaapp.service.CheckInService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import io.grpc.Status;
import com.nomba.dsaapp.exception.*;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class CheckInGrpcService extends CheckInServiceGrpc.CheckInServiceImplBase{

    private final CheckInService checkInService;

    /**
     * Handles a check-in operation for a Direct Sales Agent (DSA). This method validates
     * the supplied geolocation and authentication details, initiates the check-in process,
     * and responds with either the session ID or an error in case of failure.
     *
     * @param request The request object containing the bearer token, timestamp, latitude,
     *                and longitude fields required for the check-in process.
     * @param responseObserver A gRPC stream observer used to send the check-in response
     *                         back to the client. The response includes the session ID if
     *                         the check-in is successful, or an error status otherwise.
     */
    @Override
    public void checkIn(CheckInRequest request, StreamObserver<CheckInResponse> responseObserver) {
        try {
            UUID sessionId = checkInService.checkIn(
                    request.getBearerToken(),
                    request.getTimestamp(),
                    request.getLatitude(),
                    request.getLongitude()
            );

            CheckInResponse response = CheckInResponse.newBuilder()
                    .setSessionId(sessionId.toString())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (DsaNotFoundException ex) {
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription(ex.getMessage()).asRuntimeException());
        } catch (GeofenceViolationException ex) {
            responseObserver.onError(
                    Status.PERMISSION_DENIED.withDescription(ex.getMessage()).asRuntimeException());
        } catch (IllegalStateException ex) {
            responseObserver.onError(
                    Status.FAILED_PRECONDITION.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception ex) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription("An unexpected error occurred").asRuntimeException());
        }
    }
}
