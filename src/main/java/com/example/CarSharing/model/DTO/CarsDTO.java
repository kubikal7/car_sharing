package com.example.CarSharing.model.DTO;

import com.example.CarSharing.model.enums.CarsStatusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarsDTO {
    private String id;
    private long car_type_id;
    private int year;
    private String color;
    private double price_per_day;
    private CarsStatusEnum status;
}
