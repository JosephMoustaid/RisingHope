package spring.charityapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.charityapp.common.Role;
import spring.charityapp.dto.*;
import spring.charityapp.dto.ResetPasswordRequestDTO;
import spring.charityapp.dto.UserDTO;
import spring.charityapp.model.Organization;
import spring.charityapp.model.SuperAdmin;
import spring.charityapp.model.User;
import spring.charityapp.service.*;
import spring.charityapp.util.JwtUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.web.bind.annotation.ModelAttribute;

// ResponseEntity
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

// IOException
import java.io.IOException;

// for sessions
import jakarta.servlet.http.HttpSession;
import spring.charityapp.util.Mapper;


import java.util.HashMap;
import java.util.Map;

//@Validated
@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AdminService superadminService;
    private final OrganizationService organizationService;
    private final UserService userService;
    private Mapper mapper = new Mapper();
    private final String uploadDir = "/home/youssefmoustaid/Desktop/my-projects/Charity App/charityapp/src/main/resources/static/uploads";


    private final FileStorageService fileStorageService = new FileStorageService(uploadDir);

    public AuthController(AuthService authService, OrganizationService organizationService, UserService userService, AdminService adminService) {
        this.authService = authService;
        this.superadminService = adminService;
        this.userService = userService;
        this.organizationService = organizationService;
    }


    @GetMapping("/logout")
    public String logout(
            Model model,
            HttpSession session) {
        model.addAttribute("connected", session.getAttribute("role") != null);
        session.invalidate(); // Invalidate the session
        return "redirect:/home"; // Redirect to home page after logout
    }
    @GetMapping("/login")
    public String chooseRole(
            Model model,
            HttpSession session) {

        if (session.getAttribute("role") != null) {
            model.addAttribute("connected", session.getAttribute("role") != null);

            return "redirect:/home";
        }
        model.addAttribute("connected", session.getAttribute("role") != null);
        return "chooseRole";
    }


    // 1. Admin login page (GET)
    @GetMapping("/login/admin")
    public String adminLoginPage(Model model, HttpSession session) {
        if (session.getAttribute("role") != null) {
            return "redirect:/home";
        }
        model.addAttribute("loginForm", new LoginDTO()); // empty object for form binding
        return "userLogin"; // Your admin login page
    }

    // 2. Admin login (POST)
    @PostMapping("/login/admin")
    public String adminLoginPost(@ModelAttribute("admin") LoginDTO loginForm,
                                 Model model,
                                 HttpSession session) {
        SuperAdmin admin = authService.authentificateAdmin(loginForm.getEmail(), loginForm.getPassword());
        if (admin == null) {
            model.addAttribute("error", "Invalid admin credentials.");
            return "adminLogin"; // Make sure you have adminLogin.html
        }

        Map<String, String> tokens = JwtUtil.generateAdminTokens(admin.getId());
        session.setAttribute("accessToken", tokens.get("access_token"));
        session.setAttribute("refreshToken", tokens.get("refresh_token"));
        session.setAttribute("role", Role.Admin);
        session.setAttribute("id", admin.getId());
        session.setAttribute("email", admin.getEmail());

        return "redirect:/admin/dashboard";
    }


    // 3. Organization login page (GET)
    @GetMapping("/login/organization")
    public String organizationLoginPage(Model model, HttpSession session) {
        if (session.getAttribute("role") != null) {
            return "redirect:/home";
        }
        model.addAttribute("loginForm", new LoginDTO()); // empty object for form binding
        return "organizationLogin"; // Your organization login page
    }

    // 4. Organization login (POST)
    @PostMapping("/login/organization")
    public String organizationLoginPost(@Valid @ModelAttribute("loginForm") LoginDTO loginForm,
                                        BindingResult bindingResult,
                                        Model model,
                                        HttpSession session) {
        if(bindingResult.hasErrors()) {
            log.error("Validation errors: {}", bindingResult.getAllErrors());
            return "organizationLogin";
        }

        Organization organization = authService.authentificateOrganization(loginForm.getEmail(), loginForm.getPassword());
        if (organization == null || !organization.getValidated()) {
            model.addAttribute("error", "Invalid credentials or organization not validated by admin yet.");
            return "organizationLogin";
        }

        Map<String, String> tokens = JwtUtil.generateTokens(organization.getId(), Role.Organization);
        session.setAttribute("accessToken", tokens.get("access_token"));
        session.setAttribute("refreshToken", tokens.get("refresh_token"));
        session.setAttribute("role", Role.Organization);
        session.setAttribute("id", organization.getId());
        session.setAttribute("email", organization.getEmail());
        model.addAttribute("connected", session.getAttribute("role") != null);

        return "redirect:/home";
    }


    // 5. User login page (GET)
    @GetMapping("/login/user")
    public String userLoginPage(Model model, HttpSession session) {
        if (session.getAttribute("role") != null) {
            return "redirect:/home";
        }
        model.addAttribute("loginForm", new LoginDTO()); // empty object for form binding
        return "userLogin"; // Your user login page
    }

    // Updated User Login Post Method
    @PostMapping("/login/user")
    public String userLoginPost(@Valid @ModelAttribute("loginForm") LoginDTO userForm,
                                BindingResult bindingResult,
                                Model model,
                                HttpSession session) {

        // Check for validation errors first
        if (bindingResult.hasErrors()) {
            log.error("Validation errors: {}", bindingResult.getAllErrors());
            return "userLogin";
        }

        User user = authService.authentificateUser(userForm.getEmail(), userForm.getPassword());
        if (user == null) {
            model.addAttribute("error", "Invalid email or password");
            bindingResult.rejectValue("password", "<PASSWORD>", "Invalid email or password");
            return "userLogin";
        }

        Map<String, String> tokens = JwtUtil.generateTokens(user.getId(), Role.User);
        session.setAttribute("accessToken", tokens.get("access_token"));
        session.setAttribute("refreshToken", tokens.get("refresh_token"));
        session.setAttribute("role", Role.User);
        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        model.addAttribute("connected", true);

        return "redirect:/home";
    }

    // Updated User Registration Method
    @PostMapping("/user/register")
    public String userRegister(
            @Valid @ModelAttribute("user") UserRegisterDTO request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Check validation errors
        if (bindingResult.hasErrors()) {
            log.error("Validation errors: {}", bindingResult.getAllErrors());
            return "userRegister";
        }

        // Check password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Passwords do not match");
            return "userRegister";
        }

        // Check password strength
        if (!isValidPassword(request.getPassword())) {
            bindingResult.rejectValue("password", "error.user", "Password must be at least 8 characters with uppercase, number, and special character");
            return "userRegister";
        }

        try {
            log.info("Registering user: {} with password > {}", request.getEmail() , request.getPassword());
            User user = userService.addUser(mapper.mapUserRegisterDTOToUser( request ) );
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/auth/login/user";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "userRegister";
        }
    }
    @GetMapping("/register/user")
    public String userRegisterPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "success", required = false) String success,
            Model model ) {

        model.addAttribute("user", new UserRegisterDTO());

        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        return "userRegister";
    }

    @GetMapping("/register/organization")
    public String orgRegisterPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "success", required = false) String success,
            Model model ) {

        model.addAttribute("organization", new OrganizationDTO());

        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        return "organizationRegister";
    }

    @PostMapping("/organization/register")
    public String orgRegister(
            @Valid @ModelAttribute("organization") OrganizationDTO request,
            BindingResult bindingResult,    // <--- immediately after @ModelAttribute
            Model model ) {


        log.info("Registering organization: {}", request);

        log.info("Binding organization class: {}", request.getClass());
        log.info("Binding result: {}", bindingResult);
        log.info("Binding result class: {}", bindingResult.getClass());
        log.info("Binding result has errors : {}", bindingResult.hasErrors());
        log.info("Binding result has field errors : {}", bindingResult.hasFieldErrors());

        log.info("html content  : {}", request.getHtmlContent());
         // ------------------------
        // Check validation FIRST
        if (bindingResult.hasErrors()) {
            log.error("Validation errors: {}", bindingResult.getAllErrors());
            return "organizationRegister";
        }

        // Then check passwords
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.organization", "Passwords do not match");
            return "organizationRegister";
        }
        // ------------------------
        // Validate images
        if (request.getImages() == null || request.getImages().size() > 3) {
            model.addAttribute("error", "Maximum of 5 images allowed");
            return "organizationRegister";  // Forward to the registration form with error
        }

        // Process images
        Map<String, String> mediaPaths = new HashMap<>();
        int imageCount = 0;

        for (MultipartFile image : request.getImages()) {
            if (!image.isEmpty()) {
                if (!image.getContentType().startsWith("image/")) {
                    model.addAttribute("error", "Only image files allowed");
                    return "organizationRegister";  // Forward to the registration form with error
                }
                // Store the image with as : org_title + "_image" + imageCount + date + time + extenssion
                String originalFilename = image.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String safeOrgName = request.getName().replaceAll("[^a-zA-Z0-9]", "_"); // Sanitize org name
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String customFileName = safeOrgName + "_image" + imageCount + "_" + timestamp + extension;

                String filePath = fileStorageService.storeFile(image, customFileName);

                String mediaPath = "/uploads/" + filePath;
                mediaPaths.put("image_" + imageCount++, mediaPath);
            }
        }

        // Set the media paths
        request.setMedia(mediaPaths);

        // Save organization
        Organization organization = organizationService.registerOrganization(request);

        // Redirect with success
        model.addAttribute("success", "Registration successful!");
        return "redirect:/auth/login/organization";  // Redirect after successful registration
    }



    @PostMapping("/user/reset-password")
    public String resetUserPassword(@ModelAttribute ResetPasswordRequestDTO request, Model model) {
        String email = request.email();
        String oldPassword = request.oldPassword();
        String newPassword = request.newPassword();

        User user = userService.getUserByEmail(email);
        if (user == null || !Bcrypt.checkPassword(oldPassword, user.getPassword()) || !isValidPassword(newPassword)) {
            model.addAttribute("error", "Invalid credentials or new password is too weak.");
            return "resetPasswordForm"; // Show error page/form
        }

        userService.updateUserPassword(user.getId(), newPassword);
        model.addAttribute("message", "Password updated successfully.");
        return "redirect:/home"; // Redirect after success
    }

    @PostMapping("/org/reset-password")
    public String resetOrganizationPassword(@ModelAttribute ResetPasswordRequestDTO request, Model model) {
        String email = request.email();
        String oldPassword = request.oldPassword();
        String newPassword = request.newPassword();

        Organization org = organizationService.getOrganiozationByEmail(email).orElseThrow(
                () -> new RuntimeException("Organization not found")
        );

        if (!org.getValidated()) {
            model.addAttribute("error", "Organization has not been validated yet.");
            return "resetPasswordForm";
        }

        if (!Bcrypt.checkPassword(oldPassword, org.getPassword()) || !isValidPassword(newPassword)) {
            model.addAttribute("error", "Invalid credentials or new password is too weak.");
            return "resetPasswordForm";
        }

        userService.updateUserPassword(org.getId(), newPassword);
        model.addAttribute("message", "Password updated successfully.");
        return "redirect:/home";
    }

    private boolean isValidPassword(String password) {
        // Check for null or minimum length
        if (password == null || password.length() < 8) {
            return false;
        }

        // Check for at least one uppercase letter
        boolean hasUppercase = false;
        // Check for at least one lowercase letter
        boolean hasLowercase = false;
        // Track character variety (to ensure not all characters are the same)
        char firstChar = password.charAt(0);
        boolean allSameChar = true;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            }
            if (Character.isLowerCase(c)) {
                hasLowercase = true;
            }
            if (c != firstChar) {
                allSameChar = false;
            }
        }

        // Password is valid if:
        // 1. Has at least one uppercase and one lowercase letter
        // 2. Not all characters are the same
        // 3. Is at least 8 characters long (checked earlier)
        return hasUppercase && hasLowercase && !allSameChar;
    }


}


