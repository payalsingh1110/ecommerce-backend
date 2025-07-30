package com.payal.ecom.services.admin.adminOrder;

import com.payal.ecom.dto.AnalyticsResponse;
import com.payal.ecom.dto.OrderDto;

import java.util.List;

public interface AdminOrderService {

    List<OrderDto> getAllPlaceOrders();

    OrderDto changeOrderStatus(Long orderId, String status);

    AnalyticsResponse calculateAnalytics();


}
