package spring.charityapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.charityapp.model.SuperAdmin;

import java.util.Optional;

@Repository
public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Integer> {

    // Find SuperAdmin by email (for login)
    Optional<SuperAdmin> findByEmail(String email);

    // Check if a SuperAdmin with given email exists (for registration validation)
    boolean existsByEmail(String email);
}
