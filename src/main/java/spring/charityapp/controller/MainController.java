package spring.charityapp.controller;


import jakarta.servlet.http.HttpSession;
import org.joda.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spring.charityapp.common.ActionState;
import spring.charityapp.common.Category;
import spring.charityapp.common.PaymentMethod;
import spring.charityapp.dto.CharityActionDTO;
import spring.charityapp.dto.OrganizationDTO;
import spring.charityapp.dto.TransactionDTO;
import spring.charityapp.dto.UserDTO;
import spring.charityapp.service.CharityActionService;
import spring.charityapp.service.OrganizationService;
import spring.charityapp.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

@Controller
@RequestMapping("")
public class MainController {


    // services
    private final CharityActionService charityActionService;
    private  final OrganizationService organizationService;
    private final UserService userService;

    public MainController(CharityActionService charityActionService,
                          OrganizationService organizationService,
                          UserService userService) {
        this.charityActionService = charityActionService;
        this.organizationService = organizationService;
        this.userService = userService;
    }
    @GetMapping("")
    public String index(Model model) {
        return "redirect:/home";
    }
    @GetMapping("/home")
    public String home(Model model,
                       HttpSession session) {

        model.addAttribute("connected", false); // or true based on your logic
        // check if the user is connected
        if (session.getAttribute("role") != null) {
            model.addAttribute("connected", true);
        }

        // fetch charity actions to display on the home page
        List<CharityActionDTO> charityActions = charityActionService.get6CharityActions();

        // Fetch organizations and users to display on the home page
        List<OrganizationDTO> organizations = organizationService.findTop6();

        for (OrganizationDTO org : organizations) {
            // fetch only the first 100 characters form the html content
            if (org.getHtmlContent() != null) {
                String htmlContent = org.getHtmlContent();
                org.setHtmlContent(htmlContent.substring(0, 100) + "...");
            }else {
                org.setHtmlContent("No description available.");
            }
        }
        model.addAttribute("organizations", organizations);

        // Example donations
        List<TransactionDTO> foodDonations = List.of(
                new TransactionDTO(1, new Date(), LocalTime.now(), 1, 101, 100f, PaymentMethod.STRIPE, ActionState.GOING, "REF123FOOD"),
                new TransactionDTO(2, new Date(), LocalTime.now(), 1, 102, 200f, PaymentMethod.PAYPAL, ActionState.GOING, "REF124FOOD")
        );

        List<TransactionDTO> educationDonations = List.of(
                new TransactionDTO(3, new Date(), LocalTime.now(), 2, 103, 300f, PaymentMethod.STRIPE, ActionState.GOING, "REF125EDU")
        );

        List<TransactionDTO> medicalDonations = List.of(
                new TransactionDTO(4, new Date(), LocalTime.now(), 3, 104, 400f, PaymentMethod.STRIPE, ActionState.GOING, "REF126MED")
        );

        // Add CharityActions with their corresponding donations
        model.addAttribute("connected", session.getAttribute("role") != null);

        model.addAttribute("charityActions", charityActions);

        // Optionally, fetch and pass user data here
        List<UserDTO> users = userService.getAllUsers();  // Assuming you have a UserDTO for mapping
        model.addAttribute("users", users);

        return "home";
    }


    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

    @GetMapping("/faq")
    public String faq(Model model) {
        return "faq";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        return "contact";
    }

    @GetMapping("/charities")
    public String charities(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model) {

        Page<CharityActionDTO> charityActions = charityActionService.getFilteredCharityActions(state, category, page, size);

        // Add filter options
        List<String> charityCategories = Arrays.stream(Category.values()).map(Enum::toString).toList();
        List<String> states = Arrays.stream(ActionState.values()).map(Enum::toString).toList();

        // Add attributes for view
        model.addAttribute("charityActions", charityActions.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", charityActions.getTotalPages());
        model.addAttribute("state", state);
        model.addAttribute("category", category);
        model.addAttribute("categories", charityCategories);
        model.addAttribute("states", states);
        model.addAttribute("title", "Charity Actions");
        model.addAttribute("subtitle", "Explore our charity actions and make a difference.");

        return "charities";
    }

    @GetMapping("/charity/{id}")
    public String charity(Model model,
                          @PathVariable("id") int id) {
        // Fetch charity action details based on the ID

        CharityActionDTO charityAction = charityActionService.getCharityActionById(id)
                .orElse(null);
        OrganizationDTO organization = charityActionService.getOrganizationByCharityActionId(id).orElse(null);
        if (charityAction == null) {
            return "redirect:/home"; // Redirect if charity action not found
        }

        model.addAttribute("charityAction", charityAction);
        model.addAttribute("title", "Charity Action");
        model.addAttribute("organization", organization);
        model.addAttribute("donations", charityAction.getDonations()
                .stream()
                .limit(10)
                .collect(java.util.stream.Collectors.toList()));

        return "charityprogram";
    }

    @GetMapping("/categories")
    public String categoriesPage(Model model) {
        List<String> categories = Arrays.stream(Category.values()).map(Enum::name).toList();
        model.addAttribute("categories", categories);
        model.addAttribute("title", "Explore Categories");
        return "categories";
    }

    @GetMapping("/organization/{id}")
    public String organization(Model model,
                               @PathVariable("id") int id) {
        OrganizationDTO organization = organizationService.getById(id).orElse(null);
        if (organization == null) {
            return "redirect:/home"; // Redirect if organization not found
        }
        model.addAttribute("organization", organization);
        return "organization";
    }
}
