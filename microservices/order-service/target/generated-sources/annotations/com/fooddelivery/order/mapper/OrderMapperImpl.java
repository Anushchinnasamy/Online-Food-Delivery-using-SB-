package com.fooddelivery.order.mapper;

import com.fooddelivery.order.dto.response.OrderItemResponseDTO;
import com.fooddelivery.order.dto.response.OrderResponseDTO;
import com.fooddelivery.order.dto.response.OrderSummaryResponseDTO;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-28T00:20:56+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderResponseDTO toResponseDTO(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();

        orderResponseDTO.setItems( toItemResponseDTOList( order.getOrderItems() ) );
        orderResponseDTO.setId( order.getId() );
        orderResponseDTO.setOrderNumber( order.getOrderNumber() );
        orderResponseDTO.setRestaurantId( order.getRestaurantId() );
        orderResponseDTO.setTotalAmount( order.getTotalAmount() );
        orderResponseDTO.setOrderStatus( order.getOrderStatus() );
        orderResponseDTO.setPaymentStatus( order.getPaymentStatus() );
        orderResponseDTO.setSpecialInstructions( order.getSpecialInstructions() );
        orderResponseDTO.setEstimatedDeliveryTime( order.getEstimatedDeliveryTime() );
        orderResponseDTO.setActualDeliveryTime( order.getActualDeliveryTime() );
        orderResponseDTO.setCreatedAt( order.getCreatedAt() );
        orderResponseDTO.setUpdatedAt( order.getUpdatedAt() );

        return orderResponseDTO;
    }

    @Override
    public OrderSummaryResponseDTO toSummaryResponseDTO(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderSummaryResponseDTO orderSummaryResponseDTO = new OrderSummaryResponseDTO();

        orderSummaryResponseDTO.setId( order.getId() );
        orderSummaryResponseDTO.setOrderNumber( order.getOrderNumber() );
        orderSummaryResponseDTO.setRestaurantId( order.getRestaurantId() );
        orderSummaryResponseDTO.setTotalAmount( order.getTotalAmount() );
        orderSummaryResponseDTO.setOrderStatus( order.getOrderStatus() );
        orderSummaryResponseDTO.setPaymentStatus( order.getPaymentStatus() );
        orderSummaryResponseDTO.setEstimatedDeliveryTime( order.getEstimatedDeliveryTime() );
        orderSummaryResponseDTO.setCreatedAt( order.getCreatedAt() );

        orderSummaryResponseDTO.setItemCount( order.getTotalItemCount() );

        return orderSummaryResponseDTO;
    }

    @Override
    public OrderItemResponseDTO toItemResponseDTO(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        OrderItemResponseDTO orderItemResponseDTO = new OrderItemResponseDTO();

        orderItemResponseDTO.setMenuItemId( orderItem.getMenuItemId() );
        orderItemResponseDTO.setItemName( orderItem.getItemName() );
        orderItemResponseDTO.setItemPrice( orderItem.getItemPrice() );
        orderItemResponseDTO.setQuantity( orderItem.getQuantity() );
        orderItemResponseDTO.setSubtotal( orderItem.getSubtotal() );
        orderItemResponseDTO.setSpecialInstructions( orderItem.getSpecialInstructions() );

        return orderItemResponseDTO;
    }

    @Override
    public List<OrderResponseDTO> toResponseDTOList(List<Order> orders) {
        if ( orders == null ) {
            return null;
        }

        List<OrderResponseDTO> list = new ArrayList<OrderResponseDTO>( orders.size() );
        for ( Order order : orders ) {
            list.add( toResponseDTO( order ) );
        }

        return list;
    }

    @Override
    public List<OrderSummaryResponseDTO> toSummaryResponseDTOList(List<Order> orders) {
        if ( orders == null ) {
            return null;
        }

        List<OrderSummaryResponseDTO> list = new ArrayList<OrderSummaryResponseDTO>( orders.size() );
        for ( Order order : orders ) {
            list.add( toSummaryResponseDTO( order ) );
        }

        return list;
    }

    @Override
    public List<OrderItemResponseDTO> toItemResponseDTOList(List<OrderItem> orderItems) {
        if ( orderItems == null ) {
            return null;
        }

        List<OrderItemResponseDTO> list = new ArrayList<OrderItemResponseDTO>( orderItems.size() );
        for ( OrderItem orderItem : orderItems ) {
            list.add( toItemResponseDTO( orderItem ) );
        }

        return list;
    }
}
