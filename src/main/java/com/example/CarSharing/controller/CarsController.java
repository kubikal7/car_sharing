package com.example.CarSharing.controller;

import com.example.CarSharing.model.CarType;
import com.example.CarSharing.model.Cars;
import com.example.CarSharing.model.DTO.CarsDTO;
import com.example.CarSharing.model.enums.CarsStatusEnum;
import com.example.CarSharing.repository.CarTypeRepository;
import com.example.CarSharing.repository.CarsRepository;
import com.example.CarSharing.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cars")
public class CarsController {

    @Autowired
    private CarsRepository carsRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private CarTypeRepository carTypeRepository;

    //endpoint wyświetlania wszystkich samochodów
    @GetMapping("/all")
    public List<Cars> getAllCars(){
        return carsRepository.findAll();
    }

    //endpoint wyświetlania samochodu po ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCarById(@PathVariable String id){
        Optional<Cars> carOpt = carsRepository.findById(id);
        if(carOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car not found");
        }
        return ResponseEntity.ok(carOpt.get());
    }

    //h dodanie nowego samochodu
    @PostMapping("/add")
    public ResponseEntity<?> addCar(
            @RequestHeader("Authorization") String token,
            @RequestBody CarsDTO newCar
    ){
        //czy user adminem
        if(!authService.isAdmin(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only admin can add cars");
        }
        //czy car o danym ID juz nie istnieje
        if(carsRepository.findById(newCar.getId()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Car with this ID already exists");
        }
        //domyślny status na available, jeśli w bazie jest null
        if(newCar.getStatus() == null){
            newCar.setStatus(CarsStatusEnum.available);
        }

        Cars newCarDB = new Cars();
        Optional<CarType> carType = carTypeRepository.findById(newCar.getCar_type_id());
        if(carType.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid car type ID");
        }
        newCarDB.setCarType(carType.get());
        newCarDB.setYear(newCar.getYear());
        newCarDB.setColor(newCar.getColor());
        newCarDB.setStatus(newCar.getStatus());
        newCarDB.setPrice_per_day(newCar.getPrice_per_day());
        newCarDB.setId(newCar.getId());
        carsRepository.save(newCarDB);

        return ResponseEntity.ok("Car added: " + newCar.getId());
    }

    //i aktualizacja danych samochodu (np. status)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCar(
            @PathVariable String id,
            @RequestHeader("Authorization") String token,
            @RequestBody CarsDTO updatedCar
    ){
        if(!authService.isAdmin(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only admin can update cars");
        }

        Optional<Cars> carOPT = carsRepository.findById(id);
        if(carOPT.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car not found");
        }

        Optional<CarType> carType = carTypeRepository.findById(updatedCar.getCar_type_id());
        if(carType.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CarType not found");
        }
        Cars existingCar = carOPT.get();
        //aktualizuj pola (oprócz ID)
        existingCar.setCarType(carType.get()); //jeśli chcesz
        existingCar.setYear(updatedCar.getYear());
        existingCar.setColor(updatedCar.getColor());
        existingCar.setPrice_per_day(updatedCar.getPrice_per_day());
        existingCar.setStatus(updatedCar.getStatus());
        //pominąć parametry które nie aktualizować

        carsRepository.save(existingCar);
        return ResponseEntity.ok("Car updated");
    }

    //endpoint wyświetlania wszystkich dostępnych samochodów
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableCars(){
        List<Cars> availableCars = carsRepository.findAll().stream()
                .filter(car -> car.getStatus() == CarsStatusEnum.available)
                .toList();
        return ResponseEntity.ok(availableCars);
    }
}
