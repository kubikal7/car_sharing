package com.example.CarSharing.model.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReserveRequest {
    private String carId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
