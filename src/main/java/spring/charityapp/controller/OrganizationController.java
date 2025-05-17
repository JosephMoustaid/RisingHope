package spring.charityapp.controller;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.rule.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.charityapp.dto.BankInfoDTO;
import spring.charityapp.dto.CharityActionDTO;
import spring.charityapp.dto.OrganizationDTO;
import spring.charityapp.model.Organization;
import spring.charityapp.service.CharityActionService;
import spring.charityapp.service.OrganizationService;

@Slf4j
@Controller
@RequestMapping("")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final CharityActionService charityActionService;

    public OrganizationController(OrganizationService organizationService,
                               CharityActionService charityActionService) {
        this.organizationService = organizationService;
        this.charityActionService = charityActionService;
    }


    @GetMapping("/charity/create")
    public String createCharityAction(
            HttpSession session,
            Model model
    ) {
        CharityActionDTO charityAction = new CharityActionDTO();
        model.addAttribute("charityAction", charityAction);
        return "create-charity-action";
    }

    @PostMapping("/charity/create")
    public String createCharityAction(
            @Valid @ModelAttribute("charityAction") CharityActionDTO request,
            BindingResult bindingResult,
            @RequestParam("mediaFiles") MultipartFile[] mediaFiles,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (session.getAttribute("role") == null ) {
            return "redirect:/home";
        }
        if(session.getAttribute("role") != null ) {
            String role = session.getAttribute("role").toString();
            if(! role.toLowerCase().equals("organization")) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access");
                log.error("Unauthorized access attempt to create charity action,, youn are currently logged as {}", session.getAttribute("role"));
                return "redirect:/home";
            }
        }
        // set the organization ID from the session
        int organizationId = (int) session.getAttribute("id");
        OrganizationDTO org = organizationService.getById(organizationId).orElse(null);
        if(org == null) {
            model.addAttribute("error", "Organization not found");
            return "redirect:/home";
        }

        if (bindingResult.hasErrors()) {
            if(request.getMedia().isEmpty()) {
                model.addAttribute("error", "Please upload at least one media file");
            }
            if(request.getDescription() == null || request.getDescription().isBlank() ){
                model.addAttribute("error", "Please provide a description");
            }
            if(request.getBankInfo().getBankName() == null || request.getBankInfo().getBankName().isBlank()) {
                model.addAttribute("error", "Please provide a bank name");
            }
            if(request.getBankInfo().getIban() == null || request.getBankInfo().getIban().isBlank()) {
                model.addAttribute("error", "Please provide an valid IBAN");
            }
            if(request.getBankInfo().getAccountHolderName() == null || request.getBankInfo().getAccountHolderName().isBlank()) {
                model.addAttribute("error", "Please provide the account holder name");
            }
            if(request.getBankInfo().getSwiftCode() == null || request.getBankInfo().getSwiftCode().isBlank()) {
                model.addAttribute("error", "Please provide the SWIFT code");
            }
            model.addAttribute("error", "Please correct the errors in the form");
            log.error("Validation errors during charity action creation: {}", bindingResult.getAllErrors());
            return "create-charity-action";
        }

        try {
            log.info("trying to Create charity action: {}", request.toString());

            // Process and save the charity action
            charityActionService.createCharityAction(request, mediaFiles, org);

            redirectAttributes.addFlashAttribute("success", "Charity program created successfully!");
            return "redirect:/profile";

        } catch (Exception e) {
            model.addAttribute("error", "Error creating charity program: " + e.getMessage());
            log.error("Error creating charity program: {}", e.getMessage());
            return "create-charity-action";
        }
    }
}
