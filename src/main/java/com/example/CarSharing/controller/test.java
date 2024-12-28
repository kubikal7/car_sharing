package com.example.CarSharing.controller;

import com.example.CarSharing.model.CarType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

public class test {

    public void testowy(){
        CarType carType = new CarType();
        carType.setModel("lol");
    }

}
