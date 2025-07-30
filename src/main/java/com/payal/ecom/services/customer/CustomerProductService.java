package com.payal.ecom.services.customer;

import com.payal.ecom.dto.ProductDetailDto;
import com.payal.ecom.dto.ProductDto;

import java.util.List;

public interface CustomerProductService {

    List<ProductDto> getAllProducts();

    List<ProductDto> getAllProductsByName(String title);

    ProductDetailDto getProductDetailById(Long productId);
}
