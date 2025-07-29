package com.payal.ecom.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderProductResponseDto {

    private List<ProductDto> productDtoList;

    private Long orderAmount;
}
