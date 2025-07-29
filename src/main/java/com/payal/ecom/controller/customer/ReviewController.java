package com.payal.ecom.controller.customer;

import com.payal.ecom.dto.OrderProductResponseDto;
import com.payal.ecom.services.customer.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
@Slf4j
public class ReviewController  {

    private final ReviewService reviewService;


    @GetMapping("/ordered-products/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderProductResponseDto> getOrderedProductDetailsByOrderId(@PathVariable Long orderId){
        log.info("order Id: {}",orderId);
        return ResponseEntity.ok(reviewService.getOrderedProductDetailsByOrderId(orderId));
    }
}
