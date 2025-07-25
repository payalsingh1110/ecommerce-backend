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
public class CartServiceImpl implements CartService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto) {
        Long userId = addProductInCartDto.getUserId();
        Long productId = addProductInCartDto.getProductId();

        // Find active (pending) order for this user
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.Pending);
        if (activeOrder == null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            activeOrder = new Order();
            activeOrder.setUser(user);
            activeOrder.setOrderStatus(OrderStatus.Pending);
            activeOrder.setTotalAmount(0L);
            activeOrder.setAmount(0L);
            activeOrder.setCartItems(new ArrayList<>());
            activeOrder = orderRepository.save(activeOrder);
        }

        // Check if product is already in the cart
        Optional<CartItem> optionalCartItem = cartItemsRepository.findByProductIdAndOrderIdAndUserId(productId, activeOrder.getId(), userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (optionalCartItem.isPresent()) {
            // Increment quantity
            CartItem existingCartItem = optionalCartItem.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
            existingCartItem.setPrice(existingCartItem.getQuantity() * product.getPrice());
            cartItemsRepository.save(existingCartItem);

            // Update order total
            activeOrder.setTotalAmount(activeOrder.getTotalAmount() + product.getPrice());
            activeOrder.setAmount(activeOrder.getAmount() + product.getPrice());
            orderRepository.save(activeOrder);

            return ResponseEntity.ok(existingCartItem.getCartDto());
        }

        // If not present, create new cart item
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setPrice(product.getPrice());
        newCartItem.setQuantity(1L);
        newCartItem.setUser(activeOrder.getUser());
        newCartItem.setOrder(activeOrder);

        cartItemsRepository.save(newCartItem);

        activeOrder.setTotalAmount(activeOrder.getTotalAmount() + product.getPrice());
        activeOrder.setAmount(activeOrder.getAmount() + product.getPrice());
        orderRepository.save(activeOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(newCartItem.getCartDto());
    }

    public ResponseEntity<?> getCartByUserId(Long userId) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.Pending);

        if (activeOrder == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No active cart found for this user.");
        }

        List<CartItemsDto> cartItemsDtoList = activeOrder.getCartItems()
                .stream()
                .map(CartItem::getCartDto)
                .collect(Collectors.toList());

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

