package spring.charityapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.charityapp.model.Organization;
import spring.charityapp.common.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {

    // Find by email (for login/auth)
    Optional<Organization> findByEmail(String email);

    // Check if an email already exists (for registration validation)
    boolean existsByEmail(String email);


    List<Organization> findTop6ByOrderByIdAsc();

    // Find all organizations by validation status
    List<Organization> findByValidated(Boolean validated);

    // Find by status (PENDING, APPROVED, REJECTED)
    List<Organization> findByStatus(Status status);

    // Search by name containing (case-insensitive)
    List<Organization> findByNameContainingIgnoreCase(String name);

    // For admin or super-admin to approve/review unapproved organizations
    List<Organization> findByValidatedFalseAndStatus(Status status);
}
