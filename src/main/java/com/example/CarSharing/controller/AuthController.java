package com.example.CarSharing.controller;

import com.example.CarSharing.model.Users;
import com.example.CarSharing.model.enums.UsersRoleEnum;
import com.example.CarSharing.repository.UsersRepository;
import com.example.CarSharing.services.AuthService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AuthService authService;

    @Getter
    @Setter
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @PutMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Users> userOPT = usersRepository.findByEmail(loginRequest.getEmail());

        //poprawność hasła (porównanie zahashowanego hasła)
        if (userOPT.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), userOPT.get().getPassword())) {
            Users user = userOPT.get();
            String token = generateUniqueToken();

            //token do bazy danych
            user.setToken(token);
            usersRepository.save(user);

            return ResponseEntity.ok(token);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private String generateUniqueToken() {
        String token;
        do {
            token = UUID.randomUUID().toString();  //generowanie nowego tokena
        } while (usersRepository.existsByToken(token));  //czy token już istnieje w bazie
        return token;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users newUser, @RequestHeader("Authorization") String authorizationToken) {
        if(newUser.getRole() != UsersRoleEnum.admin && newUser.getRole() != UsersRoleEnum.user) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");
        }

        if(newUser.getRole() == UsersRoleEnum.admin){
            if(!authService.isAdmin(authorizationToken)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }


        //czy użytkownik o takim e-mailu już istnieje
        if (usersRepository.findByEmail(newUser.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        }

        int age = (int) ChronoUnit.YEARS.between(newUser.getDate_of_birth(), LocalDate.now());
        if (age < 18) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User must be at least 18 years old");
        }

        System.out.println(newUser);
        //haszowanie hasła
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        usersRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }
}
