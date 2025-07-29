package com.payal.ecom.services.customer.review;

import com.payal.ecom.dto.OrderProductResponseDto;

public interface ReviewService {

    OrderProductResponseDto getOrderedProductDetailsByOrderId(Long orderId);
}
