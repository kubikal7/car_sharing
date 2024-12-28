package com.example.CarSharing.repository;

import com.example.CarSharing.model.CarServiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarServiceHistoryRepository extends JpaRepository<CarServiceHistory,Long> {
}
