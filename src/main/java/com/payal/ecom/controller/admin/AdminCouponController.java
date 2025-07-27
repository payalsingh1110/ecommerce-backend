package com.payal.ecom.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payal.ecom.entity.Coupon;
import com.payal.ecom.exceptions.ValidationException;
import com.payal.ecom.services.admin.coupon.AdminCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final AdminCouponService adminCouponService;


    @PostMapping
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon){

        try {
            String json = new ObjectMapper().writeValueAsString(coupon);
            System.out.println("RAW Coupon JSON: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{

            Coupon createdCoupon = adminCouponService.createCoupon(coupon);
            return ResponseEntity.ok(createdCoupon);
        }catch (ValidationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons(){
        return ResponseEntity.ok(adminCouponService.getAllCoupons());
    }
}
