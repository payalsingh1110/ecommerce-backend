package com.payal.ecom.dto;

import com.payal.ecom.entity.CartItem;
import com.payal.ecom.entity.User;
import com.payal.ecom.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDto {

    private Long id;

    private  String orderDescription;

    private Date date;

    private Long amount;     //amount after applying coupon code

    private String address;

    private String payment;

    private OrderStatus orderStatus;

    private Long totalAmount;   // total amount of order


    private Long discount;

    private UUID trackingId;


    private String userName;


    private List<CartItemsDto> cartItems;

    private String CouponName;
}
