package com.example.CarSharing.controller;

import com.example.CarSharing.model.Users;
import com.example.CarSharing.repository.UsersRepository;
import com.example.CarSharing.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UsersController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(@RequestHeader("Authorization") String token){
        if (!authService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(usersRepository.findAll());
    }

    @GetMapping("/")
    public ResponseEntity<?> getByToken(@RequestHeader("Authorization") String token){
        Optional<Users> userOPT = usersRepository.findByToken(token);
        if(userOPT.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(userOPT.get());
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<?> modifyUser(@RequestHeader("Authorization") String token, @PathVariable long id, @RequestBody Users userBody){
        Optional<Users> userOPTtoken = usersRepository.findByToken(token);
        Optional<Users> userOPT = usersRepository.findById(id);

        if(userOPT.isEmpty() || userOPTtoken.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if(userOPT.get().getId()!=userOPTtoken.get().getId() && !authService.isAdmin(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Users user = userOPT.get();
        user.setName(userBody.getName());
        user.setEmail(userBody.getEmail());
        user.setCountry(userBody.getCountry());
        user.setSurname(userBody.getSurname());
        user.setPassword(passwordEncoder.encode(userBody.getPassword()));
        user.setDate_of_birth(userBody.getDate_of_birth());
        return ResponseEntity.ok(usersRepository.save(user));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token, @PathVariable long id){
        if (!authService.isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Users> userOPT = usersRepository.findById(id);

        if(userOPT.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        usersRepository.deleteById(id);
        return ResponseEntity.ok().body("Deleted "+id);
    }
}
