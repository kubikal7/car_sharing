package com.example.CarSharing.repository;

import com.example.CarSharing.model.CarServiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarServiceHistoryRepository extends JpaRepository<CarServiceHistory, Long> {
    List<CarServiceHistory> findByCarId(String carId); //pobieranie historii serwisowej konkretnego samochodu
}
