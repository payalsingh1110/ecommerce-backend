package com.payal.ecom.services.customer.cart;

import com.payal.ecom.dto.AddProductInCartDto;
import com.payal.ecom.dto.OrderDto;
import com.payal.ecom.dto.PlaceOrderDto;
import com.payal.ecom.entity.Order;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface CartService {

    ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto);

    ResponseEntity<?> getCartByUserId(Long userId);

    OrderDto applyCoupon(Long userId, String code);

    OrderDto increaseProductQuantity(AddProductInCartDto addProductInCartDto);

    OrderDto decreaseProductQuantity(AddProductInCartDto addProductInCartDto);

    OrderDto removeProductFromCart(AddProductInCartDto addProductInCartDto);

    OrderDto placeOrder(PlaceOrderDto placeOrderDto);

    List<OrderDto> getMyPlacedOrders(Long userId);

    OrderDto getOrderByTrackingId(String trackingId);



}
