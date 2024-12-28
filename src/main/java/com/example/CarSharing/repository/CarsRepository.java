package com.example.CarSharing.repository;

import com.example.CarSharing.model.Cars;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarsRepository extends JpaRepository<Cars,String> {
}
