package com.payal.ecom.services.admin.adminOrder;

import com.payal.ecom.dto.AnalyticsResponse;
import com.payal.ecom.dto.OrderDto;
import com.payal.ecom.entity.Order;
import com.payal.ecom.enums.OrderStatus;
import com.payal.ecom.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderRepository orderRepository;

    public List<OrderDto> getAllPlaceOrders(){

        List<Order> orderList = orderRepository.
                findAllByOrderStatusIn(List.of(OrderStatus.Placed, OrderStatus.Shipped, OrderStatus.Delivered));

        return orderList
                .stream()
                .map(Order::getOrderDto)
                .collect(Collectors.toList());
    }

    public  OrderDto changeOrderStatus(Long orderId, String status){
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isPresent()){
            Order order = optionalOrder.get();

            if(Objects.equals(status,"Shipped")){
                order.setOrderStatus(OrderStatus.Shipped);
            }else if(Objects.equals(status,"Delivered")){
                order.setOrderStatus(OrderStatus.Delivered);
            }

            return orderRepository.save(order).getOrderDto();
        }
        return null;
    }

    public AnalyticsResponse calculateAnalytics(){
        LocalDate currentDate = LocalDate.now();
        LocalDate previousMonthDate = currentDate.minusMonths(1);

        Long currentMonthOrders = getTotalOrdersForMonth(currentDate.getMonthValue(), currentDate.getYear());
        Long previousMonthOrders = getTotalOrdersForMonth(currentDate.getMonthValue(), currentDate.getYear());

        Long currentMonthEarning = getTotalEarningForMonth(currentDate.getMonthValue(), currentDate.getYear());
        Long previousMonthEarning = getTotalEarningForMonth(currentDate.getMonthValue(), currentDate.getYear());

        Long placed = orderRepository.countByOrderStatus(OrderStatus.Placed);
        Long shipped = orderRepository.countByOrderStatus(OrderStatus.Shipped);
        Long delivered = orderRepository.countByOrderStatus(OrderStatus.Delivered);

        return new AnalyticsResponse(placed,shipped,delivered, currentMonthOrders,
                previousMonthOrders, currentMonthEarning, previousMonthEarning );
    }

    public Long getTotalOrdersForMonth(int month, int year){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0 );

        Date startOfMonth = calendar.getTime();

        // Move the calendar to the end of the specified month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE, 59 );
        calendar.set(Calendar.SECOND, 59);

        Date endOfMonth = calendar.getTime();

        List<Order> orders = orderRepository.findByDateBetweenAndOrderStatus(startOfMonth, endOfMonth, OrderStatus.Delivered);

        return (long)orders.size();

    }


    public Long getTotalEarningForMonth(int month, int year){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0 );

        Date startOfMonth = calendar.getTime();

        // Move the calendar to the end of the specified month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE, 59 );
        calendar.set(Calendar.SECOND, 59);

        Date endOfMonth = calendar.getTime();

        List<Order> orders = orderRepository.findByDateBetweenAndOrderStatus(startOfMonth, endOfMonth, OrderStatus.Delivered);

        Long sum = 0L;
        for(Order order: orders){
            sum += order.getAmount();
        }

        return sum;

    }



}
