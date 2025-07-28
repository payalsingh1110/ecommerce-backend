package com.payal.ecom.services.customer.cart;

import com.payal.ecom.dto.AddProductInCartDto;
import com.payal.ecom.dto.OrderDto;
import com.payal.ecom.dto.PlaceOrderDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CartService {

    ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto);

    ResponseEntity<?> getCartByUserId(Long userId);

    OrderDto applyCoupon(Long userId, String code);

    OrderDto increaseProductQuantity(AddProductInCartDto addProductInCartDto);

    OrderDto decreaseProductQuantity(AddProductInCartDto addProductInCartDto);

    OrderDto removeProductFromCart(AddProductInCartDto addProductInCartDto);

    OrderDto placeOrder(PlaceOrderDto placeOrderDto);

    List<OrderDto> getMyPlacedOrders(Long userId);


}
