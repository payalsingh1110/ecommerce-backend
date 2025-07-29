package com.payal.ecom.services.customer.review;

import com.payal.ecom.dto.OrderProductResponseDto;
import com.payal.ecom.dto.ReviewDto;

import java.io.IOException;

public interface ReviewService {

    OrderProductResponseDto getOrderedProductDetailsByOrderId(Long orderId);

    ReviewDto giveReview(ReviewDto reviewDto) throws IOException;


}
