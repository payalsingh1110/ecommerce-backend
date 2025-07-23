package com.payal.ecom.services.customer.cart;


import com.payal.ecom.dto.AddProductInCartDto;
import com.payal.ecom.entity.CartItem;
import com.payal.ecom.entity.Order;
import com.payal.ecom.entity.Product;
import com.payal.ecom.entity.User;
import com.payal.ecom.enums.OrderStatus;
import com.payal.ecom.repository.CartItemsRepository;
import com.payal.ecom.repository.OrderRepository;
import com.payal.ecom.repository.ProductRepository;
import com.payal.ecom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private ProductRepository productRepository;

    public ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto){
        Order activeOrder = orderRepository.findByUserIdAndStatus(addProductInCartDto.getUserId(), OrderStatus.Pending);
        Optional<CartItem> optionalCartItem = cartItemsRepository.findByProductIdAndOrderIdAndUserId
                (addProductInCartDto.getProductId(), activeOrder.getId(), addProductInCartDto.getUserId());

        if(optionalCartItem.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }else{
            Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
            Optional<User> optionalUser = userRepository.findById(addProductInCartDto.getUserId());

            if(optionalProduct.isPresent() && optionalUser.isPresent() ){
                CartItem cart = new CartItem();
                cart.setProduct(optionalProduct.get());
                cart.setPrice(optionalProduct.get().getPrice());
                cart.setQuantity(1L);
                cart.setUser(optionalUser.get());
                cart.setOrder(activeOrder);

                CartItem updatedCart = cartItemsRepository.save(cart);

                activeOrder.setTotalAmount(activeOrder.getTotalAmount() + cart.getPrice());
                activeOrder.setAmount(activeOrder.getAmount() + cart.getPrice());
                activeOrder.getCartItems().add(cart);

                orderRepository.save(activeOrder);

                return ResponseEntity.status(HttpStatus.CREATED).body(cart);

            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Product not found");


            }
        }
    }
}
