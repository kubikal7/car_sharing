package com.example.CarSharing.model;

import com.example.CarSharing.model.enums.DetailsStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "details_of_transaction")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DetailsOfTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime start_date;

    private LocalDateTime end_date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Cars car;

    private double price;

    @Enumerated(EnumType.STRING)
    private DetailsStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
}
