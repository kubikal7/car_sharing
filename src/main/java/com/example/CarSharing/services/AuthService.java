package com.example.CarSharing.services;

import com.example.CarSharing.model.Users;
import com.example.CarSharing.model.enums.UsersRoleEnum;
import com.example.CarSharing.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepository;

    public boolean isAdmin(String authorizationToken){
        Optional<Users> user = usersRepository.findByToken(authorizationToken);
        return user.isPresent() && user.get().getRole() == UsersRoleEnum.admin;
    }
}
