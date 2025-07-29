package com.payal.ecom.services.customer.review;


import com.payal.ecom.dto.OrderProductResponseDto;
import com.payal.ecom.dto.ProductDto;
import com.payal.ecom.dto.ReviewDto;
import com.payal.ecom.entity.*;
import com.payal.ecom.repository.OrderRepository;
import com.payal.ecom.repository.ProductRepository;
import com.payal.ecom.repository.ReviewRepository;
import com.payal.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService{

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final ReviewRepository reviewRepository;

    public OrderProductResponseDto getOrderedProductDetailsByOrderId(Long orderId){
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        OrderProductResponseDto orderProductResponseDto = new OrderProductResponseDto();
        if(optionalOrder.isPresent()){
            orderProductResponseDto.setOrderAmount(optionalOrder.get().getAmount());

            List<ProductDto> productDtoList = new ArrayList<>();
            for(CartItem cartItem : optionalOrder.get().getCartItems()){
                ProductDto productDto = new ProductDto();

                productDto.setId(cartItem.getProduct().getId());
                productDto.setName(cartItem.getProduct().getName());
                productDto.setPrice(cartItem.getPrice());
                productDto.setQuantity(cartItem.getQuantity());

                productDto.setByteImg(cartItem.getProduct().getImg());

                productDtoList.add(productDto);
            }
            orderProductResponseDto.setProductDtoList(productDtoList);
        }
        return orderProductResponseDto;
    }

    public ReviewDto giveReview(ReviewDto reviewDto) throws IOException {
        log.info("Inside giveReview service,Product ID:{}", reviewDto.getProductId());
        Optional<Product> optionalProduct = productRepository.findById(reviewDto.getProductId());
        Optional<User> optionalUser = userRepository.findById(reviewDto.getUserId());

        if(optionalUser.isPresent() && optionalProduct.isPresent()){
            log.info("Inside giveReview service,Product ID:{}", reviewDto.getProductId());
            Review review = new Review();

            review.setDescription(reviewDto.getDescription());
            review.setRating(reviewDto.getRating());
            review.setUser(optionalUser.get());
            review.setProduct(optionalProduct.get());

            if (reviewDto.getImg() != null && !reviewDto.getImg().isEmpty()) {
                review.setImg(reviewDto.getImg().getBytes());
            } else {
                System.out.println("Review image is missing or empty");
                review.setImg(null); // or skip setting image
            }
         //   review.setImg(reviewDto.getImg().getBytes());//

            return reviewRepository.save(review).getDto();


        }
        return null;
    }
}
