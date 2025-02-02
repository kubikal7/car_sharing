package com.example.CarSharing.controller;

import com.example.CarSharing.model.DTO.PaymentRequestDTO;
import com.example.CarSharing.model.DetailsOfTransaction;
import com.example.CarSharing.model.Payment;
import com.example.CarSharing.model.Users;
import com.example.CarSharing.model.enums.PaymentMethodEnum;
import com.example.CarSharing.repository.DetailsOfTransactionRepository;
import com.example.CarSharing.repository.PaymentRepository;
import com.example.CarSharing.repository.UsersRepository;
import com.example.CarSharing.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private DetailsOfTransactionRepository detailsRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AuthService authService;

    //endpoint wyświetlania wszystkich płatności (admin tylko)
    @GetMapping("/all")
    public ResponseEntity<?> getAll(@RequestHeader("Authorization") String token){
        if (!authService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(paymentRepository.findAll());
    }

    //endpoint wyświetlania płatności po ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id){
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        if(paymentOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found");
        }
        return ResponseEntity.ok(paymentOpt.get());
    }

    @GetMapping("/user/{userID}")
    public ResponseEntity<?> getUserPayments(@RequestHeader("Authorization") String token, @PathVariable long userID){
        Optional<Users> userOPTtoken = usersRepository.findByToken(token);
        Optional<Users> userOPT = usersRepository.findById(userID);

        if(userOPT.isEmpty() || userOPTtoken.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if(userOPT.get().getId()!=userOPTtoken.get().getId() && !authService.isAdmin(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Payment> payments = paymentRepository.findByUser(userOPT.get());
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserPaymentsByToken(@RequestHeader("Authorization") String token){
        Optional<Users> userOPTtoken = usersRepository.findByToken(token);
        if(userOPTtoken.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<Payment> payments = paymentRepository.findByUser(userOPTtoken.get());
        return ResponseEntity.ok(payments);
    }

    //endpoint dokonywania płatności
    // PaymentController.java

    @PostMapping("/pay/{transactionID}")
    public ResponseEntity<?> payForTransaction(
            @RequestHeader("Authorization") String token,
            @RequestBody PaymentRequestDTO req,
            @PathVariable long transactionID
    ) {
        var userOpt = usersRepository.findByToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        var user = userOpt.get();
        var dtOpt = detailsRepository.findById(transactionID);

        if (dtOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
        }

        var dt = dtOpt.get();
        if (dt.getUser().getId() != user.getId() && !authService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No permission to pay for this transaction");
        }

        //czy transakcja już została opłacona
        if (dt.getPayment() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Transaction already paid");
        }

        Payment pay = new Payment();
        pay.setUser(user);
        pay.setDate(LocalDateTime.now());
        pay.setType(req.getType());
        pay.setDetailsOfTransaction(dt);
        paymentRepository.save(pay);

        dt.setPayment(pay);
        detailsRepository.save(dt);

        return ResponseEntity.ok("Payment done. PaymentId=" + pay.getId());
    }
}
