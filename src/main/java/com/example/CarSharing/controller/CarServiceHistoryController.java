package com.example.CarSharing.controller;

import com.example.CarSharing.model.CarServiceHistory;
import com.example.CarSharing.model.Cars;
import com.example.CarSharing.model.DTO.CarServiceHistoryDTO;
import com.example.CarSharing.model.Users;
import com.example.CarSharing.model.enums.CarsStatusEnum;
import com.example.CarSharing.repository.CarServiceHistoryRepository;
import com.example.CarSharing.repository.CarsRepository;
import com.example.CarSharing.repository.UsersRepository; // DODANO
import com.example.CarSharing.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/service-history")
public class CarServiceHistoryController {

    @Autowired
    private CarServiceHistoryRepository carServiceHistoryRepository;
    @Autowired
    private CarsRepository carsRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private AuthService authService;

    //endpoint pobierania całej historii serwisowej (admin tylko)
    @GetMapping("/all")
    public ResponseEntity<?> getAllServiceHistory(@RequestHeader("Authorization") String token){
        if (!authService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(carServiceHistoryRepository.findAll());
    }

    //endpoint do pobierania historii serwisowej dla konkretnego samochodu
    @GetMapping("/car/{carId}")
    public ResponseEntity<?> getServiceHistoryByCarId(
            @RequestHeader("Authorization") String token,
            @PathVariable String carId
    ){
        Optional<Users> userOpt = usersRepository.findByToken(token);
        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Users user = userOpt.get();

        List<CarServiceHistory> history = carServiceHistoryRepository.findByCarId(carId);
        return ResponseEntity.ok(history);
    }

    //modyfikacja historii serwisowej po ID
    @PutMapping("/modify/{id}")
    public ResponseEntity<?> modifyServiceHistory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody CarServiceHistoryDTO updatedHistoryDTO
    ){
        if (!authService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only admin can modify service history");
        }

        Optional<CarServiceHistory> historyOpt = carServiceHistoryRepository.findById(id);
        if(historyOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service history not found");
        }

        CarServiceHistory history = historyOpt.get();
        history.setStart_date(updatedHistoryDTO.getStartDate());
        history.setEnd_date(updatedHistoryDTO.getEndDate());
        history.setProblem(updatedHistoryDTO.getProblem());

        carServiceHistoryRepository.save(history);
        return ResponseEntity.ok("Service history updated");
    }

    //istniejący endpoint do dodawania historii serwisowej
    @PostMapping("/add")
    public ResponseEntity<?> addHistory(
            @RequestHeader("Authorization") String token,
            @RequestBody CarServiceHistoryDTO carServiceHistoryDTO
    ) {
        //czy user jest adminem
        if (!authService.isAdmin(token)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Only admin can register service");
        }

        //szukamy auta w bazie
        Cars car = carsRepository.findById(carServiceHistoryDTO.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        //nowy wpis w tabeli car_service_history
        CarServiceHistory carServiceHistory = new CarServiceHistory();
        carServiceHistory.setCar(car);
        carServiceHistory.setEnd_date(carServiceHistoryDTO.getEndDate());
        carServiceHistory.setStart_date(carServiceHistoryDTO.getStartDate());
        carServiceHistory.setProblem(carServiceHistoryDTO.getProblem());

        carServiceHistoryRepository.save(carServiceHistory);

        //aktualizuj status auta (np. na 'not')
        car.setStatus(CarsStatusEnum.not);
        carsRepository.save(car);

        return ResponseEntity.ok("Service history added, car status updated to 'not'");
    }
}
