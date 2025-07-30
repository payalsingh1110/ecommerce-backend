package com.payal.ecom.dto;

import lombok.Data;

@Data
public class WishlistDto {

    private Long userId;

    private Long productId;

    private Long id;

    private String productName;

    private String productDescription;

    private byte[] returnedImg;

    private Long price;
}
