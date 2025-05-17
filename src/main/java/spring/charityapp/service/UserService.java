package spring.charityapp.service;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import spring.charityapp.dto.TransactionDTO;
import spring.charityapp.dto.UserDTO;
import spring.charityapp.model.User;
import spring.charityapp.repository.UserRepository;
import spring.charityapp.util.Mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final Mapper mapper = new Mapper();
    /**
     * Add a new user with hashed password.
     */

    public User addUser(User user) {
        // Hash password before saving
        // Check if the password is already hashed
        if (!user.getPassword().startsWith("$2a$")) { // BCrypt hashes start with "$2a$"
            String hashedPassword = Bcrypt.hashPassword(user.getPassword());
            user.setPassword(hashedPassword);
        }
        return userRepo.save(user);
    }

    public User getUserByEmail(String email) {
        List<User> users = userRepo.findAll();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        String message = "User with email " + email + " not found";
        throw new IllegalArgumentException(message);
    }
    /**
     * Get all users mapped to UserDTO, including full TransactionDTO.
     */
    public List<UserDTO> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(mapper::mapUserToUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * Find a user by ID mapped to UserDTO, including full TransactionDTO.
     */
    public Optional<UserDTO> getUserById(int id) {
        return userRepo.findById(id).map(mapper::mapUserToUserDTO);
    }

    /**
     * Update an existing user (including rehashing password if changed).
     */
    public boolean updateUser(User updatedUser) {
        Optional<User> existingUserOpt = userRepo.findById(updatedUser.getId());
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            existingUser.setFirstname(updatedUser.getFirstname());
            existingUser.setLastname(updatedUser.getLastname());
            existingUser.setGender(updatedUser.getGender());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setStatus(updatedUser.getStatus());
            existingUser.setProfile(updatedUser.getProfile());

            // Rehash password if it was changed
            if (!updatedUser.getPassword().equals(existingUser.getPassword())) {
                existingUser.setPassword(Bcrypt.hashPassword(updatedUser.getPassword()));
            }

            userRepo.save(existingUser);
            return true;
        }
        return false;
    }

    /**
     * Delete a user by ID.
     */
    public boolean deleteUser(int id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Authenticate user by email and password.
     */
    public Optional<User> authenticate(String email, String password) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (Bcrypt.checkPassword(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Map User entity to UserDTO, including full list of TransactionDTO.
     */


    // update User Password
    public User updateUserPassword(int id, String newPassword) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(newPassword);
        return userRepo.save(user);
    }

}
