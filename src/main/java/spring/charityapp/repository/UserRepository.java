package spring.charityapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.charityapp.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Basic CRUD operations are inherited from JpaRepository

    // 1. Find by email (exact match)
    Optional<User> findByEmail(String email);

    // 2. Find by name components
    List<User> findByLastname(String lastname);
    List<User> findByFirstnameAndLastname(String firstname, String lastname);

    // 3. Find top donors
    @Query("SELECT u FROM User u JOIN u.transactions d GROUP BY u ORDER BY SUM(d.amount) DESC LIMIT 10")
    List<User> findTopDonors();

    // 4. Find users with donations above a certain amount
    @Query("SELECT DISTINCT u FROM User u JOIN u.transactions d WHERE d.amount > :minAmount")
    List<User> findUsersWithDonationsAbove(@Param("minAmount") double minAmount);

    // 5. Custom update operation
    @Modifying
    @Query("UPDATE User u SET u.firstname = :firstname, u.lastname = :lastname WHERE u.id = :id")
    int updateUserDetails(@Param("id") UUID id,
                          @Param("firstname") String firstname,
                          @Param("lastname") String lastname);


    // 7. Search by name pattern
    List<User> findByFirstnameContainingOrLastnameContaining(String firstnamePattern, String lastnamePattern);

    // 8. Check if email exists (for registration validation)
    boolean existsByEmail(String email);
}
