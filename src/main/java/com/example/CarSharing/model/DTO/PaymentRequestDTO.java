package com.example.CarSharing.model.DTO;

import com.example.CarSharing.model.enums.PaymentMethodEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDTO {
    private Long transactionId;
    private PaymentMethodEnum type;
}
