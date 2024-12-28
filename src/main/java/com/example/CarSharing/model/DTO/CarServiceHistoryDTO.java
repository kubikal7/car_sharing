package com.example.CarSharing.model.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CarServiceHistoryDTO {
    private String carId; // ID samochodu
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String problem;
}
