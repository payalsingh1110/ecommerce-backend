package com.payal.ecom.controller.customer;

import com.payal.ecom.dto.OrderProductResponseDto;
import com.payal.ecom.dto.ReviewDto;
import com.payal.ecom.services.customer.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/review")
    public ResponseEntity<?> giveReview(@ModelAttribute ReviewDto reviewDto) throws IOException {
       log.info("Product ID:{} " , reviewDto.getProductId());
        ReviewDto reviewDto1 = reviewService.giveReview(reviewDto);
        if(reviewDto1 == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDto1);
    }
}
