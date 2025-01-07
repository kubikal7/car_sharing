package com.example.CarSharing.repository;

import com.example.CarSharing.model.DetailsOfTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailsOfTransactionRepository extends JpaRepository<DetailsOfTransaction, Long> {

    //pobieranie transakcji po car_id by sprawdzać kolizje terminów w danym aucie
    List<DetailsOfTransaction> findByCarId(String carId);

    //pobieranie transakcji po user_id
    List<DetailsOfTransaction> findByUserId(Long userId);
}
