package com.nomba.dsaapp.controller;

import com.nomba.dsaapp.dto.request.CheckOutRequest;
import com.nomba.dsaapp.service.CheckOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CheckOutController {

    private final CheckOutService checkOutService;

    @PostMapping("/checkout")
    public ResponseEntity<Void> checkOut(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody CheckOutRequest request) {
        checkOutService.checkOut(bearerToken,request.getSessionId(), request.getTimestamp(), request.getLatLongs());
        return ResponseEntity.ok().build();
    }
}