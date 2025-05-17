package spring.charityapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.charityapp.model.SuperAdmin;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<SuperAdmin, Integer> {

    // Find by email (useful for login)
    Optional<SuperAdmin> findByEmail(String email);
}
