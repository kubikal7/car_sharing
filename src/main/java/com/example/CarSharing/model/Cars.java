package com.example.CarSharing.model;

import com.example.CarSharing.model.enums.CarsStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cars")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cars {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "car_type_id")
    private CarType carType;
    private int year;
    private String color;
    private double price_per_day;

    @Enumerated(EnumType.STRING)
    private CarsStatusEnum status;
}
