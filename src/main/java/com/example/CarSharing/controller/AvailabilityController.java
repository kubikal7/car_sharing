package com.example.CarSharing.controller;

import com.example.CarSharing.model.Cars;
import com.example.CarSharing.model.DetailsOfTransaction;
import com.example.CarSharing.model.enums.DetailsStatusEnum;
import com.example.CarSharing.repository.CarsRepository;
import com.example.CarSharing.repository.DetailsOfTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {

    @Autowired
    private CarsRepository carsRepository;

    @Autowired
    private DetailsOfTransactionRepository detailsRepository;

    //DTO do przekazania dat
    public static class AvailabilityRequest {
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime startDate;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime endDate;

        //Gettery i Settery
        public LocalDateTime getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDateTime startDate) {
            this.startDate = startDate;
        }

        public LocalDateTime getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDateTime endDate) {
            this.endDate = endDate;
        }
    }

    //endpoint sprawdzania dostępności wszystkich samochodów
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableCars(@RequestBody AvailabilityRequest req) {
        LocalDateTime start = req.getStartDate();
        LocalDateTime end = req.getEndDate();

        if (start == null || end == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start and end dates are required.");
        }

        List<Cars> allCars = carsRepository.findAll();
        List<Cars> result = allCars.stream()
                .filter(car -> {
                    List<DetailsOfTransaction> dtList = detailsRepository.findByCarId(car.getId());
                    boolean collision = dtList.stream()
                            .filter(dt -> dt.getStatus() != DetailsStatusEnum.canceled)
                            .anyMatch(dt -> start.isBefore(dt.getEnd_date()) && end.isAfter(dt.getStart_date()));
                    return !collision;
                })
                .toList();

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No available cars for the given period.");
        }

        return ResponseEntity.ok(result);
    }

    //endpoint sprawdzania dostępności konkretnego samochodu w określonym czasie
    @GetMapping("/{id}")
    public ResponseEntity<?> isCarAvailable(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ){
        Optional<Cars> carOpt = carsRepository.findById(id);
        if(carOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car not found");
        }

        Cars car = carOpt.get();

        List<DetailsOfTransaction> dtList = detailsRepository.findByCarId(car.getId());
        boolean isAvailable = dtList.stream()
                .filter(dt -> dt.getStatus() != DetailsStatusEnum.canceled)
                .noneMatch(dt -> startDate.isBefore(dt.getEnd_date()) && endDate.isAfter(dt.getStart_date()));

        return ResponseEntity.ok(isAvailable ? "Car is available" : "Car is not available");
    }
}
