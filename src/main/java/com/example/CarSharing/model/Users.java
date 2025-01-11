package com.example.CarSharing.model;

import com.example.CarSharing.model.enums.UsersRoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotBlank(message = "Surname cannot be empty")
    private String surname;
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password cannot be empty")
    private String password;
    @JsonIgnore
    private String token;
    @NotNull(message = "Date of birth cannot be null")
    private LocalDate date_of_birth;
    @NotBlank(message = "Country cannot be empty")
    private String country;

    @Enumerated(EnumType.STRING)
    private UsersRoleEnum role;
}
