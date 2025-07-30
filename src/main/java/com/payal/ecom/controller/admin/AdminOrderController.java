package com.payal.ecom.controller.admin;


import com.payal.ecom.dto.AnalyticsResponse;
import com.payal.ecom.dto.OrderDto;
import com.payal.ecom.services.admin.adminOrder.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping("/placeOrders")
    public ResponseEntity<List<OrderDto>> getAllPlaceOrders(){
        return ResponseEntity.ok(adminOrderService.getAllPlaceOrders());
    }

    @GetMapping("/order/{orderId}/{status}")
    public ResponseEntity<?> changeOrderStatus(@PathVariable Long orderId, @PathVariable String status){
        OrderDto orderDto = adminOrderService.changeOrderStatus(orderId, status);
        if(orderDto == null){
            return new ResponseEntity<>("Something went wrong!", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(orderDto);
    }

    @GetMapping("/order/analytics")
    public ResponseEntity<AnalyticsResponse> getAnalytics(){
        return ResponseEntity.ok(adminOrderService.calculateAnalytics());
    }


}
