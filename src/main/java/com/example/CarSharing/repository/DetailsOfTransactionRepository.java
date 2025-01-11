package com.example.CarSharing.repository;

import com.example.CarSharing.Interfaces.TransactionDateProjection;
import com.example.CarSharing.model.DetailsOfTransaction;
import com.example.CarSharing.model.enums.CarsStatusEnum;
import com.example.CarSharing.model.enums.DetailsStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DetailsOfTransactionRepository extends JpaRepository<DetailsOfTransaction, Long> {

    //pobieranie transakcji po car_id by sprawdzać kolizje terminów w danym aucie
    List<DetailsOfTransaction> findByCarId(String carId);

    //pobieranie transakcji po user_id
    List<DetailsOfTransaction> findByUserId(Long userId);

    List<TransactionDateProjection> findByCarIdAndStartDateAfterAndStatusNot(String carId, LocalDateTime startDate, DetailsStatusEnum status);
}
