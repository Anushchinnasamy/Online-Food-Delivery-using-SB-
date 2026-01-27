package com.fooddelivery.payment.mapper;

import com.fooddelivery.payment.dto.request.PaymentInitiateRequestDTO;
import com.fooddelivery.payment.dto.response.PaymentResponseDTO;
import com.fooddelivery.payment.dto.response.PaymentSummaryResponseDTO;
import com.fooddelivery.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for Payment entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {RefundMapper.class}
)
public interface PaymentMapper {

    /**
     * Convert PaymentInitiateRequestDTO to Payment entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "gatewayTransactionId", ignore = true)
    @Mapping(target = "gatewayName", ignore = true)
    @Mapping(target = "gatewayResponse", ignore = true)
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    @Mapping(target = "refundedAmount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "refunds", ignore = true)
    Payment toEntity(PaymentInitiateRequestDTO dto);

    /**
     * Convert Payment entity to PaymentResponseDTO
     */
    PaymentResponseDTO toResponseDTO(Payment payment);

    /**
     * Convert Payment entity to PaymentSummaryResponseDTO
     */
    @Mapping(source = "refunds", target = "refunds")
    PaymentSummaryResponseDTO toSummaryResponseDTO(Payment payment);

    /**
     * Convert list of Payment entities to list of PaymentResponseDTOs
     */
    List<PaymentResponseDTO> toResponseDTOList(List<Payment> payments);

    /**
     * Convert list of Payment entities to list of PaymentSummaryResponseDTOs
     */
    List<PaymentSummaryResponseDTO> toSummaryResponseDTOList(List<Payment> payments);

    /**
     * Update Payment entity from PaymentInitiateRequestDTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "gatewayTransactionId", ignore = true)
    @Mapping(target = "gatewayName", ignore = true)
    @Mapping(target = "gatewayResponse", ignore = true)
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    @Mapping(target = "refundedAmount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "refunds", ignore = true)
    void updateEntityFromDTO(PaymentInitiateRequestDTO dto, @MappingTarget Payment payment);
}