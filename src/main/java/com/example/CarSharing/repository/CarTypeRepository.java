package com.example.CarSharing.repository;

import com.example.CarSharing.model.CarType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarTypeRepository extends JpaRepository<CarType, Long> {
    Optional<CarType> findByBrandAndModelAndNrOfSeats(String brand, String model, int nrOfSeats);
}
