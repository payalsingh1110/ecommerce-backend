package com.payal.ecom.services.customer.review;


import com.payal.ecom.dto.OrderProductResponseDto;
import com.payal.ecom.dto.ProductDto;
import com.payal.ecom.entity.CartItem;
import com.payal.ecom.entity.Order;
import com.payal.ecom.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{

    private final OrderRepository orderRepository;

    public OrderProductResponseDto getOrderedProductDetailsByOrderId(Long orderId){
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        OrderProductResponseDto orderProductResponseDto = new OrderProductResponseDto();
        if(optionalOrder.isPresent()){
            orderProductResponseDto.setOrderAmount(optionalOrder.get().getAmount());

            List<ProductDto> productDtoList = new ArrayList<>();
            for(CartItem cartItem : optionalOrder.get().getCartItems()){
                ProductDto productDto = new ProductDto();

                productDto.setId(cartItem.getId());
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
}
