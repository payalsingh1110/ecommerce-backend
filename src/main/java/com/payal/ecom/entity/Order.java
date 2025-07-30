package com.payal.ecom.entity;

import com.payal.ecom.dto.OrderDto;
import com.payal.ecom.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String orderDescription;

    private Date date;

    private Long amount;     //amount after applying coupon code

    private String address;

    private String payment;

    private OrderStatus orderStatus;

    private Long totalAmount;   // total amount of order


    private Long discount;

    @Column(unique = true, nullable = true)
    private UUID trackingId;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="coupon_id", referencedColumnName = "id")
    private Coupon coupon;




    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<CartItem> cartItems;



    public OrderDto getOrderDto(){
        OrderDto orderDto = new OrderDto();

        orderDto.setId(id);
        orderDto.setOrderDescription(orderDescription);
        orderDto.setAddress(address);
        orderDto.setTrackingId(trackingId);
        orderDto.setTotalAmount(totalAmount);
        orderDto.setAmount(amount);
        orderDto.setDate(date);
        orderDto.setOrderStatus(orderStatus);
        orderDto.setUserName(user.getName());
        if(coupon != null ){
            orderDto.setCouponName(coupon.getName());
        }
        return orderDto;
    }




}
