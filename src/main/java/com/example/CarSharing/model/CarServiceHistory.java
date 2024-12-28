package com.example.CarSharing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "car_service_history")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarServiceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Cars car;

    private LocalDateTime start_date;

    private LocalDateTime end_date;

    private String problem;
}
