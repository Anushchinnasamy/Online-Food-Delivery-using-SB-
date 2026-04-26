package com.fooddelivery.order.mapper;

import com.fooddelivery.order.dto.response.OrderItemResponseDTO;
import com.fooddelivery.order.dto.response.OrderResponseDTO;
import com.fooddelivery.order.dto.response.OrderSummaryResponseDTO;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderItem;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Order entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {

    /**
     * Convert Order entity to OrderResponseDTO
     */
    @Mapping(target = "items", source = "orderItems")
    OrderResponseDTO toResponseDTO(Order order);

    /**
     * Convert Order entity to OrderSummaryResponseDTO
     */
    @Mapping(target = "itemCount", expression = "java(order.getTotalItemCount())")
    OrderSummaryResponseDTO toSummaryResponseDTO(Order order);

    /**
     * Convert OrderItem entity to OrderItemResponseDTO
     */
    OrderItemResponseDTO toItemResponseDTO(OrderItem orderItem);

    /**
     * Convert list of Order entities to OrderResponseDTO list
     */
    List<OrderResponseDTO> toResponseDTOList(List<Order> orders);

    /**
     * Convert list of Order entities to OrderSummaryResponseDTO list
     */
    List<OrderSummaryResponseDTO> toSummaryResponseDTOList(List<Order> orders);

    /**
     * Convert list of OrderItem entities to OrderItemResponseDTO list
     */
    List<OrderItemResponseDTO> toItemResponseDTOList(List<OrderItem> orderItems);
}