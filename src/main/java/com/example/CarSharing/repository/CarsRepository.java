package com.example.CarSharing.repository;

import com.example.CarSharing.model.Cars;
import com.example.CarSharing.model.enums.CarsStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarsRepository extends JpaRepository<Cars,String> {
    List<Cars> findByStatusNot(CarsStatusEnum status);
}
