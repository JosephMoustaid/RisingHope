package spring.charityapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.charityapp.model.SuperAdmin;
import spring.charityapp.repository.AdminRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepo;

    /**
     * Register a new super admin.
     */
    public SuperAdmin register(Map<String, String> details) {
        SuperAdmin superAdmin = new SuperAdmin();
        superAdmin.register(details);
        return adminRepo.save(superAdmin);
    }

    /**
     * Authenticate admin using email and password.
     */
    public boolean login(String email, String password) {
        Optional<SuperAdmin> admin = adminRepo.findByEmail(email);
        return admin.map(a -> a.login(email, password)).orElse(false);
    }

    /**
     * Reset password for a given admin ID.
     */
    public boolean resetPassword(Integer id, String newPassword) {
        Optional<SuperAdmin> adminOpt = adminRepo.findById(id);
        if (adminOpt.isPresent()) {
            SuperAdmin admin = adminOpt.get();
            admin.resetPassword(newPassword);
            adminRepo.save(admin);
            return true;
        }
        return false;
    }

    /**
     * Get all super admins.
     */
    public List<SuperAdmin> getAllAdmins() {
        return adminRepo.findAll();
    }

    /**
     * Get a specific admin by ID.
     */
    public Optional<SuperAdmin> getById(Integer id) {
        return adminRepo.findById(id);
    }

    /**
     * Delete a super admin.
     */
    public boolean deleteAdmin(Integer id) {
        if (adminRepo.existsById(id)) {
            adminRepo.deleteById(id);
            return true;
        }
        return false;
    }
}
