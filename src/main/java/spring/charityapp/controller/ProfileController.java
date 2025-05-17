package spring.charityapp.controller;


import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring.charityapp.common.Role;
import spring.charityapp.dto.*;
import spring.charityapp.model.Transaction;
import spring.charityapp.service.*;
import org.springframework.ui.Model;

import jakarta.validation.Valid;;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.web.multipart.MultipartFile;
@Slf4j
@RequestMapping("")
@Controller
public class ProfileController {

    private final UserService userService;
    private final CharityActionService charityActionService;
    private final TransactionService transactionService;
    private final OrganizationService organizationService;


    private final String uploadDir = "/home/youssefmoustaid/Desktop/my-projects/Charity App/charityapp/src/main/resources/static/uploads";

    private final FileStorageService fileStorageService = new FileStorageService(uploadDir);


    public ProfileController(UserService userService ,
                             CharityActionService charityActionService,
                             TransactionService transactionService,
                             OrganizationService organizationService) {
        this.userService = userService;
        this.charityActionService = charityActionService;
        this.transactionService = transactionService;
        this.organizationService = organizationService;
    }
    @GetMapping("/profile")
    public String showProfile(
            HttpSession session,
            Model model) {
        Object roleObj = session.getAttribute("role");
        if (roleObj == null) {
            return "redirect:/home";
        }

        String role = roleObj.toString();
        int id = (int) session.getAttribute("id");

        switch (role) {
            case "Organization" -> {
                OrganizationDTO organization = organizationService.getById(id).orElse(null);
                if (organization == null) return "redirect:/home";

                OrgProfileDTO orgProfileDTO = new OrgProfileDTO();
                orgProfileDTO.setOrganization(organization);
                orgProfileDTO.setTotalCharityActions(organizationService.getTotalCharityProgramsCreartedByOrganization(id));
                orgProfileDTO.setTotalDonations(organizationService.getTotalDonationsByOrganization(id));
                orgProfileDTO.setTotalMoneyRaised(organizationService.getTotalMoneyRaisedByOrganization(id));
                orgProfileDTO.setTotalProgramsWithAchievedGoals(organizationService.getTotalProgramsWithAchievedGoals(id));
                List<CharityActionDTO> charityActions = charityActionService.getCharityActionsByOrganizationId(id);
                orgProfileDTO.setCharityActions(charityActions);

                model.addAttribute("orgProfile", orgProfileDTO);
                // model.addAttribute("organization", organization);

                // model.addAttribute(("totalCharityActions"), organizationService.getTotalCharityProgramsCreartedByOrganization(id)) ;
                // model.addAttribute(("totalDonations"), organizationService.getTotalDonationsByOrganization(id ));
                // model.addAttribute(("totalMoneyRaised"), organizationService.getTotalMoneyRaisedByOrganization(id ));

                // Fetch charity actions posted by the org -> and their corresponding details(stats - all trabsactons ...
                // model.addAttribute("charityActions", charityActions);

                return "organizationProfile";
            }
            case "User" -> {
                UserDTO user = userService.getUserById(id).orElse(null);
                if (user == null) return "redirect:/home";
                model.addAttribute("user", user);

                List<TransactionDTO> transactions = transactionService.getTransactionsByUser(id);
                model.addAttribute("transactions", transactions);

                float totalAmount = 0;
                for (TransactionDTO t : transactions) totalAmount += t.getAmount();
                model.addAttribute("totalAllTransactions", totalAmount);

                return "userProfile";
            }
            default -> {
                return "redirect:/home"; // Unrecognized role
            }
        }

    }

    @GetMapping("/profile/edit")
    public String editProfile(
            HttpSession session,
            Model model) {
        Object roleObj = session.getAttribute("role");
        if (roleObj == null) {
            return "redirect:/home";
        }

        String role = roleObj.toString();
        int id = (int) session.getAttribute("id");

        switch (role) {
            case "Organization" -> {
                OrganizationDTO organization = organizationService.getById(id).orElse(null);
                if (organization == null) return "redirect:/home";

                model.addAttribute("organization", organization);
                return "editOrganization";
            }
            case "User" -> {
                UserDTO user = userService.getUserById(id).orElse(null);
                if (user == null) return "redirect:/home";

                model.addAttribute("user", user);
                return "editUserProfile";
            }
            default -> {
                return "redirect:/home"; // Unrecognized role
            }
        }
    }
    @PostMapping("/organization/update")
    public String updateOrganization(
            @Valid @ModelAttribute("organization") OrganizationDTO request,
            BindingResult bindingResult,
            Model model) {

        log.info("Updating organization: {}", request);

        if (bindingResult.hasErrors()) {
            log.error("Validation errors during update: {}", bindingResult.getAllErrors());
            return "editOrganization"; // your edit form page
        }

        // Optional: Check if the passwords should be updated and match
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "error.organization", "Passwords do not match");
                return "editOrganization"; // your edit form page
            }
        }

        // Validate images (optional, same logic as registration)
        if (request.getImages() != null && request.getImages().size() > 3) {
            model.addAttribute("error", "Maximum of 3 images allowed");
            return "editOrganization";
        }

        Map<String, String> mediaPaths = new HashMap<>();
        int imageCount = 0;
        for (MultipartFile image : request.getImages()) {
            if (!image.isEmpty()) {
                if (!image.getContentType().startsWith("image/")) {
                    model.addAttribute("error", "Only image files allowed");
                    return "editOrganization";
                }
                String originalFilename = image.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String safeOrgName = request.getName().replaceAll("[^a-zA-Z0-9]", "_");
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String customFileName = safeOrgName + "_image" + imageCount + "_" + timestamp + extension;

                String filePath = fileStorageService.storeFile(image, customFileName);
                String mediaPath = "/uploads/" + filePath;
                mediaPaths.put("image_" + imageCount++, mediaPath);
            }
        }
        request.setMedia(mediaPaths);

        // 🔁 Update and save the organization
        organizationService.updateOrganization(request);

        model.addAttribute("success", "Organization updated successfully!");
        return "redirect:/profile"; // redirect to profile page or confirmation
    }

}
