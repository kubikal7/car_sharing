package com.example.CarSharing.model;

import com.example.CarSharing.model.enums.UsersRoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String token;
    private LocalDate date_of_birth;
    private String country;

    @Enumerated(EnumType.STRING)
    private UsersRoleEnum role;
}
