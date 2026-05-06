package com.nomba.dsaapp.dto.request;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CheckOutRequest {
    private String bearerToken;
    private UUID sessionId;
    private long timestamp;
    private List<long[]> latLongs;
}