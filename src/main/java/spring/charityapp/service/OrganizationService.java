package spring.charityapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.ssl.SslProperties;
import spring.charityapp.util.Mapper;
import org.springframework.stereotype.Service;
import spring.charityapp.common.Status;
import spring.charityapp.dto.OrganizationDTO;
import spring.charityapp.model.Organization;
import spring.charityapp.repository.OrganizationRepository;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepo;
    private final Mapper mapper = new Mapper();

    private final CharityActionService charityActionService;

    /**
     * Register a new organization.
     */
    public boolean register(Organization organization) {
        if (organizationRepo.existsByEmail(organization.getEmail())) {
            return false; // Email already exists
        }
        organization.setValidated(false);
        organization.setStatus(Status.PENDING);
        organizationRepo.save(organization);
        return true;
    }

    /**
     * Authenticate an organization by email (and you can add password check if needed).
     */
    public Optional<OrganizationDTO> getByEmail(String email) {
        return organizationRepo.findByEmail(email).map(org -> {
            OrganizationDTO orgDTO = mapper.mapOrganizationToOrganiationDTO(org);
            return orgDTO;
        });
    }

    /**
     * Get organization by ID.
     */
    public Optional<OrganizationDTO> getById(Integer id) {
        return organizationRepo.findById(id).map(
                org -> {
                    OrganizationDTO orgDTO = mapper.mapOrganizationToOrganiationDTO(org);
                    return orgDTO;
                }
        );
    }

    public Optional<Organization> getOrganiozationByEmail(String email) {
        return organizationRepo.findByEmail(email);
    }

    /**
     * Get all organizations.
     */
    public List<OrganizationDTO> getAll() {
        return organizationRepo.findAll()
                .stream()
                .map(org -> {
                    OrganizationDTO orgDTO = mapper.mapOrganizationToOrganiationDTO(org);
                    return orgDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get the latest 6 organizations.
     */
    public List<OrganizationDTO> findTop6() {
        return organizationRepo.findTop6ByOrderByIdAsc()
                .stream()
                .map(org -> {
                    OrganizationDTO orgDTO = mapper.mapOrganizationToOrganiationDTO(org);
                    return orgDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * Check if an email is already used.
     */
    public boolean emailExists(String email) {
        return organizationRepo.existsByEmail(email);
    }

    /**
     * Get organizations by validation status.
     */
    public List<OrganizationDTO> getByValidation(boolean validated) {
        return organizationRepo.findByValidated(validated)
                .stream()
                .map(organization -> mapper.mapOrganizationToOrganiationDTO(organization))
                .collect(Collectors.toList());
    }


    /**
     * Get organizations by status.
     */
    public List<OrganizationDTO> getByStatus(Status status) {
        return organizationRepo.findByStatus(status)
                .stream()
                .map(org -> {
                    OrganizationDTO orgDTO = mapper.mapOrganizationToOrganiationDTO(org);
                    return orgDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * Search organizations by name (case-insensitive).
     */
    public List<OrganizationDTO> searchByName(String keyword) {
        return organizationRepo.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(organization -> mapper.mapOrganizationToOrganiationDTO(organization))
                .collect(Collectors.toList());
    }

    /**
     * Get pending unvalidated organizations (for admin review).
     */
    public List<OrganizationDTO> getPendingUnvalidated(Status status) {
        return organizationRepo.findByValidatedFalseAndStatus(status)
                .stream()
                .map(organization -> mapper.mapOrganizationToOrganiationDTO(organization))
                .collect(Collectors.toList());
    }

    /**
     * Update organization's validation and status (used by admin/superadmin).
     */
    public boolean validateAndApprove(Integer id, boolean validated, Status newStatus) {
        Optional<Organization> optOrg = organizationRepo.findById(id);
        if (optOrg.isPresent()) {
            Organization org = optOrg.get();
            org.setValidated(validated);
            org.setStatus(newStatus);
            organizationRepo.save(org);
            return true;
        }
        return false;
    }

    /**
     * Delete an organization.
     */
    public boolean delete(Integer id) {
        if (organizationRepo.existsById(id)) {
            organizationRepo.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Map Organization entity to OrganizationDTO.
     */

    public Organization registerOrganization(OrganizationDTO org) {
        Organization organization = mapper.mapOrganizationDtoToOrganization(org);
        return organizationRepo.save(organization);
    }


    // dashboard
    public long getTotalCharityProgramsCreartedByOrganization(int id) {
        long totalCharityPrograms = 0;
        totalCharityPrograms = charityActionService.getCharityActionsByOrganizationId(id).stream().count();
        return totalCharityPrograms;
    }

    public long getTotalDonationsByOrganization(int id) {
        long totalDonations = 0;
        totalDonations = charityActionService.getCharityActionsByOrganizationId(id).stream()
                .mapToInt(charityAction -> charityAction.getDonations().size())
                .sum();
        return totalDonations;
    }

    public double getTotalMoneyRaisedByOrganization(int id) {
        double totalMoneyRaised = 0;
        totalMoneyRaised = charityActionService.getCharityActionsByOrganizationId(id).stream()
                .mapToDouble(charityAction -> (double) charityAction.getCollectedAmount())
                .sum();
        return totalMoneyRaised;
    }

    // totalProgramsWithAchievedGoals
    public int getTotalProgramsWithAchievedGoals(int id) {
        int totalProgramsWithAchievedGoals = 0;
        totalProgramsWithAchievedGoals = charityActionService.getCharityActionsByOrganizationId(id).stream()
                .filter(charityAction -> charityAction.getCollectedAmount() >= charityAction.getObjectiveAmount())
                .mapToInt(charityAction -> 1)
                .sum();
        return totalProgramsWithAchievedGoals;
    }

    public Organization updateOrganization(OrganizationDTO org) {
        Organization organization = mapper.mapOrganizationDtoToOrganization(org);

        // Find existing organization by email
        Optional<Organization> existingOrgOptional = organizationRepo.findByEmail(organization.getEmail());
        if (existingOrgOptional.isEmpty()) {
            throw new IllegalArgumentException("Organization not found");
        }

        Organization existingOrg = existingOrgOptional.get();

        // Update fields only if changed
        if (!Objects.equals(existingOrg.getName(), organization.getName())) {
            existingOrg.setName(organization.getName());
        }

        if (!Objects.equals(existingOrg.getLegalAddress(), organization.getLegalAddress())) {
            existingOrg.setLegalAddress(organization.getLegalAddress());
        }

        if (!Objects.equals(existingOrg.getHtmlContent(), organization.getHtmlContent())) {
            existingOrg.setHtmlContent(organization.getHtmlContent());
            log.info("HTML content updated for organization: {}", organization.getHtmlContent());
        }

        // Update media map
        for (Map.Entry<String, String> entry : organization.getMedia().entrySet()) {
            String key = entry.getKey();
            String newValue = entry.getValue();

            if (existingOrg.getMedia().containsKey(key)) {
                String oldValue = existingOrg.getMedia().get(key);
                if (!Objects.equals(oldValue, newValue)) {
                    // Delete old file (if applicable)
                    File oldMediaFile = new File(oldValue);
                    if (oldMediaFile.exists()) {
                        oldMediaFile.delete();
                    }

                    // Replace with new
                    existingOrg.getMedia().put(key, newValue);
                }
            } else {
                // New key, just add it
                existingOrg.getMedia().put(key, newValue);
            }
        }

        return organizationRepo.save(existingOrg);
    }

}
