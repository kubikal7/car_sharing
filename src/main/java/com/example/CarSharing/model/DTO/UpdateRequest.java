package com.example.CarSharing.model.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateRequest {
    private LocalDateTime newStart;
    private LocalDateTime newEnd;
}
