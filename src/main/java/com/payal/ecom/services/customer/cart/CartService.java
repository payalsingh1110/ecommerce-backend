package com.payal.ecom.services.customer.cart;

import com.payal.ecom.dto.AddProductInCartDto;
import com.payal.ecom.dto.OrderDto;
import org.springframework.http.ResponseEntity;

public interface CartService {

    ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto);

    ResponseEntity<?> getCartByUserId(Long userId);

    OrderDto applyCoupon(Long userId, String code);
}
