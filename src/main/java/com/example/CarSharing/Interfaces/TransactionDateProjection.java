package com.example.CarSharing.Interfaces;

import java.time.LocalDateTime;

public interface TransactionDateProjection {
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
}
