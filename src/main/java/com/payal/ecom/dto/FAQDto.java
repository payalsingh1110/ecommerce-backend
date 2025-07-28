package com.payal.ecom.dto;

import com.payal.ecom.entity.Product;
import lombok.Data;


@Data
public class FAQDto {

    private Long id;

    private String question;

    private String answer;

    private Long productId;

}
