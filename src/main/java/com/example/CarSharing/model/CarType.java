package com.example.CarSharing.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "car_type")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brand;
    private String model;
    @Column(name="nr_of_seats")
    private int nrOfSeats;

}
