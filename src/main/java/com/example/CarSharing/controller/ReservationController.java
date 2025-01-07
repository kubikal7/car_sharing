package com.example.CarSharing.controller;

import com.example.CarSharing.model.DTO.ReserveRequest;
import com.example.CarSharing.model.DTO.UpdateRequest;
import com.example.CarSharing.model.Cars;
import com.example.CarSharing.model.DetailsOfTransaction;
import com.example.CarSharing.model.Users;
import com.example.CarSharing.model.enums.CarsStatusEnum;
import com.example.CarSharing.model.enums.DetailsStatusEnum;
import com.example.CarSharing.repository.CarsRepository;
import com.example.CarSharing.repository.DetailsOfTransactionRepository;
import com.example.CarSharing.repository.UsersRepository;
import com.example.CarSharing.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private DetailsOfTransactionRepository detailsRepository;

    @Autowired
    private CarsRepository carsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AuthService authService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(@RequestHeader("Authorization") String token){
        if (!authService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(detailsRepository.findAll());
    }

    //pobranie rezerwacji dla konkretnego użytkownika
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReservationsByUser(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId
    ){
        Optional<Users> requestingUser = usersRepository.findByToken(token);
        if(requestingUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Users user = requestingUser.get();
        if(user.getId() != userId && !authService.isAdmin(token)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No permission to view these reservations");
        }

        List<DetailsOfTransaction> reservations = detailsRepository.findByUserId(userId);
        return ResponseEntity.ok(reservations);
    }

    //rezerwacja
    @PostMapping("/reserve")
    public ResponseEntity<?> reserveCar(
            @RequestHeader("Authorization") String token,
            @RequestBody ReserveRequest req
    ) {
        var userOpt = usersRepository.findByToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        var user = userOpt.get();
        var carOpt = carsRepository.findById(req.getCarId());

        if (carOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car not found");
        }

        var car = carOpt.get();

        //czy daty nie są z przeszłości
        if (req.getStartDate().isBefore(LocalDateTime.now()) || req.getEndDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Reservation dates cannot be in the past");
        }

        //dostępnośc samochodu w podanym okresie
        List<DetailsOfTransaction> existingReservations = detailsRepository.findByCarId(car.getId());
        boolean isAvailable = existingReservations.stream()
                .filter(dt -> dt.getStatus() != DetailsStatusEnum.canceled)
                .noneMatch(dt ->
                        req.getStartDate().isBefore(dt.getEnd_date()) && req.getEndDate().isAfter(dt.getStart_date())
                );

        if (!isAvailable) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Car is not available in the selected period");
        }

        DetailsOfTransaction dt = new DetailsOfTransaction();
        dt.setCar(car);
        dt.setUser(user);
        dt.setStart_date(req.getStartDate());
        dt.setEnd_date(req.getEndDate());
        dt.setStatus(DetailsStatusEnum.during);

        long days = ChronoUnit.DAYS.between(req.getStartDate(), req.getEndDate());
        if (days < 1) days = 1;

        dt.setPrice(days * car.getPrice_per_day());
        detailsRepository.save(dt);

        car.setStatus(CarsStatusEnum.rent);
        carsRepository.save(car);

        return ResponseEntity.ok("Reserved transactionId=" + dt.getId());
    }


    //modyfikacja rezerwacji
    @PutMapping("/modify")
    public ResponseEntity<?> modifyReservation(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateRequest req
    ) {
        var userOpt = usersRepository.findByToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        var user = userOpt.get();
        var dtOpt = detailsRepository.findById(req.getTransactionId());

        if (dtOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
        }

        var dt = dtOpt.get();
        if (dt.getUser().getId() != user.getId() && !authService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not your reservation");
        }

        //czy nowe daty nie są w przeszłości
        if (req.getNewStart().isBefore(LocalDateTime.now()) || req.getNewEnd().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Reservation dates cannot be in the past");
        }

        //czy nowy okres ma tyle samo dni co poprzedni
        long originalDays = ChronoUnit.DAYS.between(dt.getStart_date(), dt.getEnd_date());
        long newDays = ChronoUnit.DAYS.between(req.getNewStart(), req.getNewEnd());
        if (originalDays != newDays) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("New reservation period must have the same number of days as the original");
        }

        if(newDays<1)
            newDays=1;


        //czy data startu nie jest późniejsza niż data zakończenia
        if (req.getNewStart().isAfter(req.getNewEnd())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date cannot be after end date");
        }

        //dostępnośc samochodu w nowym okresie
        List<DetailsOfTransaction> existingReservations = detailsRepository.findByCarId(dt.getCar().getId());
        boolean isAvailable = existingReservations.stream()
                .filter(existingDt -> !existingDt.getId().equals(dt.getId()) && existingDt.getStatus() != DetailsStatusEnum.canceled)
                .noneMatch(existingDt ->
                        req.getNewStart().isBefore(existingDt.getEnd_date()) && req.getNewEnd().isAfter(existingDt.getStart_date())
                );

        if (!isAvailable) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Car is not available in the new selected period");
        }

        dt.setStart_date(req.getNewStart());
        dt.setEnd_date(req.getNewEnd());
        dt.setPrice(dt.getCar().getPrice_per_day() * newDays);

        detailsRepository.save(dt);

        return ResponseEntity.ok("Reservation updated. New price= " + dt.getPrice());
    }


    //anulowanie rezerwacji
    @PutMapping("/cancel/{id}") //@PathVariable zamiast cancelrequest
    public ResponseEntity<?> cancelReservation(
            @RequestHeader("Authorization") String token,
            @PathVariable long id
    ) {
        var userOpt = usersRepository.findByToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        var user = userOpt.get();
        var dtOpt = detailsRepository.findById(id);

        if (dtOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
        }

        var dt = dtOpt.get();
        if (dt.getUser().getId() != user.getId() && !authService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No permission to cancel");
        }

        dt.setStatus(DetailsStatusEnum.canceled);
        detailsRepository.save(dt);

        //zmiana statusu samochodu na 'available' po anulowaniu rezerwacji jeśli nie ma innych rezerwacji
        List<DetailsOfTransaction> activeReservations = detailsRepository.findByCarId(dt.getCar().getId()).stream()
                .filter(d -> d.getStatus() != DetailsStatusEnum.canceled)
                .toList();

        if(activeReservations.isEmpty()){
            Cars car = dt.getCar();
            car.setStatus(CarsStatusEnum.available);
            carsRepository.save(car);
        }

        return ResponseEntity.ok("Reservation canceled");
    }
}
