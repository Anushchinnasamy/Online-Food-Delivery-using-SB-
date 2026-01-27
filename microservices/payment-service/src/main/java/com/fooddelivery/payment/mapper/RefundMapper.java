package com.fooddelivery.payment.mapper;

import com.fooddelivery.payment.dto.request.RefundRequestDTO;
import com.fooddelivery.payment.dto.response.RefundResponseDTO;
import com.fooddelivery.payment.entity.Refund;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for Refund entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RefundMapper {

    /**
     * Convert RefundRequestDTO to Refund entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "refundTransactionId", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "refundStatus", ignore = true)
    @Mapping(target = "gatewayRefundId", ignore = true)
    @Mapping(target = "gatewayResponse", ignore = true)
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "paymentTransactionId", target = "payment.transactionId")
    Refund toEntity(RefundRequestDTO dto);

    /**
     * Convert Refund entity to RefundResponseDTO
     */
    @Mapping(source = "payment.transactionId", target = "paymentTransactionId")
    RefundResponseDTO toResponseDTO(Refund refund);

    /**
     * Convert list of Refund entities to list of RefundResponseDTOs
     */
    List<RefundResponseDTO> toResponseDTOList(List<Refund> refunds);

    /**
     * Update Refund entity from RefundRequestDTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "refundTransactionId", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "refundStatus", ignore = true)
    @Mapping(target = "gatewayRefundId", ignore = true)
    @Mapping(target = "gatewayResponse", ignore = true)
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(RefundRequestDTO dto, @MappingTarget Refund refund);
}