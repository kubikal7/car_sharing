package com.example.CarSharing.controller;

import com.example.CarSharing.model.CarServiceHistory;
import com.example.CarSharing.model.Cars;
import com.example.CarSharing.model.DTO.CarServiceHistoryDTO;
import com.example.CarSharing.repository.CarServiceHistoryRepository;
import com.example.CarSharing.repository.CarsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarServiceHistoryController {

    @Autowired
    private CarServiceHistoryRepository carServiceHistoryRepository;
    @Autowired
    private CarsRepository carsRepository;

    @PostMapping("/service")
    public CarServiceHistory addHistory(@RequestBody CarServiceHistoryDTO carServiceHistoryDTO){
        Cars car = carsRepository.findById(carServiceHistoryDTO.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        CarServiceHistory carServiceHistory = new CarServiceHistory();
        carServiceHistory.setCar(car);
        carServiceHistory.setEnd_date(carServiceHistoryDTO.getEndDate());
        carServiceHistory.setStart_date(carServiceHistoryDTO.getStartDate());
        carServiceHistory.setProblem(carServiceHistoryDTO.getProblem());
        return carServiceHistoryRepository.save(carServiceHistory);
    }
}
