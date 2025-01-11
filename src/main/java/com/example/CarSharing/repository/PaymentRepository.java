package com.example.CarSharing.repository;

import com.example.CarSharing.model.Payment;
import com.example.CarSharing.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUser(Users user);
}
