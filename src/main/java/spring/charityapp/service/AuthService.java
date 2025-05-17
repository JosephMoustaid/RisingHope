package spring.charityapp.service;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;
import spring.charityapp.common.Role;
import spring.charityapp.exceptions.BadCredentialsException;

import spring.charityapp.model.*;

import spring.charityapp.repository.AdminRepository;
import spring.charityapp.repository.OrganizationRepository;
import spring.charityapp.repository.UserRepository;


import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final AdminRepository adminRepository;

    public AuthService(UserRepository userRepository, OrganizationRepository organizationRepository,
                       AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.adminRepository = adminRepository;
    }


    // for User
    public User authentificateUser(String email, String password) {
        String message = "User with email " + email + " not found";
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, message)
        );
        if (Bcrypt.checkPassword(password, user.getPassword())) {
            return user;
        } else {
            String message2 = "Invalid password " + password;
            throw new BadCredentialsException(message2);
        }
    }

    // for Organization
    public Organization authentificateOrganization(String email, String password) {
        Organization org = organizationRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Orgnization not found")
        );
        if (Bcrypt.checkPassword(password, org.getPassword())) {
            return org;
        } else {
            String message = "Invalid password " + password;
            throw new BadCredentialsException(message);
        }
    }

    // for SuperAdmin
    public SuperAdmin authentificateAdmin(String email, String password) {
        SuperAdmin admin = adminRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found")
        );
        if (Bcrypt.checkPassword(password, admin.getPassword())) {
            return admin;
        } else {
            throw new BadCredentialsException("Invalid password");
        }
    }
}