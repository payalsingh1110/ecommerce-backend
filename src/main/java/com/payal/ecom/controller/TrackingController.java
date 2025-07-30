package com.payal.ecom.controller;



import com.payal.ecom.dto.OrderDto;
import com.payal.ecom.entity.Order;
import com.payal.ecom.services.customer.cart.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
@Slf4j
public class TrackingController {
    private final CartService cartService;

    @GetMapping("/{trackingId}")
    public ResponseEntity<OrderDto> getOrderByTrackingId(@PathVariable String trackingId) {
        try {
            OrderDto order = cartService.getOrderByTrackingId(trackingId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
