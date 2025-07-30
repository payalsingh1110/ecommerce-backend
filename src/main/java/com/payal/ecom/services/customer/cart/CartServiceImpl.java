package com.payal.ecom.services.customer.cart;

import com.payal.ecom.dto.AddProductInCartDto;
import com.payal.ecom.dto.CartItemsDto;
import com.payal.ecom.dto.OrderDto;
import com.payal.ecom.dto.PlaceOrderDto;
import com.payal.ecom.entity.*;
import com.payal.ecom.enums.OrderStatus;
import com.payal.ecom.exceptions.ValidationException;
import com.payal.ecom.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.*;
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


    @Autowired
    private CouponRepository couponRepository;

    /**
     * Add product to cart
     */
    @Transactional
    public ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto) {
        Long userId = addProductInCartDto.getUserId();
        Long productId = addProductInCartDto.getProductId();

        // Find or create active order   

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


        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if product is already in cart
        Optional<CartItem> optionalCartItem = cartItemsRepository
                .findByProductIdAndOrderIdAndUserId(productId, activeOrder.getId(), userId);

        if (optionalCartItem.isPresent()) {
 
            CartItem existingCartItem = optionalCartItem.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
            existingCartItem.setPrice(existingCartItem.getQuantity() * product.getPrice());
            cartItemsRepository.save(existingCartItem);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setPrice(product.getPrice());
            newCartItem.setQuantity(1L);
            newCartItem.setUser(activeOrder.getUser());
            newCartItem.setOrder(activeOrder);
            cartItemsRepository.save(newCartItem);
        }

        recalculateOrderTotals(activeOrder);
        orderRepository.save(activeOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(activeOrder.getOrderDto());
    }

    /**
     * Get Cart for a user
     */
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

        OrderDto orderDto = activeOrder.getOrderDto();
        orderDto.setCartItems(cartItemsDtoList);
        orderDto.setCouponName(activeOrder.getCoupon() != null ? activeOrder.getCoupon().getName() : null);

        return ResponseEntity.ok(orderDto);
    }

    /**
     * Apply coupon
     */
    public OrderDto applyCoupon(Long userId, String code) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.Pending);
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ValidationException("Coupon not found"));

        if (couponIsExpired(coupon)) {
            throw new ValidationException("Coupon has expired.");
        }

        double discountAmount = ((coupon.getDiscount() / 100.0) * activeOrder.getTotalAmount());
        double netAmount = activeOrder.getTotalAmount() - discountAmount;

        activeOrder.setAmount((long) netAmount);
        activeOrder.setDiscount((long) discountAmount);
        activeOrder.setCoupon(coupon);

        orderRepository.save(activeOrder);
        return activeOrder.getOrderDto();
    }

    private boolean couponIsExpired(Coupon coupon) {
        Date currentDate = new Date();
        Date expirationDate = coupon.getExpirationDate();
        return expirationDate != null && currentDate.after(expirationDate);
    }

    /**
     * Increase quantity
     */
    public OrderDto increaseProductQuantity(AddProductInCartDto addProductInCartDto) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(), OrderStatus.Pending);
        Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());

        Optional<CartItem> optionalCartItem = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
                addProductInCartDto.getProductId(), activeOrder.getId(), addProductInCartDto.getUserId()
        );

        if (optionalCartItem.isPresent() && optionalProduct.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            Product product = optionalProduct.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItem.setPrice(cartItem.getQuantity() * product.getPrice());

            cartItemsRepository.save(cartItem);
            recalculateOrderTotals(activeOrder);
            orderRepository.save(activeOrder);
            return activeOrder.getOrderDto();
        }
        return null;
    }

    /**
     * Decrease quantity
     */
    public OrderDto decreaseProductQuantity(AddProductInCartDto addProductInCartDto) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(), OrderStatus.Pending);
        Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());

        Optional<CartItem> optionalCartItem = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
                addProductInCartDto.getProductId(), activeOrder.getId(), addProductInCartDto.getUserId()
        );

        if (optionalCartItem.isPresent() && optionalProduct.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            Product product = optionalProduct.get();

            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                cartItem.setPrice(cartItem.getQuantity() * product.getPrice());
                cartItemsRepository.save(cartItem);
            } else {
                // If quantity is 1 and user tries to decrease -> remove the product
                cartItemsRepository.delete(cartItem);
            }

            recalculateOrderTotals(activeOrder);
            orderRepository.save(activeOrder);
            return activeOrder.getOrderDto();
        }
        return null;
    }

    /**
     * Remove product completely
     */
    @Override
    public OrderDto removeProductFromCart(AddProductInCartDto addProductInCartDto) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(), OrderStatus.Pending);
        if (activeOrder == null) {
            throw new RuntimeException("No active cart found for this user.");
        }

        Optional<CartItem> optionalCartItem = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
                addProductInCartDto.getProductId(), activeOrder.getId(), addProductInCartDto.getUserId()
        );

        if (optionalCartItem.isPresent()) {
            cartItemsRepository.delete(optionalCartItem.get());
            recalculateOrderTotals(activeOrder);
            orderRepository.save(activeOrder);
        }

        return activeOrder.getOrderDto();
    }
  
   /**
     * Recalculate order totals
     */
    private void recalculateOrderTotals(Order activeOrder) {
        long total = activeOrder.getCartItems()
                .stream()
                .mapToLong(CartItem::getPrice)
                .sum();
        activeOrder.setTotalAmount(total);

        // If cart is empty, remove coupon
        if (total == 0) {
            activeOrder.setCoupon(null);
            activeOrder.setDiscount(null);
            activeOrder.setAmount(0L);
            return;
        }

        long discount = (activeOrder.getDiscount() != null) ? activeOrder.getDiscount() : 0;
        activeOrder.setAmount(total - discount);
    }
    /**
     * Place Order
     */
    public OrderDto placeOrder(PlaceOrderDto placeOrderDto) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(
                placeOrderDto.getUserId(), OrderStatus.Pending);

        // If there is no cart
        if (activeOrder == null) {
            throw new RuntimeException("No active cart found. Please add products before placing an order.");
        }

        Optional<User> optionalUser = userRepository.findById(placeOrderDto.getUserId());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Placed Active order
        activeOrder.setOrderDescription(placeOrderDto.getOrderDescription());
        activeOrder.setAddress(placeOrderDto.getAddress());
        activeOrder.setDate(new Date());
        activeOrder.setOrderStatus(OrderStatus.Placed);
        activeOrder.setTrackingId(UUID.randomUUID());
        orderRepository.save(activeOrder);

        //create New empty Pending order  so that user can  add product again
        Order newOrder = new Order();
        newOrder.setAmount(0L);
        newOrder.setTotalAmount(0L);
        newOrder.setDiscount(0L);
        newOrder.setUser(optionalUser.get());
        newOrder.setOrderStatus(OrderStatus.Pending);
        orderRepository.save(newOrder);

        return activeOrder.getOrderDto();
    }

    public List<OrderDto> getMyPlacedOrders(Long userId){

       return orderRepository.findByUserIdAndOrderStatusIn(userId, List.of(OrderStatus.Placed,OrderStatus.Shipped,
               OrderStatus.Delivered)).stream()
               .map(Order::getOrderDto)
               .collect(Collectors.toList());

    }

    public OrderDto getOrderByTrackingId(String trackingId) {
        UUID uuid;
        try {
            uuid = UUID.fromString(trackingId);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid tracking ID format.");
        }

        Order order = orderRepository.findByTrackingId(uuid)
                .orElseThrow(() -> new RuntimeException("Order not found with Tracking ID: " + trackingId));

        OrderDto dto = order.getOrderDto();
        dto.setCartItems(order.getCartItems()
                .stream()
                .map(CartItem::getCartDto)
                .collect(Collectors.toList())
        );

        dto.setCouponName(order.getCoupon() != null ? order.getCoupon().getName() : null);

        return dto;
    }


}