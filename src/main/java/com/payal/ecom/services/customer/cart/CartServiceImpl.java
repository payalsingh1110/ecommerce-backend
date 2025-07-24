package com.payal.ecom.services.customer.cart;


import com.payal.ecom.dto.AddProductInCartDto;
import com.payal.ecom.dto.CartItemsDto;
import com.payal.ecom.dto.OrderDto;
import com.payal.ecom.entity.CartItem;
import com.payal.ecom.entity.Order;
import com.payal.ecom.entity.Product;
import com.payal.ecom.entity.User;
import com.payal.ecom.enums.OrderStatus;
import com.payal.ecom.repository.CartItemsRepository;
import com.payal.ecom.repository.OrderRepository;
import com.payal.ecom.repository.ProductRepository;
import com.payal.ecom.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;


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

                    //Fix: Handle null activeOrder
    @Transactional  // to avoid session flush issues
    public ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto) {

        System.out.println("ram:");
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(), OrderStatus.Pending);

        //  If no active (pending) order, create a new one
        if (activeOrder == null) {
            Optional<User> optionalUser = userRepository.findById(addProductInCartDto.getUserId());

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            activeOrder = new Order();
            activeOrder.setUser(optionalUser.get());
            activeOrder.setOrderStatus(OrderStatus.Pending);
            activeOrder.setTotalAmount(0L);
            activeOrder.setAmount(0L);
            activeOrder.setCartItems(new ArrayList<>());  // Initialize empty cart list
            activeOrder = orderRepository.save(activeOrder);  // Save to generate ID
        }

        //  Check if product already exists in the cart
        Optional<CartItem> optionalCartItem = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
                addProductInCartDto.getProductId(), activeOrder.getId(), addProductInCartDto.getUserId()
        );

        if (optionalCartItem.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Product already in cart");
        }

        Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
        Optional<User> optionalUser = userRepository.findById(addProductInCartDto.getUserId());

        if (optionalProduct.isPresent() && optionalUser.isPresent()) {
            CartItem cart = new CartItem();
            cart.setProduct(optionalProduct.get());
            cart.setPrice(optionalProduct.get().getPrice());
            cart.setQuantity(1L);
            cart.setUser(optionalUser.get());
            cart.setOrder(activeOrder);

            CartItem updatedCart = cartItemsRepository.save(cart);

//            activeOrder.setTotalAmount(activeOrder.getTotalAmount() + cart.getPrice());
//            activeOrder.setAmount(activeOrder.getAmount() + cart.getPrice());
//            activeOrder.getCartItems().add(cart); // here you already saved the CartItem//
//            orderRepository.save(activeOrder);  //again adding to Order's cart list and saving order is redundant..Because JPA will automatically manage the relation based on cart.setOrder(activeOrder).

                    // Don’t double-link manually
            activeOrder.setTotalAmount(activeOrder.getTotalAmount() + cart.getPrice());
            activeOrder.setAmount(activeOrder.getAmount() + cart.getPrice());
            orderRepository.save(activeOrder); // no need to manually update cartItems list again


//            return ResponseEntity.status(HttpStatus.CREATED).body(cart);   Better Practice: Wrap CartItem return into DTO
            CartItemsDto cartDto = cart.getCartDto();
            return ResponseEntity.status(HttpStatus.CREATED).body(cartDto);

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Product not found");
        }
    }
// return type OrderDto
    public ResponseEntity<?> getCartByUserId(Long userId){
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId,OrderStatus.Pending);

                 // for handle nullpointerExpection
        if (activeOrder == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No active cart found for this user.");
        }

        List<CartItemsDto> cartItemsDtoList = activeOrder.getCartItems().stream().map(CartItem::getCartDto).collect(Collectors.toList());



        OrderDto orderDto = new OrderDto();
        orderDto.setAmount(activeOrder.getAmount());
        orderDto.setId(activeOrder.getId());
        orderDto.setOrderStatus(activeOrder.getOrderStatus());
        orderDto.setDiscount(activeOrder.getDiscount());
        orderDto.setTotalAmount(activeOrder.getTotalAmount());
        orderDto.setCartItems(cartItemsDtoList);

        return ResponseEntity.ok(orderDto);
    }
}
