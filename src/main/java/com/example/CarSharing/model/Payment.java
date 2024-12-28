package com.example.CarSharing.model;

import com.example.CarSharing.model.enums.PaymentMethodEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "details_of_transaction_id")
    private DetailsOfTransaction detailsOfTransaction;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum type;
}
