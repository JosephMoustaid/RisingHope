package spring.charityapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spring.charityapp.common.ActionState;
import spring.charityapp.common.Category;
import spring.charityapp.dto.CharityActionDTO;
import spring.charityapp.dto.OrganizationDTO;
import spring.charityapp.dto.TransactionDTO;
import spring.charityapp.model.CharityAction;
import spring.charityapp.model.Organization;
import spring.charityapp.model.Transaction;
import spring.charityapp.repository.CharityActionRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import spring.charityapp.util.Mapper;

import spring.charityapp.repository.*;


import java.util.ArrayList;
import java.util.Objects;
// pagination
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
// Predicate
import jakarta.persistence.criteria.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CharityActionService {

    private final CharityActionRepository charityActionRepo;
    private final OrganizationRepository organizationRepository;
    private final FileStorageService fileStorageService;
    private final Mapper mapper = new Mapper();

    /**
     * Get all charity actions mapped to CharityActionDTO.
     */
    public List<CharityActionDTO> getAllCharityActions() {
        return charityActionRepo.findAll()
                .stream()
                .map( charityAction -> mapper.mapCharityActionToCharityActionDTO(charityAction) )
                .collect(Collectors.toList());
    }

    public List<CharityActionDTO> get6CharityActions(){
        return charityActionRepo.findTop6ByOrderByStartDateDesc().
                stream()
                .map(charityAction -> mapper.mapCharityActionToCharityActionDTO(charityAction))
                .collect(Collectors.toList());
    }
    /**
     * Get a single charity action by ID.
     */
    public Optional<CharityActionDTO> getCharityActionById(int id) {
        return charityActionRepo.findById(id).map(charityAction -> mapper.mapCharityActionToCharityActionDTO(charityAction));
    }

    /**
     * Get all charity actions by organization ID.
     */
    public List<CharityActionDTO> getCharityActionsByOrganizationId(int organizationId) {
        return charityActionRepo.findByOrganization_Id(organizationId)
                .stream()
                .map(charityAction -> mapper.mapCharityActionToCharityActionDTO(charityAction))
                .collect(Collectors.toList());
    }


    public Optional<CharityActionDTO> createCharityAction(CharityActionDTO charityActionDTO,
                                                              MultipartFile[] files,
                                                              OrganizationDTO organizationDTO) {
            // 1. Fetch the existing managed Organization entity
            Organization org = organizationRepository.findById(organizationDTO.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Organization not found with id: " + organizationDTO.getId()));

            // 2. Create new CharityAction
            CharityAction charityAction = new CharityAction();

            // Map fields from DTO (excluding organization)
            charityAction.setTitle(charityActionDTO.getTitle());
            charityAction.setDescription(charityActionDTO.getDescription());
            charityAction.setStartDate(charityActionDTO.getStartDate());
            charityAction.setEndDate(charityActionDTO.getEndDate());
            charityAction.setObjectiveAmount(charityActionDTO.getObjectiveAmount());
            charityAction.setCollectedAmount(0.0F);
            charityAction.setActionState(ActionState.GOING);
            charityAction.setCategory(Category.valueOf(charityActionDTO.getCategory().toUpperCase()));
            // Set the managed organization (already exists in DB)
            charityAction.setOrganization(org);
            charityAction.setLocation(charityActionDTO.getLocation());
            // 3. Process media files
            Map<String, String> mediaPaths = new HashMap<>();
            if (files != null) {
                int imageCount = 0;
                for (MultipartFile image : files) {
                    if (!image.isEmpty()) {
                        String originalFilename = image.getOriginalFilename();
                        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        String safeOrgName = org.getName().replaceAll("[^a-zA-Z0-9]", "_");
                        String charityActionName = charityAction.getTitle().replaceAll("[^a-zA-Z0-9]", "_");
                        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                        String customFileName = safeOrgName + "_" + charityActionName + "_image" + imageCount + "_" + timestamp + extension;

                        String filePath = fileStorageService.storeFile(image, customFileName);
                        mediaPaths.put("image_" + imageCount++, "/uploads/" + filePath);
                    }
                }
            }
            charityAction.setMedias(mediaPaths);
            charityAction.setDonations(null);
            // 4. Save only the charity action
            log.info("Saving new charity action for organization ID: {}", org.getId());
            CharityAction savedAction = charityActionRepo.save(charityAction);

            return Optional.of(mapper.mapCharityActionToCharityActionDTO(savedAction));
    }



    public Page<CharityActionDTO> getFilteredCharityActions(String state, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<CharityAction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (state != null && !state.isEmpty()) {
                predicates.add(cb.equal(root.get("actionState"), ActionState.valueOf(state)));
            }

            if (category != null && !category.isEmpty()) {
                predicates.add(cb.equal(root.get("category"), Category.valueOf(category)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<CharityAction> pageResult = charityActionRepo.findAll(spec, pageable);
        return pageResult.map(mapper::mapCharityActionToCharityActionDTO);
    }

    public Optional<OrganizationDTO> getOrganizationByCharityActionId(int charityActionId) {
        CharityAction charityAction = charityActionRepo.findById(charityActionId)
                .orElseThrow(() -> new IllegalArgumentException("Charity action not found with id: " + charityActionId));
        return Optional.of(mapper.mapOrganizationToOrganiationDTO(charityAction.getOrganization()));
    }

}