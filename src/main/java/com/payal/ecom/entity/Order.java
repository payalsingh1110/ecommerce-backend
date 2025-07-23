package com.payal.ecom.entity;

import com.payal.ecom.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

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

    private UUID trackingId;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;


    //Always match @OneToMany(mappedBy = "xyz") with @ManyToOne
    // on the child entity pointing to the same field name (xyz)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;


}
