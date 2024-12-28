package com.example.CarSharing.controller;

import com.example.CarSharing.model.CarType;
import com.example.CarSharing.repository.CarTypeRepository;
import com.example.CarSharing.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cartype")
public class CarTypeController {

    @Autowired
    private CarTypeRepository carTypeRepository;
    @Autowired
    private AuthService authService;

    @PostMapping("/add")
    public ResponseEntity<?> addCarType(@RequestBody CarType newCarType, @RequestHeader("Authorization") String authorizationToken){
        if(!authService.isAdmin(authorizationToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<CarType> existingCarType = carTypeRepository.findByBrandAndModelAndNrOfSeats(newCarType.getBrand(), newCarType.getModel(), newCarType.getNrOfSeats());
        if(existingCarType.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Car type already exists");
        }

        return ResponseEntity.ok(carTypeRepository.save(newCarType));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCarTypes(){
        return ResponseEntity.ok(carTypeRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCarType(@PathVariable Long id){
        return ResponseEntity.ok(carTypeRepository.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCarType(@PathVariable Long id, @RequestBody CarType updatedCarType, @RequestHeader("Authorization") String authorizationToken) {
        if (!authService.isAdmin(authorizationToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<CarType> existingCarTypeOptional = carTypeRepository.findById(id);
        if (existingCarTypeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car type not found");
        }

        CarType existingCarType = existingCarTypeOptional.get();
        existingCarType.setBrand(updatedCarType.getBrand());
        existingCarType.setModel(updatedCarType.getModel());
        existingCarType.setNrOfSeats(updatedCarType.getNrOfSeats());

        carTypeRepository.save(existingCarType);
        return ResponseEntity.ok(existingCarType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCarType(@PathVariable Long id, @RequestHeader("Authorization") String authorizationToken){
        if (!authService.isAdmin(authorizationToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<CarType> existingCarTypeOptional = carTypeRepository.findById(id);
        if (existingCarTypeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car type not found");
        }

        carTypeRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}
