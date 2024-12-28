package com.example.CarSharing.controller;

import com.example.CarSharing.model.Cars;
import com.example.CarSharing.repository.CarsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CarsController {

    @Autowired
    private CarsRepository carsRepository;

    @GetMapping("/wszystkie")
    public List<Cars> getAllCars(){
        return carsRepository.findAll();
    }
}
